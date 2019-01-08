package com.fdz.common.enums;

import lombok.Getter;

@Getter
public enum OrdersStatus {

    WAIT_PAY((byte) 1, "待支付"), CONFIRM_SEND((byte) 2, "确认发货"), WAIT_REAL_SEND((byte) 3, "等待发货"), DELIVERED((byte) 4, "商家已发货"), RECEIVED((byte) 5, "已签收");
    private byte status;

    private String statusText;

    OrdersStatus(byte status, String statusText) {
        this.status = status;
        this.statusText = statusText;
    }

    public static OrdersStatus get(byte status) {
        for (OrdersStatus ordersStatus : OrdersStatus.values()) {
            if (ordersStatus.getStatus() == status) {
                return ordersStatus;
            }
        }
        return null;
    }
}
