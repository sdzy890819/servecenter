package com.fdz.common.enums;

import lombok.Getter;

@Getter
public enum OrdersFinishStatus {

    PROCESSING((byte) 1, "流程中"), FINISHED((byte) 2, "已完成");
    private byte status;

    private String statusText;

    OrdersFinishStatus(byte status, String statusText) {
        this.status = status;
        this.statusText = statusText;
    }

    public static OrdersFinishStatus get(byte status) {
        for (OrdersFinishStatus ordersStatus : OrdersFinishStatus.values()) {
            if (ordersStatus.getStatus() == status) {
                return ordersStatus;
            }
        }
        return null;
    }
}
