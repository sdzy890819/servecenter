package com.fdz.common.enums;

import lombok.Getter;

@Getter
public enum DeliveryStatusEnums {

    DONT_DELIVERY((byte) 0, "未发货"), DELIVERY((byte) 1, "已发货");
    private byte status;

    private String statusText;

    DeliveryStatusEnums(byte status, String statusText) {
        this.status = status;
        this.statusText = statusText;
    }

    public static DeliveryStatusEnums get(byte status) {
        for (DeliveryStatusEnums ordersStatus : DeliveryStatusEnums.values()) {
            if (ordersStatus.getStatus() == status) {
                return ordersStatus;
            }
        }
        return null;
    }
}
