package com.logistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.WarehouseFlow;

import java.time.LocalDate;

public interface WarehouseFlowService extends IService<WarehouseFlow> {

    IPage<WarehouseFlow> pageFlows(Page<WarehouseFlow> page, Long warehouseId, String productName,
                                    String type, LocalDate startDate, LocalDate endDate);
}
