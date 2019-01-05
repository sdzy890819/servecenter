package com.fdz.order.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentRecord extends BaseEntity {

    private String sn;

    private String orderSn;

    private Byte paymentType;

    private BigDecimal amount;

    private BigDecimal surplusAmount;

    private Date payTime;

    private Byte payStatus;

    private String paySn;

    private Byte payRoute;

    private Long partnerId;

    public PaymentRecord() {
        super();
    }

    public PaymentRecord(Long id) {
        super(id);
    }

}