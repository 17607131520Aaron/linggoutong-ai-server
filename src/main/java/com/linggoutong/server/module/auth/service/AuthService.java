package com.linggoutong.server.module.auth.service;

import com.linggoutong.server.module.auth.dto.LoginRequest;
import com.linggoutong.server.module.auth.dto.LoginResponse;
import com.linggoutong.server.module.auth.dto.RegisterRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse register(RegisterRequest request);

    LoginResponse refreshToken(String refreshToken);

    void logout(String accessToken);
}
