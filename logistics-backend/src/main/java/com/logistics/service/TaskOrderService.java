package com.logistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.TaskOrder;

import java.time.LocalDate;

public interface TaskOrderService extends IService<TaskOrder> {

    IPage<TaskOrder> pageTasks(Page<TaskOrder> page, String taskCode, String taskType, String taskStatus,
                               Long stationId, LocalDate startDate, LocalDate endDate);

    TaskOrder getTaskDetail(Long taskId);

    void assignTask(Long taskId, Long deliveryPersonId);

    void enterReceipt(Long taskId, String status, String satisfaction, String remark);
}
