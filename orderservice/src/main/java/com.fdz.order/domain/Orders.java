package com.fdz.order.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Orders extends BaseEntity {

    private String orderSn;

    private String partnerSn;

    private Long partnerId;

    private BigDecimal amount;

    private Date buyTime;

    private Date confirmTime;

    private Date businessDeliveryTime;

    private Date endTime;

    private Byte deliveryStatus;

    private Byte status;

    private Byte orderStatus;

    private Byte businessDeliveryStatus;

    public Orders() {
        super();
    }

    public Orders(Long id) {
        super(id);
    }

}