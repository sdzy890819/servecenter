package com.fdz.common.enums;

import lombok.Getter;

@Getter
public enum PaymentTypeEnums {

    RECHARGE((byte) 1, "充值"), CASH((byte) 2, "提现"), PAY((byte) 3, "支付货款"), INFO((byte) 4, "信息费");

    private byte type;

    private String text;

    PaymentTypeEnums(byte type, String text) {
        this.text = text;
        this.type = type;
    }

    public static PaymentTypeEnums get(byte type) {
        for (PaymentTypeEnums paymentTypeEnums : PaymentTypeEnums.values()) {
            if (paymentTypeEnums.getType() == type) {
                return paymentTypeEnums;
            }
        }
        return null;
    }
}
