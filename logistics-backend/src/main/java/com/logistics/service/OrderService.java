package com.logistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.OrderItem;
import com.logistics.entity.Orders;

import java.time.LocalDate;
import java.util.List;

public interface OrderService extends IService<Orders> {

    Orders createOrder(Orders order, List<OrderItem> items, Long operatorId);

    void cancelOrder(Long orderId, String reason, Long operatorId);

    void exchangeOrder(Long orderId, Long productId, int quantity, String reason, LocalDate requireDate, Long operatorId);

    void returnOrder(Long orderId, Long productId, int quantity, String reason, LocalDate requireDate, Long operatorId);

    IPage<Orders> pageOrders(Page<Orders> page, String orderCode, String orderType, String orderStatus,
                             Long customerId, LocalDate startDate, LocalDate endDate);

    Orders getOrderDetail(Long orderId);
}
