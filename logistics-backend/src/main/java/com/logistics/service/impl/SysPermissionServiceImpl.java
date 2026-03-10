package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.SysPermission;
import com.logistics.entity.SysRolePermission;
import com.logistics.mapper.SysPermissionMapper;
import com.logistics.mapper.SysRolePermissionMapper;
import com.logistics.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public List<SysPermission> getTreeList() {
        List<SysPermission> all = list(new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getSortOrder));
        return buildTree(all, 0L);
    }

    private List<SysPermission> buildTree(List<SysPermission> all, Long parentId) {
        List<SysPermission> tree = new ArrayList<>();
        for (SysPermission perm : all) {
            Long pid = perm.getParentId() == null ? 0L : perm.getParentId();
            if (pid.equals(parentId)) {
                perm.setChildren(buildTree(all, perm.getId()));
                tree.add(perm);
            }
        }
        return tree;
    }

    @Override
    public List<SysPermission> getPermissionsByRoleId(Long roleId) {
        List<SysRolePermission> rolePerms = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));
        if (rolePerms.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> permIds = rolePerms.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
        return listByIds(permIds);
    }
}
