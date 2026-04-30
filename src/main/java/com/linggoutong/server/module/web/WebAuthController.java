package com.linggoutong.server.module.web;

import com.linggoutong.server.common.result.R;
import com.linggoutong.server.module.auth.dto.LoginRequest;
import com.linggoutong.server.module.auth.dto.LoginResponse;
import com.linggoutong.server.module.auth.dto.RegisterRequest;
import com.linggoutong.server.module.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/web/auth")
@RequiredArgsConstructor
@Tag(name = "Web端认证", description = "Web端认证接口")
public class WebAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "Web端用户登录接口")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "Web端用户注册接口")
    public R<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return R.ok(authService.register(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token", description = "Web端刷新访问令牌")
    public R<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        return R.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "Web端用户登出接口")
    public R<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return R.ok();
    }
}
