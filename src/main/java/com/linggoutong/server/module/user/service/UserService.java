package com.linggoutong.server.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linggoutong.server.module.user.dto.UserDTO;
import com.linggoutong.server.module.user.entity.User;
import com.linggoutong.server.module.user.vo.UserVO;

public interface UserService extends IService<User> {

    UserVO getUserById(Long id);

    UserVO getUserByUsername(String username);

    UserVO updateCurrentUser(UserDTO userDTO);

    void updatePassword(String oldPassword, String newPassword);
}
