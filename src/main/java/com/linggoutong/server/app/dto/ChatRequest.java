package com.linggoutong.server.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ChatRequest {

    @NotEmpty(message = "消息列表不能为空")
    private List<ChatMessage> messages;

    private String model;

    private Double temperature;

    private Integer maxTokens;
}
