package com.fdz.order.vo;

import lombok.Data;

@Data
public class OrdersLogisticsPushVo {

    private String receiver;

    private String receiverProvince;

    private String receiverCity;

    private String receiverArea;

    private String receiverAddress;

    private String receiverMobile;

    private String logistics;

    private String logisticsSn;

    private String logisticsStatus;

    private Byte deliveryStatus;

    private Byte businessDeliveryStatus;
}
