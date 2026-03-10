package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/order-ranking")
    public Result<?> orderRanking(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(statisticsService.getOrderRanking(startDate, endDate));
    }

    @GetMapping("/station-delivery")
    public Result<?> stationDelivery(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(statisticsService.getStationDeliveryStats(startDate, endDate));
    }

    @GetMapping("/satisfaction")
    public Result<?> satisfaction(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(statisticsService.getSatisfactionStats(startDate, endDate));
    }

    @GetMapping("/operator-workload")
    public Result<?> operatorWorkload(@RequestParam(required = false) Long operatorId,
                                      @RequestParam(required = false) String orderType,
                                      @RequestParam(required = false) String productName,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(statisticsService.getOperatorWorkload(operatorId, orderType, productName, startDate, endDate));
    }
}
