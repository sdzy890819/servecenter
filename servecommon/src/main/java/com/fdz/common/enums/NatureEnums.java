package com.fdz.common.enums;

import lombok.Getter;

@Getter
public enum NatureEnums {
    COMPANY((byte) 1, "企业/公司"), PERSON((byte) 2, "个人工商户"), OTHER((byte) 3, "其他");
    private byte nature;

    private String text;

    NatureEnums(byte nature, String text) {
        this.nature = nature;
        this.text = text;
    }


    public static NatureEnums get(byte nature) {
        for (NatureEnums natureEnums : NatureEnums.values()) {
            if (natureEnums.getNature() == nature) {
                return natureEnums;
            }
        }
        return null;
    }
}
