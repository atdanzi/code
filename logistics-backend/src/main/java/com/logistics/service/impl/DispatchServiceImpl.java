package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.logistics.entity.*;
import com.logistics.mapper.*;
import com.logistics.service.DispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DispatchServiceImpl implements DispatchService {

    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final TaskOrderMapper taskOrderMapper;
    private final TaskOrderItemMapper taskOrderItemMapper;
    private final TransferOrderMapper transferOrderMapper;
    private final TransferOrderItemMapper transferOrderItemMapper;
    private final StationMapper stationMapper;
    private final WarehouseMapper warehouseMapper;
    private final ShortageRecordMapper shortageRecordMapper;

    @Override
    @Transactional
    public void manualDispatch(Long orderId, Long stationId, Long operatorId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        order.setDeliveryStationId(stationId);
        order.setOrderStatus("DISPATCHED");
        order.setUpdateTime(LocalDateTime.now());
        ordersMapper.updateById(order);

        TaskOrder task = new TaskOrder();
        task.setTaskCode("TK" + System.currentTimeMillis());
        task.setOrderId(orderId);
        task.setStationId(stationId);
        task.setTaskType(order.getOrderType());
        task.setTaskStatus("PENDING");
        task.setReceiverName(order.getReceiverName());
        task.setReceiverPhone(order.getReceiverPhone());
        task.setReceiverAddress(order.getReceiverAddress());
        task.setRequireDate(order.getRequireDate());
        task.setDispatchType("MANUAL");
        task.setOperatorId(operatorId);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        taskOrderMapper.insert(task);

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem oi : items) {
            TaskOrderItem toi = new TaskOrderItem();
            toi.setTaskOrderId(task.getId());
            toi.setProductId(oi.getProductId());
            toi.setQuantity(oi.getQuantity());
            toi.setUnitPrice(oi.getUnitPrice());
            toi.setAmount(oi.getAmount());
            taskOrderItemMapper.insert(toi);
        }

        Warehouse stationWarehouse = warehouseMapper.selectOne(
                new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getStationId, stationId).last("LIMIT 1"));
        Warehouse centerWarehouse = warehouseMapper.selectOne(
                new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getLevel, "CENTER").last("LIMIT 1"));

        if (centerWarehouse != null && stationWarehouse != null) {
            TransferOrder transfer = new TransferOrder();
            transfer.setTransferCode("TF" + System.currentTimeMillis());
            transfer.setFromWarehouseId(centerWarehouse.getId());
            transfer.setToWarehouseId(stationWarehouse.getId());
            transfer.setStatus("PENDING");
            transfer.setRelatedOrderCode(order.getOrderCode());
            transfer.setRelatedTaskCode(task.getTaskCode());
            transfer.setPlanDate(order.getRequireDate());
            transfer.setOperatorId(operatorId);
            transfer.setCreateTime(LocalDateTime.now());
            transferOrderMapper.insert(transfer);

            for (OrderItem oi : items) {
                TransferOrderItem tfi = new TransferOrderItem();
                tfi.setTransferOrderId(transfer.getId());
                tfi.setProductId(oi.getProductId());
                tfi.setQuantity(oi.getQuantity());
                transferOrderItemMapper.insert(tfi);
            }
        }
    }

    @Override
    @Transactional
    public void autoDispatch(List<Long> orderIds, Long operatorId) {
        for (Long orderId : orderIds) {
            Orders order = ordersMapper.selectById(orderId);
            if (order != null && order.getDeliveryStationId() != null) {
                manualDispatch(orderId, order.getDeliveryStationId(), operatorId);
            }
        }
    }

    @Override
    @Transactional
    public void resolveShortage(List<Long> orderIds) {
        for (Long orderId : orderIds) {
            List<ShortageRecord> records = shortageRecordMapper.selectList(
                    new LambdaQueryWrapper<ShortageRecord>()
                            .eq(ShortageRecord::getOrderId, orderId)
                            .eq(ShortageRecord::getResolved, 0));
            for (ShortageRecord sr : records) {
                sr.setResolved(1);
                sr.setResolveTime(LocalDateTime.now());
                shortageRecordMapper.updateById(sr);
            }

            Orders order = ordersMapper.selectById(orderId);
            if (order != null) {
                order.setOrderStatus("AVAILABLE");
                order.setUpdateTime(LocalDateTime.now());
                ordersMapper.updateById(order);
            }
        }
    }
}
