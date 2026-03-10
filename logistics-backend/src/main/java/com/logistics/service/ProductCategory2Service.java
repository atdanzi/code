package com.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.ProductCategory2;

import java.util.List;

public interface ProductCategory2Service extends IService<ProductCategory2> {

    boolean hasProducts(Long id);

    List<ProductCategory2> listByCategory1(Long category1Id);
}
