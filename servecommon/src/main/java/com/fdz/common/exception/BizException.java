package com.fdz.common.exception;

import com.fdz.common.constant.Constants;
import lombok.Data;

@Data
public class BizException extends RuntimeException {

    private int code = Constants.BusinessCode.UNKNOWN_CODE;

    private String message = Constants.BusinessCode.UNKNOWN_MESSAGE;

    public BizException(Exception e) {
        super(e);
    }

    public BizException() {

    }

    public BizException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BizException(String message, Exception e) {
        super(e);
        this.message = message;
    }

    public BizException(int code) {
        super();
        this.code = code;
    }

    public BizException(String message) {
        super(message);
        this.message = message;
    }

}
