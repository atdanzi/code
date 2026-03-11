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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
//             SysUser user1=new SysUser();
//             user1.setUsername("admin");
//            user1.setPassword("admin123");
//          return user1;
        if (user == null){
            log.info("user为null");
            return null;
        }

//        boolean matche(String password, String getPassword) {
//            // 1. 从完整密文中提取盐值
//            String salt = extractSalt(encodedPassword);  // 得到 "N.ZOn9G6LjCTGYCbMtKNfu"
//
//            // 2. 用相同的盐值和算法重新加密明文
//            String newHash = bcryptHash(rawPassword, salt);  // 使用相同的盐值
//
//            // 3. 比较新生成的哈希与原始哈希
//            return newHash.equals(extractHash(encodedPassword));
//        }

        log.info("password:{}",password);
        log.info("getpassword:{}",user.getPassword());
//        passwordEncoder.matches(password,user.getPassword());

//     if (!passwordEncoder.matches(password, user.getPassword())) {
//         log.info("密码不错误");
//         return user;
//     }

        if(!password.equals(user.getPassword())){
            log.info("密码错误");
            return null;
        }


        if(user.getStatus()!= null && user.getStatus() == 0) {
            log.info("权限不足");
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
