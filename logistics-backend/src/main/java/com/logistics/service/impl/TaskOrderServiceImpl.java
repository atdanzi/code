package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.Orders;
import com.logistics.entity.TaskOrder;
import com.logistics.entity.TaskOrderItem;
import com.logistics.mapper.OrdersMapper;
import com.logistics.mapper.TaskOrderItemMapper;
import com.logistics.mapper.TaskOrderMapper;
import com.logistics.service.TaskOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskOrderServiceImpl extends ServiceImpl<TaskOrderMapper, TaskOrder> implements TaskOrderService {

    private final TaskOrderItemMapper taskOrderItemMapper;
    private final OrdersMapper ordersMapper;

    @Override
    public IPage<TaskOrder> pageTasks(Page<TaskOrder> page, String taskCode, String taskType, String taskStatus,
                                       Long stationId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<TaskOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(taskCode), TaskOrder::getTaskCode, taskCode)
               .eq(StringUtils.hasText(taskType), TaskOrder::getTaskType, taskType)
               .eq(StringUtils.hasText(taskStatus), TaskOrder::getTaskStatus, taskStatus)
               .eq(stationId != null, TaskOrder::getStationId, stationId)
               .ge(startDate != null, TaskOrder::getCreateTime, startDate != null ? startDate.atStartOfDay() : null)
               .le(endDate != null, TaskOrder::getCreateTime, endDate != null ? endDate.plusDays(1).atStartOfDay() : null)
               .orderByDesc(TaskOrder::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public TaskOrder getTaskDetail(Long taskId) {
        TaskOrder task = getById(taskId);
        if (task == null) {
            return null;
        }
        List<TaskOrderItem> items = taskOrderItemMapper.selectList(
                new LambdaQueryWrapper<TaskOrderItem>().eq(TaskOrderItem::getTaskOrderId, taskId));
        task.setItems(items);
        return task;
    }

    @Override
    @Transactional
    public void assignTask(Long taskId, Long deliveryPersonId) {
        TaskOrder task = getById(taskId);
        if (task == null) {
            throw new RuntimeException("任务单不存在");
        }
        task.setDeliveryPersonId(deliveryPersonId);
        task.setTaskStatus("ASSIGNED");
        task.setAssignDate(LocalDate.now());
        task.setUpdateTime(LocalDateTime.now());
        updateById(task);

        Orders order = ordersMapper.selectById(task.getOrderId());
        if (order != null) {
            order.setOrderStatus("TASK_ASSIGNED");
            order.setUpdateTime(LocalDateTime.now());
            ordersMapper.updateById(order);
        }
    }

    @Override
    @Transactional
    public void enterReceipt(Long taskId, String status, String satisfaction, String remark) {
        TaskOrder task = getById(taskId);
        if (task == null) {
            throw new RuntimeException("任务单不存在");
        }
        task.setTaskStatus(status);
        task.setSatisfaction(satisfaction);
        task.setReceiptRemark(remark);
        task.setCompleteDate(LocalDate.now());
        task.setUpdateTime(LocalDateTime.now());
        updateById(task);

        Orders order = ordersMapper.selectById(task.getOrderId());
        if (order != null) {
            switch (status) {
                case "COMPLETED" -> order.setOrderStatus("COMPLETED");
                case "PARTIAL" -> order.setOrderStatus("PARTIAL_COMPLETED");
                case "FAILED" -> order.setOrderStatus("DELIVERY_FAILED");
            }
            order.setUpdateTime(LocalDateTime.now());
            ordersMapper.updateById(order);
        }
    }
}
