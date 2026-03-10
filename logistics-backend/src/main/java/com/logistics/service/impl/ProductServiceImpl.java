package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.OrderItem;
import com.logistics.entity.Product;
import com.logistics.mapper.OrderItemMapper;
import com.logistics.mapper.ProductMapper;
import com.logistics.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final OrderItemMapper orderItemMapper;

    @Override
    public IPage<Product> pageProducts(Page<Product> page, String name, Long category1Id, Long category2Id) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Product::getName, name)
               .eq(category1Id != null, Product::getCategory1Id, category1Id)
               .eq(category2Id != null, Product::getCategory2Id, category2Id)
               .orderByDesc(Product::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public boolean hasOrderItems(Long productId) {
        return orderItemMapper.selectCount(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getProductId, productId)) > 0;
    }
}
