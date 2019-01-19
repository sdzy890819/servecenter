package com.fdz.order.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrdersAndLogistics extends BaseEntity {

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

    private String receiver;

    private String receiverAddress;

    private String receiverMobile;

    private String logistics;

    private String logisticsSn;

    private String logisticsStatus;

    private BigDecimal platformAmount;

    private BigDecimal costAmount;

    private BigDecimal infoAmount;

}
