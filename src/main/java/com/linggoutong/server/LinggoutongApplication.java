package com.linggoutong.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@MapperScan("com.linggoutong.server.module.mapper")
public class LinggoutongApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinggoutongApplication.class, args);
    }
}
