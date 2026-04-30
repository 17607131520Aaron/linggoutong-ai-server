package com.linggoutong.server.module.push.controller.app;

import com.linggoutong.server.common.result.R;
import com.linggoutong.server.common.security.SecurityUtils;
import com.linggoutong.server.module.push.dto.PushRequest;
import com.linggoutong.server.module.push.service.PushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/app/push")
@RequiredArgsConstructor
@Tag(name = "App端推送", description = "App端推送接口")
public class AppPushController {

    private final PushService pushService;

    @PostMapping("/send")
    @Operation(summary = "发送推送", description = "向当前用户发送推送")
    public R<Void> sendToCurrentUser(@Valid @RequestBody PushRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        pushService.sendToUser(userId, request);
        return R.ok();
    }
}
