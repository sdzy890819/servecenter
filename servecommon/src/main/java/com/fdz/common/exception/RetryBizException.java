package com.fdz.common.exception;

import lombok.Data;

@Data
public class RetryBizException extends BizException {

    public RetryBizException() {
        super();
    }

    public RetryBizException(int code, String message) {
        super(code, message);
    }
}
