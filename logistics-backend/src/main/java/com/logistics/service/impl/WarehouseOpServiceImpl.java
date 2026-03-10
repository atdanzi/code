package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.entity.*;
import com.logistics.mapper.*;
import com.logistics.service.WarehouseOpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WarehouseOpServiceImpl implements WarehouseOpService {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderItemMapper purchaseOrderItemMapper;
    private final TransferOrderMapper transferOrderMapper;
    private final TransferOrderItemMapper transferOrderItemMapper;
    private final TaskOrderMapper taskOrderMapper;
    private final TaskOrderItemMapper taskOrderItemMapper;
    private final ReturnOrderMapper returnOrderMapper;
    private final ReturnOrderItemMapper returnOrderItemMapper;
    private final InventoryMapper inventoryMapper;
    private final WarehouseFlowMapper warehouseFlowMapper;
    private final WarehouseMapper warehouseMapper;
    private final OrdersMapper ordersMapper;

    @Override
    @Transactional
    public void purchaseInbound(Long purchaseOrderId, Map<Long, Integer> actualQuantities,
                                LocalDate inboundDate, String remark, Long operatorId) {
        PurchaseOrder po = purchaseOrderMapper.selectById(purchaseOrderId);
        if (po == null) {
            throw new RuntimeException("采购单不存在");
        }

        Warehouse centerWarehouse = warehouseMapper.selectOne(
                new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getLevel, "CENTER").last("LIMIT 1"));
        if (centerWarehouse == null) {
            throw new RuntimeException("中心仓库不存在");
        }

        List<PurchaseOrderItem> items = purchaseOrderItemMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderItem>().eq(PurchaseOrderItem::getPurchaseOrderId, purchaseOrderId));

        for (PurchaseOrderItem item : items) {
            Integer actualQty = actualQuantities.getOrDefault(item.getProductId(), item.getOrderQuantity());
            item.setActualQuantity(actualQty);
            purchaseOrderItemMapper.updateById(item);

            updateInventory(centerWarehouse.getId(), item.getProductId(), actualQty, "IN");

            WarehouseFlow flow = new WarehouseFlow();
            flow.setWarehouseId(centerWarehouse.getId());
            flow.setProductId(item.getProductId());
            flow.setType("IN");
            flow.setSubType("PURCHASE");
            flow.setQuantity(actualQty);
            flow.setRelatedOrderNo(po.getPurchaseCode());
            flow.setOperatorId(operatorId);
            flow.setOperateTime(LocalDateTime.now());
            flow.setRemark(remark);
            warehouseFlowMapper.insert(flow);
        }

        po.setStatus("COMPLETED");
        purchaseOrderMapper.updateById(po);
    }

    @Override
    public IPage<TransferOrder> pageTransferOrders(Page<TransferOrder> page, String status,
                                                    LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<TransferOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), TransferOrder::getStatus, status)
               .ge(startDate != null, TransferOrder::getPlanDate, startDate)
               .le(endDate != null, TransferOrder::getPlanDate, endDate)
               .orderByDesc(TransferOrder::getCreateTime);
        return transferOrderMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void transferOutbound(List<Long> transferOrderIds, Long operatorId) {
        for (Long transferOrderId : transferOrderIds) {
            TransferOrder to = transferOrderMapper.selectById(transferOrderId);
            if (to == null || !"PENDING".equals(to.getStatus())) {
                continue;
            }

            List<TransferOrderItem> items = transferOrderItemMapper.selectList(
                    new LambdaQueryWrapper<TransferOrderItem>()
                            .eq(TransferOrderItem::getTransferOrderId, transferOrderId));

            for (TransferOrderItem item : items) {
                updateInventory(to.getFromWarehouseId(), item.getProductId(), -item.getQuantity(), "OUT");

                WarehouseFlow flow = new WarehouseFlow();
                flow.setWarehouseId(to.getFromWarehouseId());
                flow.setProductId(item.getProductId());
                flow.setType("OUT");
                flow.setSubType("TRANSFER");
                flow.setQuantity(item.getQuantity());
                flow.setRelatedOrderNo(to.getTransferCode());
                flow.setOperatorId(operatorId);
                flow.setOperateTime(LocalDateTime.now());
                warehouseFlowMapper.insert(flow);
            }

            to.setStatus("OUT_DONE");
            to.setActualOutDate(LocalDate.now());
            transferOrderMapper.updateById(to);
        }
    }

    @Override
    @Transactional
    public void transferInbound(Long transferOrderId, Map<Long, Integer> actualQuantities,
                                LocalDate inboundDate, String remark, Long operatorId) {
        TransferOrder to = transferOrderMapper.selectById(transferOrderId);
        if (to == null) {
            throw new RuntimeException("调拨单不存在");
        }

        List<TransferOrderItem> items = transferOrderItemMapper.selectList(
                new LambdaQueryWrapper<TransferOrderItem>()
                        .eq(TransferOrderItem::getTransferOrderId, transferOrderId));

        for (TransferOrderItem item : items) {
            int qty = actualQuantities.getOrDefault(item.getProductId(), item.getQuantity());
            updateInventory(to.getToWarehouseId(), item.getProductId(), qty, "IN");

            WarehouseFlow flow = new WarehouseFlow();
            flow.setWarehouseId(to.getToWarehouseId());
            flow.setProductId(item.getProductId());
            flow.setType("IN");
            flow.setSubType("TRANSFER");
            flow.setQuantity(qty);
            flow.setRelatedOrderNo(to.getTransferCode());
            flow.setOperatorId(operatorId);
            flow.setOperateTime(LocalDateTime.now());
            flow.setRemark(remark);
            warehouseFlowMapper.insert(flow);
        }

        to.setStatus("COMPLETED");
        to.setActualInDate(inboundDate);
        transferOrderMapper.updateById(to);
    }

    @Override
    @Transactional
    public void pickGoods(Long taskOrderId, String picker, LocalDate pickDate, Long operatorId) {
        TaskOrder task = taskOrderMapper.selectById(taskOrderId);
        if (task == null) {
            throw new RuntimeException("任务单不存在");
        }

        Warehouse stationWarehouse = warehouseMapper.selectOne(
                new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getStationId, task.getStationId()).last("LIMIT 1"));
        if (stationWarehouse == null) {
            throw new RuntimeException("站点仓库不存在");
        }

        List<TaskOrderItem> items = taskOrderItemMapper.selectList(
                new LambdaQueryWrapper<TaskOrderItem>().eq(TaskOrderItem::getTaskOrderId, taskOrderId));

        for (TaskOrderItem item : items) {
            Inventory inv = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>()
                    .eq(Inventory::getWarehouseId, stationWarehouse.getId())
                    .eq(Inventory::getProductId, item.getProductId()));
            if (inv != null) {
                inv.setAvailableQuantity(inv.getAvailableQuantity() - item.getQuantity());
                inv.setAllocatedQuantity(inv.getAllocatedQuantity() + item.getQuantity());
                inventoryMapper.updateById(inv);
            }

            WarehouseFlow flow = new WarehouseFlow();
            flow.setWarehouseId(stationWarehouse.getId());
            flow.setProductId(item.getProductId());
            flow.setType("OUT");
            flow.setSubType("PICK");
            flow.setQuantity(item.getQuantity());
            flow.setRelatedOrderNo(task.getTaskCode());
            flow.setOperatorId(operatorId);
            flow.setOperateTime(LocalDateTime.now());
            flow.setRemark("拣货人: " + picker);
            warehouseFlowMapper.insert(flow);
        }

        task.setTaskStatus("PICKED");
        task.setUpdateTime(LocalDateTime.now());
        taskOrderMapper.updateById(task);
    }

    @Override
    @Transactional
    public void returnRegister(Long taskOrderId, Long operatorId) {
        TaskOrder task = taskOrderMapper.selectById(taskOrderId);
        if (task == null) {
            throw new RuntimeException("任务单不存在");
        }

        Orders order = ordersMapper.selectById(task.getOrderId());
        if (order == null) {
            throw new RuntimeException("关联订单不存在");
        }

        Warehouse stationWarehouse = warehouseMapper.selectOne(
                new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getStationId, task.getStationId()).last("LIMIT 1"));

        List<TaskOrderItem> items = taskOrderItemMapper.selectList(
                new LambdaQueryWrapper<TaskOrderItem>().eq(TaskOrderItem::getTaskOrderId, taskOrderId));

        if (stationWarehouse != null) {
            for (TaskOrderItem item : items) {
                Inventory inv = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>()
                        .eq(Inventory::getWarehouseId, stationWarehouse.getId())
                        .eq(Inventory::getProductId, item.getProductId()));
                if (inv != null) {
                    inv.setReturnedQuantity(inv.getReturnedQuantity() + item.getQuantity());
                    inv.setTotalQuantity(inv.getTotalQuantity() + item.getQuantity());
                    inv.setAvailableQuantity(inv.getAvailableQuantity() + item.getQuantity());
                    inventoryMapper.updateById(inv);
                }

                WarehouseFlow flow = new WarehouseFlow();
                flow.setWarehouseId(stationWarehouse.getId());
                flow.setProductId(item.getProductId());
                flow.setType("IN");
                flow.setSubType("RETURN");
                flow.setQuantity(item.getQuantity());
                flow.setRelatedOrderNo(task.getTaskCode());
                flow.setOperatorId(operatorId);
                flow.setOperateTime(LocalDateTime.now());
                warehouseFlowMapper.insert(flow);
            }
        }

        order.setOrderStatus("RETURN_REGISTERED");
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);
    }

    @Override
    @Transactional
    public void stationReturnOut(List<Long> returnOrderItemIds, Long operatorId) {
        if (returnOrderItemIds == null || returnOrderItemIds.isEmpty()) return;

        ReturnOrderItem first = returnOrderItemMapper.selectById(returnOrderItemIds.get(0));
        if (first == null) return;
        ReturnOrder ro = returnOrderMapper.selectById(first.getReturnOrderId());
        if (ro == null) return;

        Warehouse fromWarehouse = warehouseMapper.selectById(ro.getFromWarehouseId());

        for (Long itemId : returnOrderItemIds) {
            ReturnOrderItem item = returnOrderItemMapper.selectById(itemId);
            if (item == null) continue;

            if (fromWarehouse != null) {
                updateInventory(fromWarehouse.getId(), item.getProductId(), -item.getQuantity(), "OUT");

                WarehouseFlow flow = new WarehouseFlow();
                flow.setWarehouseId(fromWarehouse.getId());
                flow.setProductId(item.getProductId());
                flow.setType("OUT");
                flow.setSubType("RETURN");
                flow.setQuantity(item.getQuantity());
                flow.setRelatedOrderNo(ro.getReturnCode());
                flow.setOperatorId(operatorId);
                flow.setOperateTime(LocalDateTime.now());
                warehouseFlowMapper.insert(flow);
            }
        }

        ro.setStatus("OUT_DONE");
        returnOrderMapper.updateById(ro);
    }

    @Override
    @Transactional
    public void centerReturnIn(Long returnOrderId, Long operatorId) {
        ReturnOrder ro = returnOrderMapper.selectById(returnOrderId);
        if (ro == null) {
            throw new RuntimeException("退货单不存在");
        }

        Long toWarehouseId = ro.getToWarehouseId();
        if (toWarehouseId == null) {
            Warehouse center = warehouseMapper.selectOne(
                    new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getLevel, "CENTER").last("LIMIT 1"));
            if (center == null) throw new RuntimeException("中心仓库不存在");
            toWarehouseId = center.getId();
        }

        List<ReturnOrderItem> items = returnOrderItemMapper.selectList(
                new LambdaQueryWrapper<ReturnOrderItem>().eq(ReturnOrderItem::getReturnOrderId, returnOrderId));

        for (ReturnOrderItem item : items) {
            updateInventory(toWarehouseId, item.getProductId(), item.getQuantity(), "IN");

            WarehouseFlow flow = new WarehouseFlow();
            flow.setWarehouseId(toWarehouseId);
            flow.setProductId(item.getProductId());
            flow.setType("IN");
            flow.setSubType("RETURN");
            flow.setQuantity(item.getQuantity());
            flow.setRelatedOrderNo(ro.getReturnCode());
            flow.setOperatorId(operatorId);
            flow.setOperateTime(LocalDateTime.now());
            warehouseFlowMapper.insert(flow);
        }

        ro.setStatus("COMPLETED");
        returnOrderMapper.updateById(ro);
    }

    private void updateInventory(Long warehouseId, Long productId, int quantityDelta, String direction) {
        Inventory inv = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getWarehouseId, warehouseId)
                .eq(Inventory::getProductId, productId));
        if (inv == null) {
            inv = new Inventory();
            inv.setWarehouseId(warehouseId);
            inv.setProductId(productId);
            inv.setTotalQuantity(Math.max(quantityDelta, 0));
            inv.setAvailableQuantity(Math.max(quantityDelta, 0));
            inv.setAllocatedQuantity(0);
            inv.setReturnedQuantity(0);
            inventoryMapper.insert(inv);
        } else {
            if ("IN".equals(direction)) {
                inv.setTotalQuantity(inv.getTotalQuantity() + Math.abs(quantityDelta));
                inv.setAvailableQuantity(inv.getAvailableQuantity() + Math.abs(quantityDelta));
            } else {
                inv.setTotalQuantity(inv.getTotalQuantity() - Math.abs(quantityDelta));
                inv.setAvailableQuantity(inv.getAvailableQuantity() - Math.abs(quantityDelta));
            }
            inventoryMapper.updateById(inv);
        }
    }
}
