package com.fdz.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountResult {

    private String partnerName;

    private BigDecimal amount;

    private BigDecimal freezingAmount;

    private String contacts;

    private String contactMobile;
}
