package com.linggoutong.server.module.service;

import com.linggoutong.server.app.dto.LoginRequest;
import com.linggoutong.server.app.dto.LoginResponse;
import com.linggoutong.server.app.dto.RegisterRequest;

public interface AuthService {

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     */
    void sendSmsCode(String phone);

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户注册
     *
     * @param request 注册请求
     */
    void register(RegisterRequest request);
}