package com.linggoutong.server.common.mq.producer;

import com.linggoutong.server.common.constant.RabbitMQConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@ConditionalOnBean(RabbitTemplate.class)
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到指定队列
     */
    public void sendMessage(String routingKey, Object message) {
        String correlationId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(correlationId);

        rabbitTemplate.convertAndSend(
                RabbitMQConstant.EXCHANGE_NAME,
                routingKey,
                message,
                correlationData
        );

        log.info("消息已发送, correlationId: {}, routingKey: {}", correlationId, routingKey);
    }

    /**
     * 发送推送消息
     */
    public void sendPushMessage(Object message) {
        sendMessage(RabbitMQConstant.PUSH_ROUTING_KEY, message);
    }

    /**
     * 发送日志消息
     */
    public void sendLogMessage(Object message) {
        sendMessage(RabbitMQConstant.LOG_ROUTING_KEY, message);
    }

    /**
     * 发送延迟消息
     */
    public void sendDelayMessage(String routingKey, Object message, long delay) {
        String correlationId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(correlationId);

        rabbitTemplate.convertAndSend(
                RabbitMQConstant.DELAY_EXCHANGE_NAME,
                routingKey,
                message,
                msg -> {
                    msg.getMessageProperties().setHeader("x-delay", delay);
                    return msg;
                },
                correlationData
        );

        log.info("延迟消息已发送, correlationId: {}, delay: {}ms", correlationId, delay);
    }
}
