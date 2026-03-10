package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("supplier")
public class Supplier {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String supplierCode;
    private String name;
    private String address;
    private String contactPerson;
    private String phone;
    private String bank;
    private String bankAccount;
    private String fax;
    private String zipCode;
    private String legalPerson;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
