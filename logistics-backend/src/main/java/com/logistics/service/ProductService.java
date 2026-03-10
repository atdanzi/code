package com.logistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.Product;

public interface ProductService extends IService<Product> {

    IPage<Product> pageProducts(Page<Product> page, String name, Long category1Id, Long category2Id);

    boolean hasOrderItems(Long productId);
}
