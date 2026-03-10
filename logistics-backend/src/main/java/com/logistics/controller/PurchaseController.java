package com.logistics.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.PageResult;
import com.logistics.common.Result;
import com.logistics.entity.PurchaseOrder;
import com.logistics.entity.PurchaseOrderItem;
import com.logistics.entity.ReturnOrder;
import com.logistics.entity.ReturnOrderItem;
import com.logistics.mapper.ReturnOrderItemMapper;
import com.logistics.mapper.ReturnOrderMapper;
import com.logistics.service.PurchaseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final ReturnOrderMapper returnOrderMapper;
    private final ReturnOrderItemMapper returnOrderItemMapper;

    @GetMapping("/shortage-check")
    public Result<?> checkShortage(@RequestParam(required = false) Long category1Id,
                                   @RequestParam(required = false) Long category2Id,
                                   @RequestParam(required = false) String productName) {
        return Result.success(purchaseService.checkShortage(category1Id, category2Id, productName));
    }

    @SuppressWarnings("unchecked")
    @PostMapping
    public Result<?> create(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");

        PurchaseOrder order = new PurchaseOrder();
        order.setSupplierId(((Number) body.get("supplierId")).longValue());
        order.setOperatorId(operatorId);
        if (body.get("purchaseDate") != null) {
            order.setPurchaseDate(LocalDate.parse((String) body.get("purchaseDate")));
        } else {
            order.setPurchaseDate(LocalDate.now());
        }
        order.setRemark((String) body.get("remark"));

        List<Map<String, Object>> itemMaps = (List<Map<String, Object>>) body.get("items");
        List<PurchaseOrderItem> items = new ArrayList<>();
        if (itemMaps != null) {
            for (Map<String, Object> im : itemMaps) {
                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setProductId(((Number) im.get("productId")).longValue());
                item.setOrderQuantity(((Number) im.get("orderQuantity")).intValue());
                item.setRemark((String) im.get("remark"));
                items.add(item);
            }
        }

        PurchaseOrder created = purchaseService.createPurchase(order, items);
        return Result.success(created);
    }

    @GetMapping
    public Result<?> page(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) Long supplierId,
                          @RequestParam(required = false) String status) {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(supplierId != null, PurchaseOrder::getSupplierId, supplierId)
               .eq(StringUtils.hasText(status), PurchaseOrder::getStatus, status)
               .orderByDesc(PurchaseOrder::getCreateTime);
        IPage<PurchaseOrder> result = purchaseService.page(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(result));
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/return")
    public Result<?> createReturn(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");

        ReturnOrder ro = new ReturnOrder();
        ro.setReturnCode("RO" + System.currentTimeMillis());
        ro.setSupplierId(((Number) body.get("supplierId")).longValue());
        ro.setType("SUPPLIER");
        ro.setStatus("PENDING");
        ro.setOperatorId(operatorId);
        ro.setReturnDate(LocalDate.now());
        ro.setRemark((String) body.get("remark"));
        ro.setCreateTime(LocalDateTime.now());
        returnOrderMapper.insert(ro);

        List<Map<String, Object>> itemMaps = (List<Map<String, Object>>) body.get("items");
        if (itemMaps != null) {
            for (Map<String, Object> im : itemMaps) {
                ReturnOrderItem item = new ReturnOrderItem();
                item.setReturnOrderId(ro.getId());
                item.setProductId(((Number) im.get("productId")).longValue());
                item.setQuantity(((Number) im.get("quantity")).intValue());
                item.setRemark((String) im.get("remark"));
                returnOrderItemMapper.insert(item);
            }
        }

        return Result.success(ro);
    }
}
