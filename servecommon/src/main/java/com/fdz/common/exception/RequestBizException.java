package com.fdz.common.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class RequestBizException extends BizException {

    private String requestBody;

    private HttpStatus httpStatus;

    public RequestBizException(String requestBody, Exception e) {
        super(e);
        this.requestBody = requestBody;
    }

    public RequestBizException(String requestBody, int code, String message) {
        super(code, message);
        this.requestBody = requestBody;
    }

    public RequestBizException(String requestBody) {
        super();
        this.requestBody = requestBody;
    }

    public RequestBizException(HttpStatus httpStatus, String requestBody, Exception e) {
        super(e);
        this.httpStatus = httpStatus;
        this.requestBody = requestBody;
    }

    public RequestBizException(HttpStatus httpStatus, String requestBody, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.requestBody = requestBody;
    }

    public RequestBizException(Exception e) {
        super(e);
    }

}
