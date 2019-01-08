package com.fdz.order.service;

import com.fdz.order.domain.Orders;
import com.fdz.order.domain.OrdersLogistics;
import com.fdz.order.dto.CashierDto;
import com.fdz.order.dto.CashierResult;

public interface OrderService {

    CashierResult shopping(CashierDto cashierDto, Long partnerId);

    void delivery(String sn);

    Orders findOrdersByPartnerSn(String partnerSn);

    OrdersLogistics findOrdersLogisticsByPartnerSn(String partnerSn);
}
