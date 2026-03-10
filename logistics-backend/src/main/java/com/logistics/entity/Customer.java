package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("customer")
public class Customer {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String customerCode;
    private String name;
    private String idCard;
    private String company;
    private String phone;
    private String mobile;
    private String address;
    private String zipCode;
    private String email;
    private Long createBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
