package com.linggoutong.server.common.constant;

public class RabbitMQConstant {

    // 交换机
    public static final String EXCHANGE_NAME = "linggoutong.exchange";
    public static final String DELAY_EXCHANGE_NAME = "linggoutong.delay.exchange";

    // 队列
    public static final String PUSH_QUEUE = "linggoutong.push.queue";
    public static final String LOG_QUEUE = "linggoutong.log.queue";

    // 路由键
    public static final String PUSH_ROUTING_KEY = "linggoutong.push.routing";
    public static final String LOG_ROUTING_KEY = "linggoutong.log.routing";

    private RabbitMQConstant() {
    }
}
