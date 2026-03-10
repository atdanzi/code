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
@TableName("purchase_order")
public class PurchaseOrder {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String purchaseCode;
    private Long supplierId;
    private String status;
    private LocalDate purchaseDate;
    private Long operatorId;
    private String remark;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String supplierName;

    @TableField(exist = false)
    private List<PurchaseOrderItem> items;
}
