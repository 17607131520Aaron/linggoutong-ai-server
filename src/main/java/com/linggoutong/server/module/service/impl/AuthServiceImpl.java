package com.linggoutong.server.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linggoutong.server.app.dto.LoginRequest;
import com.linggoutong.server.app.dto.LoginResponse;
import com.linggoutong.server.app.dto.RegisterRequest;
import com.linggoutong.server.app.dto.UserInfoResponse;
import com.linggoutong.server.module.entity.User;
import com.linggoutong.server.module.mapper.UserMapper;
import com.linggoutong.server.module.service.AuthService;
import com.linggoutong.server.common.exception.BusinessException;
import com.linggoutong.server.common.result.ResultCode;
import com.linggoutong.server.common.security.JwtTokenProvider;
import com.linggoutong.server.common.util.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final long SMS_CODE_EXPIRE_MINUTES = 5;

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public void sendSmsCode(String phone) {
        String code = generateCode();
        
        redisTemplate.opsForValue().set(
                SMS_CODE_PREFIX + phone,
                code,
                SMS_CODE_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        log.info("发送验证码到手机 {}: {}", phone, code);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, request.getPhone())
        );

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 密码登录
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BusinessException(ResultCode.LOGIN_FAILED, "密码错误");
            }
        } else {
            // 验证码登录
            String cachedCode = redisTemplate.opsForValue().get(SMS_CODE_PREFIX + request.getPhone());
            if (cachedCode == null || !cachedCode.equals(request.getCode())) {
                throw new BusinessException(ResultCode.LOGIN_FAILED, "验证码错误或已过期");
            }
            redisTemplate.delete(SMS_CODE_PREFIX + request.getPhone());
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getPhone());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .build();
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        String cachedCode = redisTemplate.opsForValue().get(SMS_CODE_PREFIX + request.getPhone());
        
        if (cachedCode == null || !cachedCode.equals(request.getCode())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED, "验证码错误或已过期");
        }

        User existingUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, request.getPhone())
        );

        if (existingUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        User user = new User();
        user.setId(snowflakeIdGenerator.nextId());
        user.setUsername(request.getPhone());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname("用户" + request.getPhone().substring(7));
        user.setStatus(1);
        user.setDeleted(0);
        userMapper.insert(user);

        redisTemplate.delete(SMS_CODE_PREFIX + request.getPhone());

        log.info("用户注册成功: {}", request.getPhone());
    }

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        return UserInfoResponse.builder()
                .userId(user.getId())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .createTime(user.getCreateTime())
                .build();
    }

    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}