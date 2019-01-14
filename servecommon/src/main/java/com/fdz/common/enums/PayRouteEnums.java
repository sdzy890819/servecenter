package com.fdz.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayRouteEnums {

    SELF((byte) 1, "自有通道");

    private byte route;

    private String text;


    public static PayRouteEnums get(byte route) {
        for (PayRouteEnums payRouteEnums : PayRouteEnums.values()) {
            if (payRouteEnums.getRoute() == route) {
                return payRouteEnums;
            }
        }
        return null;
    }
}
