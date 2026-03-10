package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.entity.SysPermission;
import com.logistics.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/system/permissions")
@RequiredArgsConstructor
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;

    @GetMapping
    public Result<?> tree() {
        return Result.success(sysPermissionService.getTreeList());
    }

    @PostMapping
    public Result<?> create(@RequestBody SysPermission permission) {
        permission.setCreateTime(LocalDateTime.now());
        sysPermissionService.save(permission);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody SysPermission permission) {
        permission.setId(id);
        sysPermissionService.updateById(permission);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        sysPermissionService.removeById(id);
        return Result.success();
    }
}
