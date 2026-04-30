package com.linggoutong.server.module.web;

import com.linggoutong.server.common.result.R;
import com.linggoutong.server.module.push.dto.PushRequest;
import com.linggoutong.server.module.push.service.PushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/web/push")
@RequiredArgsConstructor
@Tag(name = "Web端推送", description = "Web端推送接口")
public class WebPushController {

    private final PushService pushService;

    @PostMapping("/send/user/{userId}")
    @Operation(summary = "发送推送到指定用户", description = "向指定用户发送推送")
    public R<Void> sendToUser(@PathVariable Long userId, @Valid @RequestBody PushRequest request) {
        pushService.sendToUser(userId, request);
        return R.ok();
    }

    @PostMapping("/send/all")
    @Operation(summary = "发送广播推送", description = "向所有用户发送广播推送")
    public R<Void> sendToAll(@Valid @RequestBody PushRequest request) {
        pushService.sendToAll(request);
        return R.ok();
    }
}
