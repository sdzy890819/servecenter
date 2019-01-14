package com.fdz.order.dto;

import lombok.Data;

@Data
public class DeliveryInfo {

    private String date;

    private String partnerName;

    private Long partnerId;

    private String waitDeliveryAndAmount;

    private String deliveryAndAmount;
}
