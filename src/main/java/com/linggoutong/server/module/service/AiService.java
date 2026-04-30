package com.linggoutong.server.module.service;

import com.linggoutong.server.app.dto.ChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

public interface AiService {

    String chat(ChatRequest request);

    SseEmitter chatStream(ChatRequest request);
}
