package com.logistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.PageResult;
import com.logistics.common.Result;
import com.logistics.entity.Customer;
import com.logistics.entity.Orders;
import com.logistics.service.CustomerService;
import com.logistics.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;

    @GetMapping
    public Result<?> page(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) String idCard,
                          @RequestParam(required = false) String mobile) {
        IPage<Customer> result = customerService.pageCustomers(new Page<>(page, size), name, idCard, mobile);
        return Result.success(new PageResult<>(result));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        return Result.success(customer);
    }

    @PostMapping
    public Result<?> create(@RequestBody Customer customer) {
        customer.setCustomerCode("CU" + System.currentTimeMillis());
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());
        customerService.save(customer);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        customer.setUpdateTime(LocalDateTime.now());
        customerService.updateById(customer);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (customerService.hasOrders(id)) {
            return Result.error("该客户存在关联订单，不能删除");
        }
        customerService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}/orders")
    public Result<?> getOrders(@PathVariable Long id,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size) {
        IPage<Orders> result = orderService.pageOrders(new Page<>(page, size), null, null, null, id, null, null);
        return Result.success(new PageResult<>(result));
    }
}
