package com.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.logistics.entity.SysPermission;
import com.logistics.entity.SysRole;
import com.logistics.entity.SysRolePermission;
import com.logistics.entity.SysUser;
import com.logistics.entity.SysUserRole;
import com.logistics.mapper.SysRoleMapper;
import com.logistics.mapper.SysRolePermissionMapper;
import com.logistics.mapper.SysPermissionMapper;
import com.logistics.mapper.SysUserMapper;
import com.logistics.mapper.SysUserRoleMapper;
import com.logistics.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public SysUser login(String username, String password) {
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return null;
        }
        return user;
    }

    @Override
    public SysUser getUserWithRole(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            return null;
        }
        user.setPassword(null);

        SysUserRole userRole = sysUserRoleMapper.selectOne(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (userRole != null) {
            SysRole role = sysRoleMapper.selectById(userRole.getRoleId());
            if (role != null) {
                user.setRoleName(role.getRoleName());
                user.setRoleCode(role.getRoleCode());

                List<SysRolePermission> rolePerms = sysRolePermissionMapper.selectList(
                        new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, role.getId()));
                if (!rolePerms.isEmpty()) {
                    List<Long> permIds = rolePerms.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
                    List<SysPermission> permissions = sysPermissionMapper.selectBatchIds(permIds);
                    user.setPermissions(permissions);
                }
            }
        }
        return user;
    }

    @Override
    public IPage<SysUser> pageUsers(Page<SysUser> page, String username, String realName) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysUser::getUsername, username)
               .like(StringUtils.hasText(realName), SysUser::getRealName, realName)
               .orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> result = page(page, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return result;
    }
}
