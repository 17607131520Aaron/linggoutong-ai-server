package com.linggoutong.server.common.mq.consumer;

import com.linggoutong.server.common.constant.RabbitMQConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.rabbitmq.host")
public class RabbitMQConsumer {

    /**
     * 监听推送队列
     */
    @RabbitListener(queues = RabbitMQConstant.PUSH_QUEUE)
    public void handlePushMessage(Object message, Channel channel, Message rabbitMessage) {
        try {
            log.info("收到推送消息: {}", message);

            // 处理推送消息...

            // 手动确认消息
            channel.basicAck(rabbitMessage.getMessageProperties().getDeliveryTag(), false);
            log.info("推送消息处理完成");
        } catch (Exception e) {
            log.error("推送消息处理失败: {}", e.getMessage(), e);
            try {
                // 消息拒绝，重新入队
                channel.basicNack(rabbitMessage.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ex) {
                log.error("消息确认失败: {}", ex.getMessage());
            }
        }
    }

    /**
     * 监听日志队列
     */
    @RabbitListener(queues = RabbitMQConstant.LOG_QUEUE)
    public void handleLogMessage(Object message, Channel channel, Message rabbitMessage) {
        try {
            log.info("收到日志消息: {}", message);

            // 处理日志消息...

            // 手动确认消息
            channel.basicAck(rabbitMessage.getMessageProperties().getDeliveryTag(), false);
            log.info("日志消息处理完成");
        } catch (Exception e) {
            log.error("日志消息处理失败: {}", e.getMessage(), e);
            try {
                // 消息拒绝，重新入队
                channel.basicNack(rabbitMessage.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ex) {
                log.error("消息确认失败: {}", ex.getMessage());
            }
        }
    }
}
