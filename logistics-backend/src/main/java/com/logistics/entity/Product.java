package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String productCode;
    private String name;
    private Long category1Id;
    private Long category2Id;
    private String unit;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal costPrice;
    private String model;
    private Long supplierId;
    private String manufacturer;
    private Integer shelfLife;
    private Integer canReturn;
    private Integer canExchange;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String category1Name;

    @TableField(exist = false)
    private String category2Name;

    @TableField(exist = false)
    private String supplierName;
}
