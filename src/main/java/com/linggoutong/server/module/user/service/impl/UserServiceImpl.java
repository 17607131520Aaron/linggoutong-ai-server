package com.linggoutong.server.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linggoutong.server.common.exception.BusinessException;
import com.linggoutong.server.common.exception.ResourceNotFoundException;
import com.linggoutong.server.common.result.ResultCode;
import com.linggoutong.server.common.security.SecurityUtils;
import com.linggoutong.server.module.user.dto.UserDTO;
import com.linggoutong.server.module.user.entity.User;
import com.linggoutong.server.module.user.mapper.UserMapper;
import com.linggoutong.server.module.user.service.UserService;
import com.linggoutong.server.module.user.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    @Override
    public UserVO getUserByUsername(String username) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    @Override
    @Transactional
    public UserVO updateCurrentUser(UserDTO userDTO) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User user = getById(currentUserId);
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_FOUND);
        }

        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }

        updateById(user);
        log.info("用户信息更新成功: {}", user.getUsername());
        return convertToVO(user);
    }

    @Override
    @Transactional
    public void updatePassword(String oldPassword, String newPassword) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User user = getById(currentUserId);
        if (user == null) {
            throw new ResourceNotFoundException(ResultCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED, "原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
        log.info("用户密码更新成功: {}", user.getUsername());
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
