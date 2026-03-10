package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("warehouse_flow")
public class WarehouseFlow {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long warehouseId;
    private Long productId;
    private String type;
    private String subType;
    private Integer quantity;
    private String relatedOrderNo;
    private Long operatorId;
    private LocalDateTime operateTime;
    private String remark;

    @TableField(exist = false)
    private String warehouseName;

    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String operatorName;
}
