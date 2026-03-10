package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@TableName("settlement_detail")
public class SettlementDetail {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long settlementId;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer inQuantity;
    private Integer outQuantity;
    private Integer settleQuantity;
    private BigDecimal settleAmount;
}
