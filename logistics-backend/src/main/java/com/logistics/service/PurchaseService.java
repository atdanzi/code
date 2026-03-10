package com.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.PurchaseOrder;
import com.logistics.entity.PurchaseOrderItem;

import java.util.List;
import java.util.Map;

public interface PurchaseService extends IService<PurchaseOrder> {

    PurchaseOrder createPurchase(PurchaseOrder order, List<PurchaseOrderItem> items);

    List<Map<String, Object>> checkShortage(Long category1Id, Long category2Id, String productName);
}
