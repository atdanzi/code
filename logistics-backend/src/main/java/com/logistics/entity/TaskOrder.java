package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@TableName("task_order")
public class TaskOrder {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskCode;
    private Long orderId;
    private Long stationId;
    private Long deliveryPersonId;
    private String taskType;
    private String taskStatus;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private LocalDate requireDate;
    private LocalDate assignDate;
    private LocalDate completeDate;
    private String satisfaction;
    private String receiptRemark;
    private String dispatchType;
    private Long operatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String orderCode;

    @TableField(exist = false)
    private String stationName;

    @TableField(exist = false)
    private String deliveryPersonName;

    @TableField(exist = false)
    private List<TaskOrderItem> items;
}
