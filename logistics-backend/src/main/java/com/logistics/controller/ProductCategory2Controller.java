package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.entity.ProductCategory2;
import com.logistics.service.ProductCategory2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/product/categories2")
@RequiredArgsConstructor
public class ProductCategory2Controller {

    private final ProductCategory2Service productCategory2Service;

    @GetMapping
    public Result<?> list(@RequestParam(required = false) Long category1Id) {
        if (category1Id != null) {
            return Result.success(productCategory2Service.listByCategory1(category1Id));
        }
        return Result.success(productCategory2Service.list());
    }

    @PostMapping
    public Result<?> create(@RequestBody ProductCategory2 category) {
        category.setCreateTime(LocalDateTime.now());
        productCategory2Service.save(category);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ProductCategory2 category) {
        category.setId(id);
        productCategory2Service.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (productCategory2Service.hasProducts(id)) {
            return Result.error("该分类下存在商品，不能删除");
        }
        productCategory2Service.removeById(id);
        return Result.success();
    }
}
