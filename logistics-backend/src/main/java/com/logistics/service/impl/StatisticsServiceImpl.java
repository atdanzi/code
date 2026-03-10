package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.logistics.entity.Orders;
import com.logistics.entity.TaskOrder;
import com.logistics.mapper.OrdersMapper;
import com.logistics.mapper.TaskOrderMapper;
import com.logistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final OrdersMapper ordersMapper;
    private final TaskOrderMapper taskOrderMapper;

    @Override
    public List<Map<String, Object>> getOrderRanking(LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.select("customer_id AS customerId", "COUNT(*) AS orderCount", "SUM(total_amount) AS totalAmount")
               .ge(startDate != null, "order_date", startDate)
               .le(endDate != null, "order_date", endDate)
               .groupBy("customer_id")
               .orderByDesc("orderCount");
        return ordersMapper.selectMaps(wrapper);
    }

    @Override
    public List<Map<String, Object>> getStationDeliveryStats(LocalDate startDate, LocalDate endDate) {
        QueryWrapper<TaskOrder> wrapper = new QueryWrapper<>();
        wrapper.select("station_id AS stationId", "COUNT(*) AS taskCount",
                       "SUM(CASE WHEN task_status='COMPLETED' THEN 1 ELSE 0 END) AS completedCount")
               .ge(startDate != null, "create_time", startDate != null ? startDate.atStartOfDay() : null)
               .le(endDate != null, "create_time", endDate != null ? endDate.plusDays(1).atStartOfDay() : null)
               .groupBy("station_id");
        return taskOrderMapper.selectMaps(wrapper);
    }

    @Override
    public List<Map<String, Object>> getSatisfactionStats(LocalDate startDate, LocalDate endDate) {
        QueryWrapper<TaskOrder> wrapper = new QueryWrapper<>();
        wrapper.select("satisfaction", "COUNT(*) AS count")
               .isNotNull("satisfaction")
               .ge(startDate != null, "complete_date", startDate)
               .le(endDate != null, "complete_date", endDate)
               .groupBy("satisfaction");
        return taskOrderMapper.selectMaps(wrapper);
    }

    @Override
    public List<Map<String, Object>> getOperatorWorkload(Long operatorId, String orderType, String productName,
                                                          LocalDate startDate, LocalDate endDate) {
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.select("operator_id AS operatorId", "order_type AS orderType", "COUNT(*) AS orderCount",
                       "SUM(total_amount) AS totalAmount")
               .eq(operatorId != null, "operator_id", operatorId)
               .eq(orderType != null, "order_type", orderType)
               .ge(startDate != null, "order_date", startDate)
               .le(endDate != null, "order_date", endDate)
               .groupBy("operator_id", "order_type");
        return ordersMapper.selectMaps(wrapper);
    }
}
