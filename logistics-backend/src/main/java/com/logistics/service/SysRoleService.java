package com.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    List<SysRole> listWithPermissions();

    void assignPermissions(Long roleId, List<Long> permissionIds);
}
