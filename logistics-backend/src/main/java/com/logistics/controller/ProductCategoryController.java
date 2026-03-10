package com.logistics.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.logistics.common.Result;
import com.logistics.entity.Product;
import com.logistics.entity.ProductCategory1;
import com.logistics.entity.ProductCategory2;
import com.logistics.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.logistics.mapper.ProductCategory1Mapper;
import com.logistics.mapper.ProductCategory2Mapper;

import java.util.List;

@RestController
@RequestMapping("/api/product/category")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategory1Mapper category1Mapper;
    private final ProductCategory2Mapper category2Mapper;
    private final ProductService productService;

    @GetMapping("/level1/list")
    public Result<List<ProductCategory1>> listLevel1() {
        return Result.success(category1Mapper.selectList(null));
    }

    @PostMapping("/level1")
    public Result<Void> addLevel1(@RequestBody ProductCategory1 category) {
        category1Mapper.insert(category);
        return Result.success();
    }

    @PutMapping("/level1")
    public Result<Void> updateLevel1(@RequestBody ProductCategory1 category) {
        category1Mapper.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/level1/{id}")
    public Result<Void> deleteLevel1(@PathVariable Long id) {
        long count = category2Mapper.selectCount(
                new LambdaQueryWrapper<ProductCategory2>().eq(ProductCategory2::getCategory1Id, id));
        if (count > 0) {
            return Result.error("该一级分类下存在二级分类，无法删除");
        }
        category1Mapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/level2/list")
    public Result<List<ProductCategory2>> listLevel2(@RequestParam(required = false) Long category1Id) {
        LambdaQueryWrapper<ProductCategory2> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category1Id != null, ProductCategory2::getCategory1Id, category1Id);
        return Result.success(category2Mapper.selectList(wrapper));
    }

    @PostMapping("/level2")
    public Result<Void> addLevel2(@RequestBody ProductCategory2 category) {
        category2Mapper.insert(category);
        return Result.success();
    }

    @PutMapping("/level2")
    public Result<Void> updateLevel2(@RequestBody ProductCategory2 category) {
        category2Mapper.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/level2/{id}")
    public Result<Void> deleteLevel2(@PathVariable Long id) {
        long count = productService.count(
                new LambdaQueryWrapper<Product>().eq(Product::getCategory2Id, id));
        if (count > 0) {
            return Result.error("该二级分类下存在商品，无法删除");
        }
        category2Mapper.deleteById(id);
        return Result.success();
    }
}
