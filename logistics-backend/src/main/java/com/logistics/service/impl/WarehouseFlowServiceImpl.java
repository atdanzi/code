package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.Product;
import com.logistics.entity.WarehouseFlow;
import com.logistics.mapper.ProductMapper;
import com.logistics.mapper.WarehouseFlowMapper;
import com.logistics.service.WarehouseFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseFlowServiceImpl extends ServiceImpl<WarehouseFlowMapper, WarehouseFlow> implements WarehouseFlowService {

    private final ProductMapper productMapper;

    @Override
    public IPage<WarehouseFlow> pageFlows(Page<WarehouseFlow> page, Long warehouseId, String productName,
                                           String type, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<WarehouseFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(warehouseId != null, WarehouseFlow::getWarehouseId, warehouseId)
               .eq(StringUtils.hasText(type), WarehouseFlow::getType, type)
               .ge(startDate != null, WarehouseFlow::getOperateTime, startDate != null ? startDate.atStartOfDay() : null)
               .le(endDate != null, WarehouseFlow::getOperateTime, endDate != null ? endDate.plusDays(1).atStartOfDay() : null);

        if (StringUtils.hasText(productName)) {
            List<Product> products = productMapper.selectList(
                    new LambdaQueryWrapper<Product>().like(Product::getName, productName));
            if (products.isEmpty()) {
                wrapper.eq(WarehouseFlow::getProductId, -1L);
            } else {
                List<Long> pids = products.stream().map(Product::getId).collect(Collectors.toList());
                wrapper.in(WarehouseFlow::getProductId, pids);
            }
        }

        wrapper.orderByDesc(WarehouseFlow::getOperateTime);
        return page(page, wrapper);
    }
}
