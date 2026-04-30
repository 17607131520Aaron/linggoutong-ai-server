package com.linggoutong.server.module.push.service.impl;

import com.linggoutong.server.module.push.dto.PushRequest;
import com.linggoutong.server.module.push.entity.PushMessage;
import com.linggoutong.server.module.push.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PushServiceImpl implements PushService {

    private final MongoTemplate mongoTemplate;

    public PushServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @Async("pushExecutor")
    public void sendToUser(Long userId, PushRequest request) {
        log.info("发送推送到用户: {}, 标题: {}", userId, request.getTitle());

        PushMessage message = new PushMessage();
        message.setUserId(userId);
        message.setTitle(request.getTitle());
        message.setContent(request.getContent());
        message.setType(request.getType());
        message.setPlatform(request.getPlatform());
        message.setExtra(request.getExtra());
        message.setStatus("SENT");
        message.setSentAt(LocalDateTime.now());
        message.setCreatedAt(LocalDateTime.now());

        mongoTemplate.save(message);

        // TODO: 集成极光推送SDK实现实际推送
        log.info("推送已保存到MongoDB");
    }

    @Override
    @Async("pushExecutor")
    public void sendToAll(PushRequest request) {
        log.info("发送广播推送, 标题: {}", request.getTitle());

        PushMessage message = new PushMessage();
        message.setUserId(0L);
        message.setTitle(request.getTitle());
        message.setContent(request.getContent());
        message.setType(request.getType());
        message.setPlatform("ALL");
        message.setExtra(request.getExtra());
        message.setStatus("SENT");
        message.setSentAt(LocalDateTime.now());
        message.setCreatedAt(LocalDateTime.now());

        mongoTemplate.save(message);

        // TODO: 集成极光推送SDK实现广播推送
        log.info("广播推送已保存到MongoDB");
    }
}
