package com.fdz.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LogisticsDto {

    private String logistics;

    private String logisticsSn;

    private String logisticsStatus;

    private String orderSn;

    private BigDecimal logisticsAmount;

}
