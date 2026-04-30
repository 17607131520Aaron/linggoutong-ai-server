package com.linggoutong.server.module.push.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "push_message")
public class PushMessage {

    @Id
    private String id;

    private Long userId;

    private String title;

    private String content;

    private String type;

    private String platform;

    private String status;

    private Object extra;

    private LocalDateTime sentAt;

    private LocalDateTime createdAt;
}
