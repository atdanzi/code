package com.logistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.Customer;

public interface CustomerService extends IService<Customer> {

    IPage<Customer> pageCustomers(Page<Customer> page, String name, String idCard, String mobile);

    boolean hasOrders(Long customerId);
}
