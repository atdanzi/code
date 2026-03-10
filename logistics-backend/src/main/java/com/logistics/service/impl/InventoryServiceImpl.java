package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.Inventory;
import com.logistics.entity.Product;
import com.logistics.mapper.InventoryMapper;
import com.logistics.mapper.ProductMapper;
import com.logistics.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    private final ProductMapper productMapper;

    @Override
    public List<Inventory> getStockList(Long warehouseId, String productName) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(warehouseId != null, Inventory::getWarehouseId, warehouseId);

        if (StringUtils.hasText(productName)) {
            List<Product> products = productMapper.selectList(
                    new LambdaQueryWrapper<Product>().like(Product::getName, productName));
            if (products.isEmpty()) {
                return List.of();
            }
            List<Long> productIds = products.stream().map(Product::getId).collect(Collectors.toList());
            wrapper.in(Inventory::getProductId, productIds);
        }

        return list(wrapper);
    }

    @Override
    public void updateReserveSettings(Long warehouseId, Long productId, int warningValue, int maxValue) {
        Inventory inv = getOne(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getWarehouseId, warehouseId)
                .eq(Inventory::getProductId, productId));
        if (inv == null) {
            inv = new Inventory();
            inv.setWarehouseId(warehouseId);
            inv.setProductId(productId);
            inv.setTotalQuantity(0);
            inv.setReturnedQuantity(0);
            inv.setAllocatedQuantity(0);
            inv.setAvailableQuantity(0);
            inv.setWarningValue(warningValue);
            inv.setMaxValue(maxValue);
            save(inv);
        } else {
            inv.setWarningValue(warningValue);
            inv.setMaxValue(maxValue);
            updateById(inv);
        }
    }
}
