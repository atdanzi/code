package com.logistics.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.PageResult;
import com.logistics.common.Result;
import com.logistics.entity.Supplier;
import com.logistics.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public Result<?> list(@RequestParam(required = false, defaultValue = "1") int page,
                          @RequestParam(required = false, defaultValue = "1000") int size) {
        IPage<Supplier> result = supplierService.page(new Page<>(page, size),
                new LambdaQueryWrapper<Supplier>().orderByDesc(Supplier::getCreateTime));
        return Result.success(new PageResult<>(result));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Supplier supplier = supplierService.getById(id);
        if (supplier == null) {
            return Result.error("供应商不存在");
        }
        return Result.success(supplier);
    }

    @PostMapping
    public Result<?> create(@RequestBody Supplier supplier) {
        supplier.setSupplierCode("SP" + System.currentTimeMillis());
        supplier.setCreateTime(LocalDateTime.now());
        supplier.setUpdateTime(LocalDateTime.now());
        supplierService.save(supplier);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        supplier.setUpdateTime(LocalDateTime.now());
        supplierService.updateById(supplier);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (supplierService.hasProducts(id)) {
            return Result.error("该供应商存在关联商品，不能删除");
        }
        supplierService.removeById(id);
        return Result.success();
    }
}
