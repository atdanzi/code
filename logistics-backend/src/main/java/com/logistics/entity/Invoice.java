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

@Data
@NoArgsConstructor
@TableName("invoice")
public class Invoice {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String invoiceNo;
    private BigDecimal amount;
    private String status;
    private LocalDate registerDate;
    private LocalDate stationReceiveDate;
    private LocalDate customerReceiveDate;
    private LocalDate voidDate;
    private Long stationId;
    private Long taskOrderId;
    private String lostPerson;
    private String remark;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String stationName;
}
