package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("transfer_order_item")
public class TransferOrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long transferOrderId;
    private Long productId;
    private Integer quantity;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String unit;
}
