package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@TableName("task_order_item")
public class TaskOrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskOrderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String unit;
}
