package com.linggoutong.server.module.service;

import com.linggoutong.server.app.dto.LoginRequest;
import com.linggoutong.server.app.dto.LoginResponse;
import com.linggoutong.server.app.dto.RegisterRequest;
import com.linggoutong.server.app.dto.UserInfoResponse;

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

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoResponse getUserInfo(Long userId);
}