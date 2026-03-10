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
@TableName("transfer_order")
public class TransferOrder {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String transferCode;
    private Long fromWarehouseId;
    private Long toWarehouseId;
    private String status;
    private String relatedOrderCode;
    private String relatedTaskCode;
    private LocalDate planDate;
    private LocalDate actualOutDate;
    private LocalDate actualInDate;
    private Long operatorId;
    private String remark;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String fromWarehouseName;

    @TableField(exist = false)
    private String toWarehouseName;

    @TableField(exist = false)
    private List<TransferOrderItem> items;
}
