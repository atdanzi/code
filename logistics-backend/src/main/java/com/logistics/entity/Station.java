package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("station")
public class Station {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String stationCode;
    private String name;
    private String address;
    private String phone;
    private Long managerId;
    private String remark;
    private LocalDateTime createTime;
}
