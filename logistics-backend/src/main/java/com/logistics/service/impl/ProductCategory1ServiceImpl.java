package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.ProductCategory1;
import com.logistics.entity.ProductCategory2;
import com.logistics.mapper.ProductCategory1Mapper;
import com.logistics.mapper.ProductCategory2Mapper;
import com.logistics.service.ProductCategory1Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCategory1ServiceImpl extends ServiceImpl<ProductCategory1Mapper, ProductCategory1> implements ProductCategory1Service {

    private final ProductCategory2Mapper productCategory2Mapper;

    @Override
    public boolean hasChildren(Long id) {
        return productCategory2Mapper.selectCount(
                new LambdaQueryWrapper<ProductCategory2>().eq(ProductCategory2::getCategory1Id, id)) > 0;
    }
}
