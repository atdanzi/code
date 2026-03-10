package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.logistics.entity.*;
import com.logistics.mapper.*;
import com.logistics.service.WarehouseOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WarehouseOperationServiceImpl implements WarehouseOperationService {

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

    @Override
    @Transactional
    public void purchaseInbound(Long purchaseOrderId, Map<Long, Integer> actualQuantities, LocalDate inboundDate, String remark) {
        PurchaseOrder po = purchaseOrderMapper.selectById(purchaseOrderId);
        if (po == null) {
            throw new RuntimeException("采购单不存在");
        }

        Warehouse centerWarehouse = warehouseMapper.selectOne(
                new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getLevel, "CENTER").last("LIMIT 1"));
        Long warehouseId = centerWarehouse != null ? centerWarehouse.getId() : 1L;

        List<PurchaseOrderItem> items = purchaseOrderItemMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderItem>().eq(PurchaseOrderItem::getPurchaseOrderId, purchaseOrderId));

        for (PurchaseOrderItem item : items) {
            Integer actual = actualQuantities.getOrDefault(item.getProductId(), item.getOrderQuantity());
            item.setActualQuantity(actual);
            purchaseOrderItemMapper.updateById(item);

            updateInventory(warehouseId, item.getProductId(), actual, true);

            WarehouseFlow flow = new WarehouseFlow();
            flow.setWarehouseId(warehouseId);
            flow.setProductId(item.getProductId());
            flow.setType("IN");
            flow.setSubType("PURCHASE");
            flow.setQuantity(actual);
            flow.setRelatedOrderNo(po.getPurchaseCode());
            flow.setOperateTime(LocalDateTime.now());
            flow.setRemark(remark);
            warehouseFlowMapper.insert(flow);
        }

        po.setStatus("COMPLETED");
        purchaseOrderMapper.updateById(po);
    }

    @Override
    @Transactional
    public void transferOut(List<Long> transferOrderIds, Long operatorId) {
        for (Long transferOrderId : transferOrderIds) {
            TransferOrder to = transferOrderMapper.selectById(transferOrderId);
            if (to == null) continue;

            List<TransferOrderItem> items = transferOrderItemMapper.selectList(
                    new LambdaQueryWrapper<TransferOrderItem>().eq(TransferOrderItem::getTransferOrderId, transferOrderId));

            for (TransferOrderItem item : items) {
                updateInventory(to.getFromWarehouseId(), item.getProductId(), item.getQuantity(), false);

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

            to.setStatus("SHIPPED");
            to.setActualOutDate(LocalDate.now());
            to.setOperatorId(operatorId);
            transferOrderMapper.updateById(to);
        }
    }

    @Override
    @Transactional
    public void transferIn(Long transferOrderId, Map<Long, Integer> actualQuantities, LocalDate inboundDate, String remark) {
        TransferOrder to = transferOrderMapper.selectById(transferOrderId);
        if (to == null) {
            throw new RuntimeException("调拨单不存在");
        }

        List<TransferOrderItem> items = transferOrderItemMapper.selectList(
                new LambdaQueryWrapper<TransferOrderItem>().eq(TransferOrderItem::getTransferOrderId, transferOrderId));

        for (TransferOrderItem item : items) {
            Integer actual = actualQuantities.getOrDefault(item.getProductId(), item.getQuantity());
            updateInventory(to.getToWarehouseId(), item.getProductId(), actual, true);

            WarehouseFlow flow = new WarehouseFlow();
            flow.setWarehouseId(to.getToWarehouseId());
            flow.setProductId(item.getProductId());
            flow.setType("IN");
            flow.setSubType("TRANSFER");
            flow.setQuantity(actual);
            flow.setRelatedOrderNo(to.getTransferCode());
            flow.setOperateTime(LocalDateTime.now());
            flow.setRemark(remark);
            warehouseFlowMapper.insert(flow);
        }

        to.setStatus("RECEIVED");
        to.setActualInDate(inboundDate);
        transferOrderMapper.updateById(to);
    }

    @Override
    @Transactional
    public void pickGoods(Long taskOrderId, Long pickerId, LocalDate pickDate) {
        TaskOrder task = taskOrderMapper.selectById(taskOrderId);
        if (task == null) {
            throw new RuntimeException("任务单不存在");
        }

        Warehouse stationWarehouse = warehouseMapper.selectOne(
                new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getStationId, task.getStationId()).last("LIMIT 1"));
        Long warehouseId = stationWarehouse != null ? stationWarehouse.getId() : 1L;

        List<TaskOrderItem> items = taskOrderItemMapper.selectList(
                new LambdaQueryWrapper<TaskOrderItem>().eq(TaskOrderItem::getTaskOrderId, taskOrderId));

        for (TaskOrderItem item : items) {
            updateInventory(warehouseId, item.getProductId(), item.getQuantity(), false);

            WarehouseFlow flow = new WarehouseFlow();
            flow.setWarehouseId(warehouseId);
            flow.setProductId(item.getProductId());
            flow.setType("OUT");
            flow.setSubType("PICK");
            flow.setQuantity(item.getQuantity());
            flow.setRelatedOrderNo(task.getTaskCode());
            flow.setOperatorId(pickerId);
            flow.setOperateTime(LocalDateTime.now());
            warehouseFlowMapper.insert(flow);
        }

        task.setTaskStatus("PICKED");
        task.setUpdateTime(LocalDateTime.now());
        taskOrderMapper.updateById(task);
    }

    @Override
    @Transactional
    public void returnRegister(Long taskOrderId) {
        TaskOrder task = taskOrderMapper.selectById(taskOrderId);
        if (task == null) {
            throw new RuntimeException("任务单不存在");
        }

        Warehouse stationWarehouse = warehouseMapper.selectOne(
                new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getStationId, task.getStationId()).last("LIMIT 1"));
        Long warehouseId = stationWarehouse != null ? stationWarehouse.getId() : 1L;

        List<TaskOrderItem> items = taskOrderItemMapper.selectList(
                new LambdaQueryWrapper<TaskOrderItem>().eq(TaskOrderItem::getTaskOrderId, taskOrderId));

        for (TaskOrderItem item : items) {
            Inventory inv = inventoryMapper.selectOne(
                    new LambdaQueryWrapper<Inventory>()
                            .eq(Inventory::getWarehouseId, warehouseId)
                            .eq(Inventory::getProductId, item.getProductId()));
            if (inv != null) {
                inv.setReturnedQuantity(inv.getReturnedQuantity() + item.getQuantity());
                inv.setAvailableQuantity(inv.getAvailableQuantity() + item.getQuantity());
                inventoryMapper.updateById(inv);
            }

            WarehouseFlow flow = new WarehouseFlow();
            flow.setWarehouseId(warehouseId);
            flow.setProductId(item.getProductId());
            flow.setType("IN");
            flow.setSubType("RETURN");
            flow.setQuantity(item.getQuantity());
            flow.setRelatedOrderNo(task.getTaskCode());
            flow.setOperateTime(LocalDateTime.now());
            warehouseFlowMapper.insert(flow);
        }
    }

    @Override
    @Transactional
    public void stationReturnOut(List<Long> returnItemIds, Long fromWarehouseId) {
        for (Long itemId : returnItemIds) {
            ReturnOrderItem item = returnOrderItemMapper.selectById(itemId);
            if (item == null) continue;

            updateInventory(fromWarehouseId, item.getProductId(), item.getQuantity(), false);

            WarehouseFlow flow = new WarehouseFlow();
            flow.setWarehouseId(fromWarehouseId);
            flow.setProductId(item.getProductId());
            flow.setType("OUT");
            flow.setSubType("RETURN_OUT");
            flow.setQuantity(item.getQuantity());
            flow.setOperateTime(LocalDateTime.now());
            warehouseFlowMapper.insert(flow);
        }
    }

    @Override
    @Transactional
    public void centerReturnIn(Long returnOrderId) {
        ReturnOrder ro = returnOrderMapper.selectById(returnOrderId);
        if (ro == null) {
            throw new RuntimeException("退货单不存在");
        }

        Warehouse centerWarehouse = warehouseMapper.selectOne(
                new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getLevel, "CENTER").last("LIMIT 1"));
        Long warehouseId = centerWarehouse != null ? centerWarehouse.getId() : 1L;

        List<ReturnOrderItem> items = returnOrderItemMapper.selectList(
                new LambdaQueryWrapper<ReturnOrderItem>().eq(ReturnOrderItem::getReturnOrderId, returnOrderId));

        for (ReturnOrderItem item : items) {
            updateInventory(warehouseId, item.getProductId(), item.getQuantity(), true);

            WarehouseFlow flow = new WarehouseFlow();
            flow.setWarehouseId(warehouseId);
            flow.setProductId(item.getProductId());
            flow.setType("IN");
            flow.setSubType("RETURN_IN");
            flow.setQuantity(item.getQuantity());
            flow.setRelatedOrderNo(ro.getReturnCode());
            flow.setOperateTime(LocalDateTime.now());
            warehouseFlowMapper.insert(flow);
        }

        ro.setStatus("COMPLETED");
        returnOrderMapper.updateById(ro);
    }

    private void updateInventory(Long warehouseId, Long productId, int quantity, boolean isIn) {
        Inventory inv = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>()
                        .eq(Inventory::getWarehouseId, warehouseId)
                        .eq(Inventory::getProductId, productId));
        if (inv == null) {
            inv = new Inventory();
            inv.setWarehouseId(warehouseId);
            inv.setProductId(productId);
            inv.setTotalQuantity(isIn ? quantity : 0);
            inv.setReturnedQuantity(0);
            inv.setAllocatedQuantity(0);
            inv.setAvailableQuantity(isIn ? quantity : 0);
            inv.setWarningValue(0);
            inv.setMaxValue(0);
            inventoryMapper.insert(inv);
        } else {
            if (isIn) {
                inv.setTotalQuantity(inv.getTotalQuantity() + quantity);
                inv.setAvailableQuantity(inv.getAvailableQuantity() + quantity);
            } else {
                inv.setTotalQuantity(inv.getTotalQuantity() - quantity);
                inv.setAvailableQuantity(inv.getAvailableQuantity() - quantity);
            }
            inventoryMapper.updateById(inv);
        }
    }
}
