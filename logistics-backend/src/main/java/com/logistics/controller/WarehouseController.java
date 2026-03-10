package com.logistics.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.logistics.common.Result;
import com.logistics.entity.Inventory;
import com.logistics.entity.Warehouse;
import com.logistics.service.InventoryService;
import com.logistics.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final InventoryService inventoryService;

    @GetMapping
    public Result<?> list() {
        return Result.success(warehouseService.list());
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Warehouse warehouse = warehouseService.getById(id);
        if (warehouse == null) {
            return Result.error("仓库不存在");
        }
        return Result.success(warehouse);
    }

    @PostMapping
    public Result<?> create(@RequestBody Warehouse warehouse) {
        warehouse.setWarehouseCode("WH" + System.currentTimeMillis());
        warehouse.setCreateTime(LocalDateTime.now());
        warehouseService.save(warehouse);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        warehouse.setId(id);
        warehouseService.updateById(warehouse);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        long invCount = inventoryService.count(
                new LambdaQueryWrapper<Inventory>().eq(Inventory::getWarehouseId, id));
        if (invCount > 0) {
            return Result.error("仓库存在库存记录，不能删除");
        }
        warehouseService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}/reserve")
    public Result<?> getReserve(@PathVariable Long id) {
        List<Inventory> list = inventoryService.getStockList(id, null);
        return Result.success(list);
    }

    @PutMapping("/{id}/reserve")
    public Result<?> updateReserve(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long productId = ((Number) body.get("productId")).longValue();
        int warningValue = ((Number) body.get("warningValue")).intValue();
        int maxValue = ((Number) body.get("maxValue")).intValue();
        inventoryService.updateReserveSettings(id, productId, warningValue, maxValue);
        return Result.success();
    }
}
