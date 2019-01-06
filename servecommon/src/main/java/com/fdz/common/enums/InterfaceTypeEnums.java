package com.fdz.common.enums;

import lombok.Getter;

@Getter
public enum InterfaceTypeEnums {

    SYNC_PARTNER_PRODUCT((byte) 1, "同步商品接口"),
    SYNC_ORDER_INFO((byte) 2, "同步订单信息接口"),
    SYNC_ORDER_STATUS((byte) 3, "同步订单状态");

    private byte type;

    private String text;

    InterfaceTypeEnums(byte type, String text) {
        this.text = text;
        this.type = type;
    }

    public static InterfaceTypeEnums get(byte type) {
        for (InterfaceTypeEnums interfaceTypeEnums : InterfaceTypeEnums.values()) {
            if (interfaceTypeEnums.getType() == type) {
                return interfaceTypeEnums;
            }
        }
        return null;
    }

}
