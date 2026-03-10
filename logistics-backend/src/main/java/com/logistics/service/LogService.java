package com.logistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.entity.LoginLog;
import com.logistics.entity.OperationLog;

import java.time.LocalDate;

public interface LogService {

    void logOperation(Long userId, String username, String operation, String method, String params, String ip);

    void logLogin(Long userId, String username, String ip, boolean success);

    IPage<OperationLog> pageOperationLogs(Page<OperationLog> page, String username, String operation,
                                           LocalDate startDate, LocalDate endDate);

    IPage<LoginLog> pageLoginLogs(Page<LoginLog> page, String username, LocalDate startDate, LocalDate endDate);

    void backupLogs(LocalDate beforeDate);
}
