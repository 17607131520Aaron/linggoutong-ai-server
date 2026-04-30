package com.linggoutong.server.common.constant;

public class RedisKeyConstant {

    public static final String PREFIX = "lg:";

    public static final String USER_TOKEN = PREFIX + "user:token:";
    public static final String REFRESH_TOKEN = PREFIX + "user:refresh:";
    public static final String CAPTCHA = PREFIX + "captcha:";
    public static final String CONFIG = PREFIX + "config:";
    public static final String RATE_LIMIT = PREFIX + "rate:api:";
    public static final String DEVICE_TOKEN = PREFIX + "device:";
    public static final String USER_INFO = PREFIX + "user:info:";

    private RedisKeyConstant() {
    }
}
