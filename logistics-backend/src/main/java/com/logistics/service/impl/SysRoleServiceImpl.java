package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.SysPermission;
import com.logistics.entity.SysRole;
import com.logistics.entity.SysRolePermission;
import com.logistics.mapper.SysPermissionMapper;
import com.logistics.mapper.SysRoleMapper;
import com.logistics.mapper.SysRolePermissionMapper;
import com.logistics.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public List<SysRole> listWithPermissions() {
        List<SysRole> roles = list();
        for (SysRole role : roles) {
            List<SysRolePermission> rolePerms = sysRolePermissionMapper.selectList(
                    new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, role.getId()));
            if (!rolePerms.isEmpty()) {
                List<Long> permIds = rolePerms.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
                List<SysPermission> permissions = sysPermissionMapper.selectBatchIds(permIds);
                role.setPermissions(permissions);
            }
        }
        return roles;
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permId : permissionIds) {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permId);
                sysRolePermissionMapper.insert(rp);
            }
        }
    }
}
