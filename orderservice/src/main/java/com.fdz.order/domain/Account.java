package com.fdz.order.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account extends BaseEntity {

    private BigDecimal amount;

    private BigDecimal freezingAmount;

    private Long partnerId;

    public Account() {
        super();
    }

    public Account(Long id) {
        super(id);
    }
}