package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.entity.SysUser;
import com.logistics.service.LogService;
import com.logistics.service.SysUserService;
import com.logistics.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;



@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;
    private final LogService logService;

    /**
     * 登录并用jwt令牌进行校验
     * @param body
     * @param request
     * @return
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> body, HttpServletRequest request) {

         log.info("开始登录");
        String username = body.get("username");
        log.info("用户名为{}",username);
        String password = body.get("password");
                log.info("密码为{}",password);
        String ip = request.getRemoteAddr();

        SysUser user = sysUserService.login(username, password);
        //log.info("user为{}",user);

        if (user == null) {
            log.info("登录失败！");
            logService.logLogin(null, username, ip, false);
            return Result.error("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
               log.info("成功生成token{}",token);
        logService.logLogin(user.getId(), user.getUsername(), ip, true);

        SysUser userInfo = sysUserService.getUserWithRole(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", userInfo);
        return Result.success(data);
    }

    /**
     * 查找用户信息
     * @param request
     * @return
     */
    @GetMapping("/info")
    public Result<?> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SysUser user = sysUserService.getUserWithRole(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 注册
     * @return
     */
    @PostMapping("/register")
       public Result<?> register(){



        return Result.success();
       }




}
