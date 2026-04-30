package com.linggoutong.server.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),

    // 认证相关 1xxx
    UNAUTHORIZED(1001, "未认证"),
    TOKEN_EXPIRED(1002, "Token已过期"),
    TOKEN_INVALID(1003, "Token无效"),
    LOGIN_FAILED(1004, "用户名或密码错误"),
    ACCOUNT_DISABLED(1005, "账号已禁用"),
    ACCESS_DENIED(1006, "权限不足"),

    // 参数相关 2xxx
    PARAM_ERROR(2001, "参数错误"),
    PARAM_MISSING(2002, "参数缺失"),
    PARAM_TYPE_ERROR(2003, "参数类型错误"),

    // 业务相关 3xxx
    USER_NOT_FOUND(3001, "用户不存在"),
    USER_ALREADY_EXISTS(3002, "用户已存在"),
    FILE_NOT_FOUND(3003, "文件不存在"),
    FILE_UPLOAD_FAILED(3004, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(3005, "文件类型不允许"),
    FILE_SIZE_EXCEEDED(3006, "文件大小超出限制"),

    // 系统相关 9xxx
    SYSTEM_ERROR(9001, "系统内部错误"),
    SERVICE_UNAVAILABLE(9002, "服务不可用"),
    REQUEST_TOO_FREQUENT(9003, "请求过于频繁");

    private final int code;
    private final String message;
}
