package com.fdz.common.rest.rsarest.vo;

import lombok.Data;

@Data
public class ThirdpartyResponse<Result> {

    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 400;

    private int code;

    private String message;

    private Result data;

    public boolean isSuccess() {
        return code == SUCCESS_CODE;
    }
}
