package com.fdz.order.service;

import com.fdz.common.utils.Page;
import com.fdz.order.domain.Account;
import com.fdz.order.domain.Orders;
import com.fdz.order.domain.OrdersLogistics;
import com.fdz.order.domain.PaymentRecord;
import com.fdz.order.dto.*;
import com.fdz.order.vo.OrderPushVo;
import com.fdz.order.vo.OrderStatusPushVo;

import java.util.List;

public interface OrderService {

    CashierResult shopping(CashierDto cashierDto, Long partnerId);

    void delivery(String sn);

    Orders findOrdersByPartnerSn(String partnerSn);

    Orders findOrdersByOrderSn(String orderSn);

    OrdersLogistics findOrdersLogisticsByPartnerSn(String partnerSn);

    OrdersLogistics findOrdersLogisticsByOrderSn(String orderSn);

    List<OrdersResult> searchOrders(SearchOrdersDto dto, Page page);

    OrdersResult findOrdersResult(String orderSn);

    void businessDelivery(LogisticsDto dto);

    void businessDelivery(String orderSn);

    void receive(String orderSn);

    OrderPushVo findOrderPushVo(String orderSn);

    OrderStatusPushVo findOrderStatusPushVo(String partnerSn);

    List<DeliveryInfo> statistics(Integer days);

    List<PaymentRecord> searchPaymentRecord(PaymentRecordSearchDto dto, Page page);

    int insertSelective(PaymentRecord record);

    PaymentRecord selectPaymentRecordByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PaymentRecord record);

    void addRecord(PaymentRecord paymentRecord);

    int insertSelective(Account record);

    Account selectAccountByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Account record);
}
