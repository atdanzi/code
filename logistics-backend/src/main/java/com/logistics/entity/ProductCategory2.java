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
@TableName("product_category2")
public class ProductCategory2 {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Long category1Id;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String category1Name;
}
