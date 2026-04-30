package com.linggoutong.server.module.auth.service.impl;

import com.linggoutong.server.common.exception.BusinessException;
import com.linggoutong.server.common.result.ResultCode;
import com.linggoutong.server.common.security.JwtTokenProvider;
import com.linggoutong.server.common.security.LoginUser;
import com.linggoutong.server.common.util.RedisUtil;
import com.linggoutong.server.module.auth.dto.LoginRequest;
import com.linggoutong.server.module.auth.dto.LoginResponse;
import com.linggoutong.server.module.auth.dto.RegisterRequest;
import com.linggoutong.server.module.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           PasswordEncoder passwordEncoder,
                           @Autowired(required = false) RedisUtil redisUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.redisUtil = redisUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(loginUser.getId(), loginUser.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(loginUser.getId());

        log.info("用户登录成功: {}", loginUser.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(7200)
                .userId(loginUser.getId())
                .username(loginUser.getUsername())
                .nickname(loginUser.getNickname())
                .build();
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        // TODO: 实现注册逻辑
        throw new BusinessException(ResultCode.FAILED, "注册功能待实现");
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        // TODO: 实现刷新Token逻辑
        throw new BusinessException(ResultCode.FAILED, "刷新Token功能待实现");
    }

    @Override
    public void logout(String accessToken) {
        // TODO: 实现登出逻辑
        log.info("用户登出");
    }
}
