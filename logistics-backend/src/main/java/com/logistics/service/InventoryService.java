package com.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.Inventory;

import java.util.List;

public interface InventoryService extends IService<Inventory> {

    List<Inventory> getStockList(Long warehouseId, String productName);

    void updateReserveSettings(Long warehouseId, Long productId, int warningValue, int maxValue);
}
