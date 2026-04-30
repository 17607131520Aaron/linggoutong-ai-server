package com.linggoutong.server.module.app;

import com.linggoutong.server.common.result.R;
import com.linggoutong.server.common.security.SecurityUtils;
import com.linggoutong.server.module.user.dto.UserDTO;
import com.linggoutong.server.module.user.service.UserService;
import com.linggoutong.server.module.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/app/user")
@RequiredArgsConstructor
@Tag(name = "App端用户", description = "App端用户接口")
public class AppUserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public R<UserVO> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(userService.getUserById(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "更新当前用户信息", description = "更新当前登录用户的信息")
    public R<UserVO> updateCurrentUser(@Valid @RequestBody UserDTO userDTO) {
        return R.ok(userService.updateCurrentUser(userDTO));
    }

    @PutMapping("/me/password")
    @Operation(summary = "修改密码", description = "修改当前用户的密码")
    public R<Void> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        userService.updatePassword(oldPassword, newPassword);
        return R.ok();
    }
}
