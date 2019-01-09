package com.fdz.order.dto;

import lombok.Data;

@Data
public class OrdersLogisticsResult {

    private String receiver;

    private String receiverAddress;

    private String receiverMobile;

    private String logistics;

    private String logisticsSn;

    private String logisticsStatus;

    private Byte deliveryStatus;

    private Byte businessDeliveryStatus;
}
