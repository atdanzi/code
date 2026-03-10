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
@TableName("return_order")
public class ReturnOrder {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String returnCode;
    private Long supplierId;
    private String type;
    private String status;
    private Long fromWarehouseId;
    private Long toWarehouseId;
    private LocalDate returnDate;
    private Long operatorId;
    private String remark;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String supplierName;

    @TableField(exist = false)
    private List<ReturnOrderItem> items;
}
