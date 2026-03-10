package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.entity.SysUser;
import com.logistics.service.LogService;
import com.logistics.service.SysUserService;
import com.logistics.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;
    private final LogService logService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String username = body.get("username");
        String password = body.get("password");
        String ip = request.getRemoteAddr();

        SysUser user = sysUserService.login(username, password);
        if (user == null) {
            logService.logLogin(null, username, ip, false);
            return Result.error("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        logService.logLogin(user.getId(), user.getUsername(), ip, true);

        SysUser userInfo = sysUserService.getUserWithRole(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", userInfo);
        return Result.success(data);
    }

    @GetMapping("/info")
    public Result<?> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SysUser user = sysUserService.getUserWithRole(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }
}
