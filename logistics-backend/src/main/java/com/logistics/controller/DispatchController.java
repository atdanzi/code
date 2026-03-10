package com.logistics.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.PageResult;
import com.logistics.common.Result;
import com.logistics.entity.Orders;
import com.logistics.entity.ShortageRecord;
import com.logistics.entity.TaskOrder;
import com.logistics.mapper.ShortageRecordMapper;
import com.logistics.service.DispatchService;
import com.logistics.service.OrderService;
import com.logistics.service.TaskOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;
    private final OrderService orderService;
    private final TaskOrderService taskOrderService;
    private final ShortageRecordMapper shortageRecordMapper;

    @GetMapping("/orders")
    public Result<?> availableOrders(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        IPage<Orders> result = orderService.pageOrders(new Page<>(page, size), null, null, "AVAILABLE", null, null, null);
        return Result.success(new PageResult<>(result));
    }

    @PostMapping("/manual")
    public Result<?> manualDispatch(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");
        Long orderId = ((Number) body.get("orderId")).longValue();
        Long stationId = ((Number) body.get("stationId")).longValue();
        dispatchService.manualDispatch(orderId, stationId, operatorId);
        return Result.success();
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/auto")
    public Result<?> autoDispatch(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");
        List<Number> ids = (List<Number>) body.get("orderIds");
        List<Long> orderIds = ids.stream().map(Number::longValue).toList();
        dispatchService.autoDispatch(orderIds, operatorId);
        return Result.success();
    }

    @GetMapping("/shortage")
    public Result<?> shortageOrders() {
        List<ShortageRecord> records = shortageRecordMapper.selectList(
                new LambdaQueryWrapper<ShortageRecord>().eq(ShortageRecord::getResolved, 0));
        return Result.success(records);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/shortage/resolve")
    public Result<?> resolveShortage(@RequestBody Map<String, Object> body) {
        List<Number> ids = (List<Number>) body.get("orderIds");
        List<Long> orderIds = ids.stream().map(Number::longValue).toList();
        dispatchService.resolveShortage(orderIds);
        return Result.success();
    }

    @GetMapping("/tasks")
    public Result<?> pageTasks(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) String taskCode,
                               @RequestParam(required = false) String taskType,
                               @RequestParam(required = false) String taskStatus,
                               @RequestParam(required = false) Long stationId,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        IPage<TaskOrder> result = taskOrderService.pageTasks(new Page<>(page, size), taskCode, taskType, taskStatus, stationId, startDate, endDate);
        return Result.success(new PageResult<>(result));
    }
}
