package com.fdz.common.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class RestResponse<T> {

    public static final int ERROR_CODE = -1;

    public static final String DEFAUL_ERROR_MSG = "未知错误";

    public static final int DEFAULT_SUCCESS = 0;

    public static final String DEFAULT_SUCCESS_MSG = "成功";


    private int code;


    private String message;


    private T data;

    public static <T> RestResponse error() {
        RestResponse restResponse = new RestResponse();
        restResponse.setCode(ERROR_CODE);
        restResponse.setMessage(DEFAUL_ERROR_MSG);
        return restResponse;
    }

    public RestResponse() {
        this(DEFAULT_SUCCESS, null);
    }

    public RestResponse(int code, String message) {
        this(code, message, null);
    }

    public RestResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static <T> RestResponse build(int code, String msg, T data) {
        return new RestResponse(code, msg, data);
    }

    public static <T> RestResponse error(int code, String msg) {
        return new RestResponse(code, msg, null);
    }

    public static <T> RestResponse error(String msg) {
        return new RestResponse(ERROR_CODE, msg);
    }

    public static <T> RestResponse success(T data) {
        return new RestResponse(DEFAULT_SUCCESS, DEFAULT_SUCCESS_MSG, data);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return code == DEFAULT_SUCCESS;
    }

}
