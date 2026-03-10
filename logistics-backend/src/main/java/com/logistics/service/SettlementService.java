package com.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.SettlementDetail;
import com.logistics.entity.SettlementRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SettlementService extends IService<SettlementRecord> {

    List<Map<String, Object>> querySupplierSettlement(Long supplierId, LocalDate startDate, LocalDate endDate);

    List<Map<String, Object>> queryStationSettlement(Long stationId, LocalDate startDate, LocalDate endDate);

    void settleWithSupplier(SettlementRecord record, List<SettlementDetail> details);

    void settleWithStation(SettlementRecord record, List<SettlementDetail> details);
}
