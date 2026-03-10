package com.logistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.PageResult;
import com.logistics.common.Result;
import com.logistics.entity.TaskOrder;
import com.logistics.service.TaskOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskOrderController {

    private final TaskOrderService taskOrderService;

    @GetMapping
    public Result<?> page(@RequestParam(defaultValue = "1") int page,
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

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        TaskOrder task = taskOrderService.getTaskDetail(id);
        if (task == null) {
            return Result.error("任务单不存在");
        }
        return Result.success(task);
    }

    @PostMapping("/{id}/assign")
    public Result<?> assign(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        taskOrderService.assignTask(id, body.get("deliveryPersonId"));
        return Result.success();
    }

    @PostMapping("/{id}/receipt")
    public Result<?> receipt(@PathVariable Long id, @RequestBody Map<String, String> body) {
        taskOrderService.enterReceipt(id, body.get("status"), body.get("satisfaction"), body.get("remark"));
        return Result.success();
    }

    @GetMapping("/payment")
    public Result<?> payment(@RequestParam(required = false) Long stationId,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                             @RequestParam(required = false) String productName) {
        IPage<TaskOrder> result = taskOrderService.pageTasks(new Page<>(1, 1000), null, null, "COMPLETED", stationId, startDate, endDate);
        return Result.success(new PageResult<>(result));
    }
}
