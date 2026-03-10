package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("inventory")
public class Inventory {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long warehouseId;
    private Long productId;
    private Integer totalQuantity;
    private Integer returnedQuantity;
    private Integer allocatedQuantity;
    private Integer availableQuantity;
    private Integer warningValue;
    private Integer maxValue;

    @TableField(exist = false)
    private String warehouseName;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String unit;
}
