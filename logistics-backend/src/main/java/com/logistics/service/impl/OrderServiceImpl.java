package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.*;
import com.logistics.mapper.*;
import com.logistics.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    private final OrderItemMapper orderItemMapper;
    private final InventoryMapper inventoryMapper;
    private final ShortageRecordMapper shortageRecordMapper;

    @Override
    @Transactional
    public Orders createOrder(Orders order, List<OrderItem> items, Long operatorId) {
        order.setOrderCode("OD" + System.currentTimeMillis());
        order.setOperatorId(operatorId);
        order.setOrderDate(LocalDate.now());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;
        boolean hasShortage = false;

        for (OrderItem item : items) {
            item.setAmount(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            totalAmount = totalAmount.add(item.getAmount());

            Inventory inv = inventoryMapper.selectOne(
                    new LambdaQueryWrapper<Inventory>()
                            .eq(Inventory::getProductId, item.getProductId())
                            .last("LIMIT 1"));
            if (inv == null || inv.getAvailableQuantity() < item.getQuantity()) {
                hasShortage = true;
                ShortageRecord sr = new ShortageRecord();
                sr.setProductId(item.getProductId());
                sr.setShortageQuantity(item.getQuantity() - (inv == null ? 0 : inv.getAvailableQuantity()));
                sr.setResolved(0);
                sr.setCreateTime(LocalDateTime.now());
                shortageRecordMapper.insert(sr);
            }
        }

        order.setTotalAmount(totalAmount);
        order.setOrderStatus(hasShortage ? "SHORTAGE" : "AVAILABLE");
        save(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // 更新缺货记录的orderId
        if (hasShortage) {
            List<ShortageRecord> records = shortageRecordMapper.selectList(
                    new LambdaQueryWrapper<ShortageRecord>().isNull(ShortageRecord::getOrderId));
            for (ShortageRecord sr : records) {
                sr.setOrderId(order.getId());
                shortageRecordMapper.updateById(sr);
            }
        }

        return order;
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String reason, Long operatorId) {
        Orders order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        order.setOrderStatus("CANCELLED");
        order.setCancelReason(reason);
        order.setCancelDate(LocalDate.now());
        order.setUpdateTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void exchangeOrder(Long orderId, Long productId, int quantity, String reason, LocalDate requireDate, Long operatorId) {
        Orders original = getById(orderId);
        if (original == null) {
            throw new RuntimeException("原订单不存在");
        }

        Orders exchangeOrder = new Orders();
        exchangeOrder.setOrderCode("OD" + System.currentTimeMillis());
        exchangeOrder.setCustomerId(original.getCustomerId());
        exchangeOrder.setOrderType("EXCHANGE");
        exchangeOrder.setOrderStatus("AVAILABLE");
        exchangeOrder.setReceiverName(original.getReceiverName());
        exchangeOrder.setReceiverPhone(original.getReceiverPhone());
        exchangeOrder.setReceiverAddress(original.getReceiverAddress());
        exchangeOrder.setDeliveryStationId(original.getDeliveryStationId());
        exchangeOrder.setRequireDate(requireDate);
        exchangeOrder.setOrderDate(LocalDate.now());
        exchangeOrder.setRelatedOrderId(orderId);
        exchangeOrder.setOperatorId(operatorId);
        exchangeOrder.setRemark(reason);
        exchangeOrder.setTotalAmount(BigDecimal.ZERO);
        exchangeOrder.setCreateTime(LocalDateTime.now());
        exchangeOrder.setUpdateTime(LocalDateTime.now());
        save(exchangeOrder);

        OrderItem item = new OrderItem();
        item.setOrderId(exchangeOrder.getId());
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setUnitPrice(BigDecimal.ZERO);
        item.setAmount(BigDecimal.ZERO);
        item.setRemark(reason);
        orderItemMapper.insert(item);
    }

    @Override
    @Transactional
    public void returnOrder(Long orderId, Long productId, int quantity, String reason, LocalDate requireDate, Long operatorId) {
        Orders original = getById(orderId);
        if (original == null) {
            throw new RuntimeException("原订单不存在");
        }

        Orders returnOrder = new Orders();
        returnOrder.setOrderCode("OD" + System.currentTimeMillis());
        returnOrder.setCustomerId(original.getCustomerId());
        returnOrder.setOrderType("RETURN");
        returnOrder.setOrderStatus("AVAILABLE");
        returnOrder.setReceiverName(original.getReceiverName());
        returnOrder.setReceiverPhone(original.getReceiverPhone());
        returnOrder.setReceiverAddress(original.getReceiverAddress());
        returnOrder.setDeliveryStationId(original.getDeliveryStationId());
        returnOrder.setRequireDate(requireDate);
        returnOrder.setOrderDate(LocalDate.now());
        returnOrder.setRelatedOrderId(orderId);
        returnOrder.setOperatorId(operatorId);
        returnOrder.setRemark(reason);
        returnOrder.setTotalAmount(BigDecimal.ZERO);
        returnOrder.setCreateTime(LocalDateTime.now());
        returnOrder.setUpdateTime(LocalDateTime.now());
        save(returnOrder);

        OrderItem item = new OrderItem();
        item.setOrderId(returnOrder.getId());
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setUnitPrice(BigDecimal.ZERO);
        item.setAmount(BigDecimal.ZERO);
        item.setRemark(reason);
        orderItemMapper.insert(item);
    }

    @Override
    public IPage<Orders> pageOrders(Page<Orders> page, String orderCode, String orderType, String orderStatus,
                                     Long customerId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(orderCode), Orders::getOrderCode, orderCode)
               .eq(StringUtils.hasText(orderType), Orders::getOrderType, orderType)
               .eq(StringUtils.hasText(orderStatus), Orders::getOrderStatus, orderStatus)
               .eq(customerId != null, Orders::getCustomerId, customerId)
               .ge(startDate != null, Orders::getOrderDate, startDate)
               .le(endDate != null, Orders::getOrderDate, endDate)
               .orderByDesc(Orders::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Orders getOrderDetail(Long orderId) {
        Orders order = getById(orderId);
        if (order == null) {
            return null;
        }
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        order.setItems(items);
        return order;
    }
}
