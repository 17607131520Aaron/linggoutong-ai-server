package com.linggoutong.server.common.exception;

import com.linggoutong.server.common.result.ResultCode;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(ResultCode.USER_NOT_FOUND.getCode(), message);
    }

    public ResourceNotFoundException(ResultCode resultCode) {
        super(resultCode);
    }
}
