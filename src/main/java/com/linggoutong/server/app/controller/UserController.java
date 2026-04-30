package com.linggoutong.server.app.controller;

import com.linggoutong.server.app.dto.UserInfoResponse;
import com.linggoutong.server.common.result.R;
import com.linggoutong.server.common.security.LoginUser;
import com.linggoutong.server.module.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/user")
@RequiredArgsConstructor
@Tag(name = "App用户接口", description = "App端用户相关接口")
public class UserController {

    private final AuthService authService;

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public R<UserInfoResponse> getUserInfo(@AuthenticationPrincipal LoginUser loginUser) {
        UserInfoResponse response = authService.getUserInfo(loginUser.getUserId());
        return R.ok(response);
    }
}