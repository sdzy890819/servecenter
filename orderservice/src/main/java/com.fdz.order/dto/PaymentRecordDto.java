package com.fdz.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRecordDto {

    private String sn;

    private String orderSn;

    private Byte paymentType;

    private BigDecimal amount;

    private Long partnerId;

}
