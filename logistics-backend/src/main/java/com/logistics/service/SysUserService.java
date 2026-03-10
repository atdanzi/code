package com.logistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.logistics.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    SysUser login(String username, String password);

    SysUser getUserWithRole(Long userId);

    IPage<SysUser> pageUsers(Page<SysUser> page, String username, String realName);
}
