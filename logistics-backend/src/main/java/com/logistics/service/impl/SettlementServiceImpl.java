package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.SettlementDetail;
import com.logistics.entity.SettlementRecord;
import com.logistics.entity.WarehouseFlow;
import com.logistics.mapper.SettlementDetailMapper;
import com.logistics.mapper.SettlementRecordMapper;
import com.logistics.mapper.WarehouseFlowMapper;
import com.logistics.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl extends ServiceImpl<SettlementRecordMapper, SettlementRecord> implements SettlementService {

    private final SettlementDetailMapper settlementDetailMapper;
    private final WarehouseFlowMapper warehouseFlowMapper;

    @Override
    public List<Map<String, Object>> querySupplierSettlement(Long supplierId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<WarehouseFlow> wrapper = new QueryWrapper<>();
        wrapper.select("product_id AS productId", "SUM(quantity) AS totalQuantity")
               .eq("sub_type", "PURCHASE")
               .ge(startDate != null, "operate_time", startDate != null ? startDate.atStartOfDay() : null)
               .le(endDate != null, "operate_time", endDate != null ? endDate.plusDays(1).atStartOfDay() : null)
               .groupBy("product_id");
        return warehouseFlowMapper.selectMaps(wrapper);
    }

    @Override
    public List<Map<String, Object>> queryStationSettlement(Long stationId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<WarehouseFlow> wrapper = new QueryWrapper<>();
        wrapper.select("product_id AS productId",
                       "SUM(CASE WHEN type='IN' THEN quantity ELSE 0 END) AS inQuantity",
                       "SUM(CASE WHEN type='OUT' THEN quantity ELSE 0 END) AS outQuantity")
               .eq(stationId != null, "warehouse_id",
                   stationId) // stationId mapped to warehouse
               .ge(startDate != null, "operate_time", startDate != null ? startDate.atStartOfDay() : null)
               .le(endDate != null, "operate_time", endDate != null ? endDate.plusDays(1).atStartOfDay() : null)
               .groupBy("product_id");
        return warehouseFlowMapper.selectMaps(wrapper);
    }

    @Override
    @Transactional
    public void settleWithSupplier(SettlementRecord record, List<SettlementDetail> details) {
        record.setType("SUPPLIER");
        record.setSettlementDate(LocalDate.now());
        record.setCreateTime(LocalDateTime.now());
        save(record);

        for (SettlementDetail detail : details) {
            detail.setSettlementId(record.getId());
            settlementDetailMapper.insert(detail);
        }
    }

    @Override
    @Transactional
    public void settleWithStation(SettlementRecord record, List<SettlementDetail> details) {
        record.setType("STATION");
        record.setSettlementDate(LocalDate.now());
        record.setCreateTime(LocalDateTime.now());
        save(record);

        for (SettlementDetail detail : details) {
            detail.setSettlementId(record.getId());
            settlementDetailMapper.insert(detail);
        }
    }
}
