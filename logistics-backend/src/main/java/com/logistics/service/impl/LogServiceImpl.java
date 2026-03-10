package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.entity.LoginLog;
import com.logistics.entity.OperationLog;
import com.logistics.mapper.LoginLogMapper;
import com.logistics.mapper.OperationLogMapper;
import com.logistics.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final OperationLogMapper operationLogMapper;
    private final LoginLogMapper loginLogMapper;

    @Override
    public void logOperation(Long userId, String username, String operation, String method, String params, String ip) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation(operation);
        log.setMethod(method);
        log.setParams(params);
        log.setIp(ip);
        log.setOperateTime(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    @Override
    public void logLogin(Long userId, String username, String ip, boolean success) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setIp(ip);
        log.setLoginTime(LocalDateTime.now());
        log.setStatus(success ? 1 : 0);
        loginLogMapper.insert(log);
    }

    @Override
    public IPage<OperationLog> pageOperationLogs(Page<OperationLog> page, String username, String operation,
                                                   LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), OperationLog::getUsername, username)
               .like(StringUtils.hasText(operation), OperationLog::getOperation, operation)
               .ge(startDate != null, OperationLog::getOperateTime, startDate != null ? startDate.atStartOfDay() : null)
               .le(endDate != null, OperationLog::getOperateTime, endDate != null ? endDate.plusDays(1).atStartOfDay() : null)
               .orderByDesc(OperationLog::getOperateTime);
        return operationLogMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<LoginLog> pageLoginLogs(Page<LoginLog> page, String username, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), LoginLog::getUsername, username)
               .ge(startDate != null, LoginLog::getLoginTime, startDate != null ? startDate.atStartOfDay() : null)
               .le(endDate != null, LoginLog::getLoginTime, endDate != null ? endDate.plusDays(1).atStartOfDay() : null)
               .orderByDesc(LoginLog::getLoginTime);
        return loginLogMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void backupLogs(LocalDate beforeDate) {
        LocalDateTime beforeDateTime = beforeDate.atStartOfDay();
        operationLogMapper.delete(
                new LambdaQueryWrapper<OperationLog>().lt(OperationLog::getOperateTime, beforeDateTime));
    }
}
