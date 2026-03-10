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
@TableName("warehouse")
public class Warehouse {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String warehouseCode;
    private String name;
    private String address;
    private Long managerId;
    private String level;
    private Long stationId;
    private String remark;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String stationName;
}
