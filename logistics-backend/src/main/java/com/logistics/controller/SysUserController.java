package com.logistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.logistics.common.PageResult;
import com.logistics.common.Result;
import com.logistics.entity.SysUser;
import com.logistics.entity.SysUserRole;
import com.logistics.mapper.SysUserRoleMapper;
import com.logistics.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/system/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping
    public Result<?> page(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String username,
                          @RequestParam(required = false) String realName) {
        IPage<SysUser> result = sysUserService.pageUsers(new Page<>(page, size), username, realName);
        return Result.success(new PageResult<>(result));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getUserWithRole(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @PostMapping
    public Result<?> create(@RequestBody SysUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserService.save(user);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        user.setPassword(null);
        user.setUpdateTime(LocalDateTime.now());
        sysUserService.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.success();
    }

    @PutMapping("/{id}/role")
    public Result<?> assignRole(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long roleId = body.get("roleId");
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUserRole> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, id);
        sysUserRoleMapper.delete(wrapper);

        if (roleId != null) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(id);
            ur.setRoleId(roleId);
            sysUserRoleMapper.insert(ur);
        }
        return Result.success();
    }
}
