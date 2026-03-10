package com.logistics.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatisticsService {

    List<Map<String, Object>> getOrderRanking(LocalDate startDate, LocalDate endDate);

    List<Map<String, Object>> getStationDeliveryStats(LocalDate startDate, LocalDate endDate);

    List<Map<String, Object>> getSatisfactionStats(LocalDate startDate, LocalDate endDate);

    List<Map<String, Object>> getOperatorWorkload(Long operatorId, String orderType, String productName,
                                                   LocalDate startDate, LocalDate endDate);
}
