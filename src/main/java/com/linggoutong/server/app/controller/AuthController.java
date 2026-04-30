package com.linggoutong.server.app.controller;

import com.linggoutong.server.app.dto.LoginRequest;
import com.linggoutong.server.app.dto.LoginResponse;
import com.linggoutong.server.app.dto.RegisterRequest;
import com.linggoutong.server.app.dto.SendSmsCodeRequest;
import com.linggoutong.server.module.service.AuthService;
import com.linggoutong.server.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/app/auth")
@RequiredArgsConstructor
@Tag(name = "App认证接口", description = "App端登录注册相关接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sms-code")
    @Operation(summary = "发送短信验证码")
    public R<Void> sendSmsCode(@Valid @RequestBody SendSmsCodeRequest request) {
        authService.sendSmsCode(request.getPhone());
        return R.ok("验证码发送成功", null);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return R.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public R<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return R.ok("注册成功", null);
    }
}