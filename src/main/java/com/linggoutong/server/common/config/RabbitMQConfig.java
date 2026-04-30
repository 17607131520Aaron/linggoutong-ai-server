package com.linggoutong.server.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.exchange.delay}")
    private String delayExchangeName;

    @Value("${rabbitmq.queue.push}")
    private String pushQueueName;

    @Value("${rabbitmq.queue.log}")
    private String logQueueName;

    @Value("${rabbitmq.routing.push}")
    private String pushRoutingKey;

    @Value("${rabbitmq.routing.log}")
    private String logRoutingKey;

    // ==================== 交换机 ====================

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public CustomExchange delayExchange() {
        return new CustomExchange(
                delayExchangeName,
                "x-delayed-message",
                true,
                false,
                java.util.Map.of("x-delayed-type", "direct")
        );
    }

    // ==================== 队列 ====================

    @Bean
    public Queue pushQueue() {
        return QueueBuilder.durable(pushQueueName).build();
    }

    @Bean
    public Queue logQueue() {
        return QueueBuilder.durable(logQueueName).build();
    }

    // ==================== 绑定 ====================

    @Bean
    public Binding pushBinding(Queue pushQueue, DirectExchange exchange) {
        return BindingBuilder.bind(pushQueue).to(exchange).with(pushRoutingKey);
    }

    @Bean
    public Binding logBinding(Queue logQueue, DirectExchange exchange) {
        return BindingBuilder.bind(logQueue).to(exchange).with(logRoutingKey);
    }

    // ==================== 消息转换器 ====================

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());

        // 消息发送到交换机的回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                // 记录日志或重试
            }
        });

        // 消息从交换机路由到队列失败的回调
        rabbitTemplate.setReturnsCallback(returned -> {
            // 记录日志或重试
        });

        return rabbitTemplate;
    }
}
