package com.fdz.order.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

@Data
public class OrdersTrack extends BaseEntity {

    private String orderSn;

    private String partnerSn;

    private Byte status;

    private Byte lastStatus;

    public OrdersTrack() {
        super();
    }

    public OrdersTrack(Long id) {
        super(id);
    }
}