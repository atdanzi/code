package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.Customer;
import com.logistics.entity.Orders;
import com.logistics.mapper.CustomerMapper;
import com.logistics.mapper.OrdersMapper;
import com.logistics.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    private final OrdersMapper ordersMapper;

    @Override
    public IPage<Customer> pageCustomers(Page<Customer> page, String name, String idCard, String mobile) {

        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Customer::getName, name)
               .like(StringUtils.hasText(idCard), Customer::getIdCard, idCard)
               .like(StringUtils.hasText(mobile), Customer::getMobile, mobile)
               .orderByDesc(Customer::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public boolean hasOrders(Long customerId) {
        return ordersMapper.selectCount(
                new LambdaQueryWrapper<Orders>().eq(Orders::getCustomerId, customerId)) > 0;
    }
}
