package com.fdz.common.enums;

import lombok.Getter;

@Getter
public enum BussinessIds {

    PRODUCTTYPE(1), PAYSN(2), PAYMENT(3), ORDER(4);

    private int id;

    BussinessIds(int id) {
        this.id = id;
    }
}
