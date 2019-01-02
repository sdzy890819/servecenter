package com.fdz.common.exception;


import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class TimeOutException extends RequestBizException {

    public TimeOutException(String message) {
        super(message);
    }


    public TimeOutException(Exception e) {
        super(e);
    }

    public TimeOutException(HttpStatus httpStatus, String responseBody, Exception e) {
        super(httpStatus, responseBody, e);
    }

}
