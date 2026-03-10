package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("purchase_order_item")
public class PurchaseOrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long purchaseOrderId;
    private Long productId;
    private Integer orderQuantity;
    private Integer actualQuantity;
    private String remark;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String unit;
}
