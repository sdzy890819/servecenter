package com.fdz.common.exception;

import lombok.Data;

@Data
public class AccessBizException extends BizException {

    public AccessBizException(int code, String message) {
        super(code, message);
    }

    public AccessBizException(String message) {
        super(message);
    }
}
