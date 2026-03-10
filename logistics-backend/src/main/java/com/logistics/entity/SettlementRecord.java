package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("settlement_record")
public class SettlementRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;
    private Long targetId;
    private String targetName;
    private BigDecimal totalAmount;
    private LocalDate settlementDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long operatorId;
    private String remark;
    private LocalDateTime createTime;
}
