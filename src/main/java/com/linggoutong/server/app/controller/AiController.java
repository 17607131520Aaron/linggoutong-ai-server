package com.linggoutong.server.app.controller;

import com.linggoutong.server.app.dto.ChatRequest;
import com.linggoutong.server.common.result.R;
import com.linggoutong.server.module.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "AI对话接口")
@RestController
@RequestMapping("/api/app/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @Operation(summary = "普通对话")
    @PostMapping("/chat")
    public R<String> chat(@Valid @RequestBody ChatRequest request) {
        String response = aiService.chat(request);
        return R.ok(response);
    }

    @Operation(summary = "SSE流式对话")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@Valid @RequestBody ChatRequest request) {
        return aiService.chatStream(request);
    }
}
