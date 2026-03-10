package com.logistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.PageResult;
import com.logistics.common.Result;
import com.logistics.entity.LoginLog;
import com.logistics.entity.OperationLog;
import com.logistics.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping("/operation")
    public Result<?> operationLogs(@RequestParam(required = false) String username,
                                   @RequestParam(required = false) String operation,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        IPage<OperationLog> result = logService.pageOperationLogs(new Page<>(page, size), username, operation, startDate, endDate);
        return Result.success(new PageResult<>(result));
    }

    @GetMapping("/login")
    public Result<?> loginLogs(@RequestParam(required = false) String username,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size) {
        IPage<LoginLog> result = logService.pageLoginLogs(new Page<>(page, size), username, startDate, endDate);
        return Result.success(new PageResult<>(result));
    }

    @PostMapping("/backup")
    public Result<?> backup(@RequestBody Map<String, String> body) {
        LocalDate beforeDate = LocalDate.parse(body.get("beforeDate"));
        logService.backupLogs(beforeDate);
        return Result.success();
    }
}
