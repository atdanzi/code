package com.logistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.PageResult;
import com.logistics.common.Result;
import com.logistics.entity.Product;
import com.logistics.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Result<?> page(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) Long category1Id,
                          @RequestParam(required = false) Long category2Id) {
        IPage<Product> result = productService.pageProducts(new Page<>(page, size), name, category1Id, category2Id);
        return Result.success(new PageResult<>(result));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    @PostMapping
    public Result<?> create(@RequestBody Product product) {
        product.setProductCode("PD" + System.currentTimeMillis());
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productService.save(product);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        product.setUpdateTime(LocalDateTime.now());
        productService.updateById(product);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (productService.hasOrderItems(id)) {
            return Result.error("该商品存在关联订单明细，不能删除");
        }
        productService.removeById(id);
        return Result.success();
    }
}
