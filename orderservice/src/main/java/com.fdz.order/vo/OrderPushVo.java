package com.fdz.order.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OrderPushVo {

    private String sn;

    private String orderSn;

    private BigDecimal amount;

    private Date buyTime;

    private Date confirmTime;

    private Date businessDeliveryTime;

    private Date endTime;

    private Byte status;

    private List<OrderProductPushVo> products;

    private OrdersLogisticsPushVo logistics;

}
