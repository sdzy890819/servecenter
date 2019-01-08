package com.fdz.order.vo;

import lombok.Data;

@Data
public class OrderStatusPushVo {

    private String sn;

    private String orderSn;

    private Byte status;

}
