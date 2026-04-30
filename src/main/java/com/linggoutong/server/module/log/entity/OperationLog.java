package com.linggoutong.server.module.log.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "operation_log")
public class OperationLog {

    @Id
    private String id;

    private Long userId;

    private String username;

    private String module;

    private String operation;

    private String method;

    private String url;

    private String ip;

    private String params;

    private Long executionTime;

    private Integer status;

    private String errorMsg;

    private LocalDateTime createTime;
}
