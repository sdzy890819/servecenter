package com.fdz.order.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderStatistics {

    private String orderDate;

    private Long partnerId;

    private byte deliveryStatus;

    private Long num;

    private BigDecimal amount;
}
