package com.fdz.common.exception;

public class IORequestBizException extends BizException {

    public IORequestBizException(String message) {
        super(message);
    }

    public IORequestBizException(Exception e) {
        super(e);
    }
}
