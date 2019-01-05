package com.fdz.order.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

@Data
public class OrdersLogistics extends BaseEntity {

    private String orderSn;

    private String partnerSn;

    private Long partnerId;

    private String receiver;

    private String receiverAddress;

    private String receiverMobile;

    private String logistics;

    private String logisticsSn;

    private String logisticsStatus;

    private Byte deliveryStatus;


    public OrdersLogistics() {
        super();
    }

    public OrdersLogistics(Long id) {
        super(id);
    }
}