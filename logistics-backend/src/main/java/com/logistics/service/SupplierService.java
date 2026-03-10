package com.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.Supplier;

public interface SupplierService extends IService<Supplier> {

    boolean hasProducts(Long supplierId);
}
