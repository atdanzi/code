package com.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@TableName("sys_permission")
public class SysPermission {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String permName;
    private String permCode;
    private Long parentId;
    private String path;
    private String icon;
    private Integer sortOrder;
    private Integer type;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private List<SysPermission> children;
}
