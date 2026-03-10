package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.entity.SysPermission;
import com.logistics.entity.SysRole;
import com.logistics.service.SysPermissionService;
import com.logistics.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/roles")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;
    private final SysPermissionService sysPermissionService;

    @GetMapping
    public Result<?> list() {
        return Result.success(sysRoleService.listWithPermissions());
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    @PostMapping
    public Result<?> create(@RequestBody SysRole role) {
        role.setCreateTime(LocalDateTime.now());
        sysRoleService.save(role);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        sysRoleService.updateById(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        if (role != null && role.getIsSystem() != null && role.getIsSystem() == 1) {
            return Result.error("系统内置角色不能删除");
        }
        sysRoleService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}/permissions")
    public Result<?> getPermissions(@PathVariable Long id) {
        List<SysPermission> permissions = sysPermissionService.getPermissionsByRoleId(id);
        return Result.success(permissions);
    }

    @SuppressWarnings("unchecked")
    @PutMapping("/{id}/permissions")
    public Result<?> assignPermissions(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        List<Long> permissionIds = (List<Long>) body.get("permissionIds");
        sysRoleService.assignPermissions(id, permissionIds);
        return Result.success();
    }
}
