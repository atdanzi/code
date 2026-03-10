package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.Inventory;
import com.logistics.entity.PurchaseOrder;
import com.logistics.entity.PurchaseOrderItem;
import com.logistics.entity.ShortageRecord;
import com.logistics.mapper.InventoryMapper;
import com.logistics.mapper.PurchaseOrderItemMapper;
import com.logistics.mapper.PurchaseOrderMapper;
import com.logistics.mapper.ShortageRecordMapper;
import com.logistics.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseService {

    private final PurchaseOrderItemMapper purchaseOrderItemMapper;
    private final ShortageRecordMapper shortageRecordMapper;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public PurchaseOrder createPurchase(PurchaseOrder order, List<PurchaseOrderItem> items) {
        order.setPurchaseCode("PO" + System.currentTimeMillis());
        order.setStatus("PENDING");
        order.setCreateTime(LocalDateTime.now());
        save(order);

        for (PurchaseOrderItem item : items) {
            item.setPurchaseOrderId(order.getId());
            purchaseOrderItemMapper.insert(item);
        }
        return order;
    }

    @Override
    public List<Map<String, Object>> checkShortage(Long category1Id, Long category2Id, String productName) {
        List<Map<String, Object>> result = new ArrayList<>();

        List<ShortageRecord> shortages = shortageRecordMapper.selectList(
                new LambdaQueryWrapper<ShortageRecord>().eq(ShortageRecord::getResolved, 0));
        for (ShortageRecord sr : shortages) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", "SHORTAGE");
            map.put("productId", sr.getProductId());
            map.put("orderId", sr.getOrderId());
            map.put("shortageQuantity", sr.getShortageQuantity());
            result.add(map);
        }

        QueryWrapper<Inventory> invWrapper = new QueryWrapper<>();
        invWrapper.apply("available_quantity <= warning_value AND warning_value > 0");
        List<Inventory> lowStock = inventoryMapper.selectList(invWrapper);
        for (Inventory inv : lowStock) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", "LOW_STOCK");
            map.put("productId", inv.getProductId());
            map.put("warehouseId", inv.getWarehouseId());
            map.put("availableQuantity", inv.getAvailableQuantity());
            map.put("warningValue", inv.getWarningValue());
            result.add(map);
        }

        return result;
    }
}
