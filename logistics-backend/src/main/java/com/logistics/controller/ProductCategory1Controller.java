package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.entity.ProductCategory1;
import com.logistics.service.ProductCategory1Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/product/categories1")
@RequiredArgsConstructor
public class ProductCategory1Controller {

    private final ProductCategory1Service productCategory1Service;

    @GetMapping
    public Result<?> list() {
        return Result.success(productCategory1Service.list());
    }

    @PostMapping
    public Result<?> create(@RequestBody ProductCategory1 category) {
        category.setCreateTime(LocalDateTime.now());
        productCategory1Service.save(category);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ProductCategory1 category) {
        category.setId(id);
        productCategory1Service.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (productCategory1Service.hasChildren(id)) {
            return Result.error("该分类下存在二级分类，不能删除");
        }
        productCategory1Service.removeById(id);
        return Result.success();
    }
}
