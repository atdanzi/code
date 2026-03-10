package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.Product;
import com.logistics.entity.Supplier;
import com.logistics.mapper.ProductMapper;
import com.logistics.mapper.SupplierMapper;
import com.logistics.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    private final ProductMapper productMapper;

    @Override
    public boolean hasProducts(Long supplierId) {
        return productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getSupplierId, supplierId)) > 0;
    }
}
