package com.logistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.PageResult;
import com.logistics.common.Result;
import com.logistics.entity.Inventory;
import com.logistics.entity.WarehouseFlow;
import com.logistics.service.InventoryService;
import com.logistics.service.WarehouseFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final WarehouseFlowService warehouseFlowService;

    @GetMapping
    public Result<?> stockList(@RequestParam(required = false) Long warehouseId,
                               @RequestParam(required = false) String productName) {
        List<Inventory> list = inventoryService.getStockList(warehouseId, productName);
        return Result.success(list);
    }

    @GetMapping("/flow")
    public Result<?> flowPage(@RequestParam(required = false) Long warehouseId,
                              @RequestParam(required = false) String productName,
                              @RequestParam(required = false) String type,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) {
        IPage<WarehouseFlow> result = warehouseFlowService.pageFlows(new Page<>(page, size), warehouseId, productName, type, startDate, endDate);
        return Result.success(new PageResult<>(result));
    }
}
