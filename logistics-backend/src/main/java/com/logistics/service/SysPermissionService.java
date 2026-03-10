package com.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.SysPermission;

import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {

    List<SysPermission> getTreeList();

    List<SysPermission> getPermissionsByRoleId(Long roleId);
}
