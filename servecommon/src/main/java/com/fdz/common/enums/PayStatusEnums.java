package com.fdz.common.enums;

import lombok.Getter;

@Getter
public enum PayStatusEnums {

    SUCCESS((byte) 1, "支付成功"), FAIL((byte) 2, "支付失败"), PROCESS((byte) 3, "处理中");

    private byte status;

    private String text;

    PayStatusEnums(byte status, String text) {
        this.status = status;
        this.text = text;
    }

    public static PayStatusEnums get(byte status) {
        for (PayStatusEnums payStatusEnums : PayStatusEnums.values()) {
            if (payStatusEnums.getStatus() == status) {
                return payStatusEnums;
            }
        }
        return null;
    }

}
