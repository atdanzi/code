package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@TableName("orders")
public class Orders {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderCode;
    private Long customerId;
    private String orderType;
    private String orderStatus;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String receiverZipCode;
    private Long deliveryStationId;
    private LocalDate requireDate;
    private LocalDate orderDate;
    private Integer needInvoice;
    private String payerName;
    private String payerAddress;
    private String payerPhone;
    private String payerZipCode;
    private String cancelReason;
    private LocalDate cancelDate;
    private Long relatedOrderId;
    private Long operatorId;
    private BigDecimal totalAmount;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String customerName;

    @TableField(exist = false)
    private String stationName;

    @TableField(exist = false)
    private String operatorName;

    @TableField(exist = false)
    private List<OrderItem> items;
}
