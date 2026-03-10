package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.Product;
import com.logistics.entity.ProductCategory2;
import com.logistics.mapper.ProductCategory2Mapper;
import com.logistics.mapper.ProductMapper;
import com.logistics.service.ProductCategory2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategory2ServiceImpl extends ServiceImpl<ProductCategory2Mapper, ProductCategory2> implements ProductCategory2Service {

    private final ProductMapper productMapper;

    @Override
    public boolean hasProducts(Long id) {
        return productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getCategory2Id, id)) > 0;
    }

    @Override
    public List<ProductCategory2> listByCategory1(Long category1Id) {
        return list(new LambdaQueryWrapper<ProductCategory2>().eq(ProductCategory2::getCategory1Id, category1Id));
    }
}
