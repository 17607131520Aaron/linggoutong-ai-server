package com.linggoutong.server.common.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class DefaultUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 默认实现，后续业务代码中替换
        return User.builder()
                .username("admin")
                .password("$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PQm0gFaE3BYfhCNMWt3GK")
                .authorities(Collections.emptyList())
                .build();
    }
}
