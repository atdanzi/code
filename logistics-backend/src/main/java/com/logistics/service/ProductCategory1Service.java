package com.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.ProductCategory1;

public interface ProductCategory1Service extends IService<ProductCategory1> {

    boolean hasChildren(Long id);
}
