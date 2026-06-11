package org.example.lease.web.admin.controller.login;


import io.jsonwebtoken.Claims;
import org.example.lease.common.login.LoginUserHolder;
import org.example.lease.common.result.Result;
import org.example.lease.common.utils.JWTUtil;
import org.example.lease.web.admin.service.LoginService;
import org.example.lease.web.admin.vo.login.CaptchaVo;
import org.example.lease.web.admin.vo.login.LoginVo;
import org.example.lease.web.admin.vo.system.user.SystemUserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台管理系统登录管理")
@RestController
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Operation(summary = "获取图形验证码")
    @GetMapping("login/captcha")
    public Result<CaptchaVo> getCaptcha() {
        CaptchaVo captchaVo = loginService.getCaptcha();
        return Result.ok(captchaVo);
    }

    @Operation(summary = "登录")
    @PostMapping("login")
    public Result<String> login(@RequestBody LoginVo loginVo) {
        String token = loginService.login(loginVo);
        return Result.ok(token);
    }

    @Operation(summary = "获取登陆用户个人信息")
    @GetMapping("info")
    public Result<SystemUserInfoVo> info(@RequestHeader("access-token")String token) {
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        SystemUserInfoVo systemUserInfoVo = loginService.getUserInfoById(userId);
        return Result.ok(systemUserInfoVo);
    }
}