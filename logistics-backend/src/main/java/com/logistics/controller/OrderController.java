package com.logistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.PageResult;
import com.logistics.common.Result;
import com.logistics.entity.OrderItem;
import com.logistics.entity.Orders;
import com.logistics.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Result<?> page(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String orderCode,
                          @RequestParam(required = false) String orderType,
                          @RequestParam(required = false) String orderStatus,
                          @RequestParam(required = false) Long customerId,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        IPage<Orders> result = orderService.pageOrders(new Page<>(page, size), orderCode, orderType, orderStatus, customerId, startDate, endDate);
        return Result.success(new PageResult<>(result));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        Orders order = orderService.getOrderDetail(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    @SuppressWarnings("unchecked")
    @PostMapping
    public Result<?> create(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");

        Orders order = new Orders();
        order.setCustomerId(toLong(body.get("customerId")));
        order.setOrderType((String) body.get("orderType"));
        order.setReceiverName((String) body.get("receiverName"));
        order.setReceiverPhone((String) body.get("receiverPhone"));
        order.setReceiverAddress((String) body.get("receiverAddress"));
        order.setReceiverZipCode((String) body.get("receiverZipCode"));
        order.setDeliveryStationId(toLong(body.get("deliveryStationId")));
        order.setNeedInvoice(body.get("needInvoice") != null ? ((Number) body.get("needInvoice")).intValue() : 0);
        order.setPayerName((String) body.get("payerName"));
        order.setPayerAddress((String) body.get("payerAddress"));
        order.setPayerPhone((String) body.get("payerPhone"));
        order.setPayerZipCode((String) body.get("payerZipCode"));
        order.setRemark((String) body.get("remark"));

        if (body.get("requireDate") != null) {
            order.setRequireDate(LocalDate.parse((String) body.get("requireDate")));
        }

        List<Map<String, Object>> itemMaps = (List<Map<String, Object>>) body.get("items");
        List<OrderItem> items = new java.util.ArrayList<>();
        if (itemMaps != null) {
            for (Map<String, Object> im : itemMaps) {
                OrderItem item = new OrderItem();
                item.setProductId(toLong(im.get("productId")));
                item.setQuantity(((Number) im.get("quantity")).intValue());
                item.setUnitPrice(new java.math.BigDecimal(im.get("unitPrice").toString()));
                item.setRemark((String) im.get("remark"));
                items.add(item);
            }
        }

        Orders created = orderService.createOrder(order, items, operatorId);
        return Result.success(created);
    }

    @PostMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");
        orderService.cancelOrder(id, body.get("reason"), operatorId);
        return Result.success();
    }

    @PostMapping("/{id}/exchange")
    public Result<?> exchange(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");
        Long productId = toLong(body.get("productId"));
        int quantity = ((Number) body.get("quantity")).intValue();
        String reason = (String) body.get("reason");
        LocalDate requireDate = body.get("requireDate") != null ? LocalDate.parse((String) body.get("requireDate")) : null;
        orderService.exchangeOrder(id, productId, quantity, reason, requireDate, operatorId);
        return Result.success();
    }

    @PostMapping("/{id}/return")
    public Result<?> returnOrder(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");
        Long productId = toLong(body.get("productId"));
        int quantity = ((Number) body.get("quantity")).intValue();
        String reason = (String) body.get("reason");
        LocalDate requireDate = body.get("requireDate") != null ? LocalDate.parse((String) body.get("requireDate")) : null;
        orderService.returnOrder(id, productId, quantity, reason, requireDate, operatorId);
        return Result.success();
    }

    private Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).longValue();
        return Long.parseLong(obj.toString());
    }
}
