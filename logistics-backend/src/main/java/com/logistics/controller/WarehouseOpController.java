package com.logistics.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.PageResult;
import com.logistics.common.Result;
import com.logistics.entity.PurchaseOrder;
import com.logistics.entity.TransferOrder;
import com.logistics.mapper.PurchaseOrderMapper;
import com.logistics.mapper.TransferOrderMapper;
import com.logistics.service.WarehouseOperationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse-op")
@RequiredArgsConstructor
public class WarehouseOpController {

    private final WarehouseOperationService warehouseOperationService;
    private final TransferOrderMapper transferOrderMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @SuppressWarnings("unchecked")
    @PostMapping("/purchase-in")
    public Result<?> purchaseIn(@RequestBody Map<String, Object> body) {
        Long purchaseOrderId = ((Number) body.get("purchaseOrderId")).longValue();
        Map<String, Object> rawQuantities = (Map<String, Object>) body.get("actualQuantities");
        Map<Long, Integer> actualQuantities = new HashMap<>();
        if (rawQuantities != null) {
            rawQuantities.forEach((k, v) -> actualQuantities.put(Long.parseLong(k), ((Number) v).intValue()));
        }
        LocalDate inboundDate = body.get("inboundDate") != null ? LocalDate.parse((String) body.get("inboundDate")) : LocalDate.now();
        String remark = (String) body.get("remark");
        warehouseOperationService.purchaseInbound(purchaseOrderId, actualQuantities, inboundDate, remark);
        return Result.success();
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/transfer-out")
    public Result<?> transferOut(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");
        List<Number> ids = (List<Number>) body.get("transferOrderIds");
        List<Long> transferOrderIds = ids.stream().map(Number::longValue).toList();
        warehouseOperationService.transferOut(transferOrderIds, operatorId);
        return Result.success();
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/transfer-in")
    public Result<?> transferIn(@RequestBody Map<String, Object> body) {
        Long transferOrderId = ((Number) body.get("transferOrderId")).longValue();
        Map<String, Object> rawQuantities = (Map<String, Object>) body.get("actualQuantities");
        Map<Long, Integer> actualQuantities = new HashMap<>();
        if (rawQuantities != null) {
            rawQuantities.forEach((k, v) -> actualQuantities.put(Long.parseLong(k), ((Number) v).intValue()));
        }
        LocalDate inboundDate = body.get("inboundDate") != null ? LocalDate.parse((String) body.get("inboundDate")) : LocalDate.now();
        String remark = (String) body.get("remark");
        warehouseOperationService.transferIn(transferOrderId, actualQuantities, inboundDate, remark);
        return Result.success();
    }

    @PostMapping("/pick")
    public Result<?> pick(@RequestBody Map<String, Object> body) {
        Long taskOrderId = ((Number) body.get("taskOrderId")).longValue();
        Long pickerId = ((Number) body.get("pickerId")).longValue();
        LocalDate pickDate = body.get("pickDate") != null ? LocalDate.parse((String) body.get("pickDate")) : LocalDate.now();
        warehouseOperationService.pickGoods(taskOrderId, pickerId, pickDate);
        return Result.success();
    }

    @PostMapping("/return-register")
    public Result<?> returnRegister(@RequestBody Map<String, Long> body) {
        warehouseOperationService.returnRegister(body.get("taskOrderId"));
        return Result.success();
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/station-return-out")
    public Result<?> stationReturnOut(@RequestBody Map<String, Object> body) {
        List<Number> ids = (List<Number>) body.get("returnItemIds");
        List<Long> returnItemIds = ids.stream().map(Number::longValue).toList();
        Long fromWarehouseId = ((Number) body.get("fromWarehouseId")).longValue();
        warehouseOperationService.stationReturnOut(returnItemIds, fromWarehouseId);
        return Result.success();
    }

    @PostMapping("/center-return-in")
    public Result<?> centerReturnIn(@RequestBody Map<String, Long> body) {
        warehouseOperationService.centerReturnIn(body.get("returnOrderId"));
        return Result.success();
    }

    @GetMapping("/transfer-orders")
    public Result<?> transferOrders(@RequestParam(required = false) String status,
                                    @RequestParam(required = false) Long warehouseId,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        LambdaQueryWrapper<TransferOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), TransferOrder::getStatus, status)
               .and(warehouseId != null, w -> w.eq(TransferOrder::getFromWarehouseId, warehouseId)
                       .or().eq(TransferOrder::getToWarehouseId, warehouseId))
               .orderByDesc(TransferOrder::getCreateTime);
        IPage<TransferOrder> result = transferOrderMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(result));
    }

    @GetMapping("/purchase-orders")
    public Result<?> purchaseOrders(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseOrder::getStatus, "PENDING")
               .orderByDesc(PurchaseOrder::getCreateTime);
        IPage<PurchaseOrder> result = purchaseOrderMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(result));
    }
}
