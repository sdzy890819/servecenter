package com.fdz.order.convert;

import com.fdz.order.domain.*;
import com.fdz.order.dto.*;
import com.fdz.order.vo.OrderProductPushVo;
import com.fdz.order.vo.OrderPushVo;
import com.fdz.order.vo.OrdersLogisticsPushVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DtoConvert {

    OrdersResult convertOrdersResult(OrdersAndLogistics ordersAndLogistics);

    OrdersLogisticsResult convertOrdersLogisticsResult(OrdersAndLogistics ordersAndLogistics);

    OrdersResult convertOrdersResult(Orders orders);

    OrdersLogisticsResult convertOrdersLogisticsResult(OrdersLogistics ordersLogistics);

    OrdersProductResult convertOrdersProductResult(OrdersProduct ordersProduct);

    List<OrdersProductResult> convertOrdersProductResult(List<OrdersProduct> ordersProducts);

    //--
    OrderProductPushVo convert(OrdersProduct ordersProduct);

    List<OrderProductPushVo> convert(List<OrdersProduct> ordersProducts);

    OrdersLogisticsPushVo convert(OrdersLogistics ordersLogistics);

    OrderPushVo convert(Orders orders);

    PaymentRecord convert(PaymentRecordDto dto);

    RechargeRecordResult convert(PaymentRecord paymentRecord);

    List<RechargeRecordResult> convertPaymentList(List<PaymentRecord> records);
}
