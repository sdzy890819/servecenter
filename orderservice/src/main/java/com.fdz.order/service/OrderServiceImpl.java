package com.fdz.order.service;

import com.fdz.common.enums.DeliveryStatusEnums;
import com.fdz.common.enums.OrdersFinishStatus;
import com.fdz.common.enums.OrdersStatus;
import com.fdz.common.exception.BizException;
import com.fdz.common.utils.IDGenerator;
import com.fdz.common.utils.StringUtils;
import com.fdz.order.domain.Orders;
import com.fdz.order.domain.OrdersLogistics;
import com.fdz.order.domain.OrdersProduct;
import com.fdz.order.dto.CashierDto;
import com.fdz.order.dto.CashierResult;
import com.fdz.order.dto.GoodsDto;
import com.fdz.order.dto.ReceivingAddressDto;
import com.fdz.order.manager.OrderManager;
import com.fdz.order.service.content.ContentService;
import com.fdz.order.service.content.dto.ThirdpartyProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderManager orderManager;

    @Resource
    private IDGenerator orderIDGenerator;

    @Resource
    private ContentService contentService;

    @Override
    public CashierResult shopping(CashierDto cashierDto, Long partnerId) {
        List<GoodsDto> goodsDtoList = cashierDto.getGoodsList();
        if (StringUtils.isEmpty(goodsDtoList)) {
            throw new BizException("产品不可以为空!");
        }
        String orderSn = String.valueOf(orderIDGenerator.getId());
        List<Long> ppIds = new ArrayList<>();
        goodsDtoList.forEach(a -> ppIds.add(a.getProductId()));
        Map<Long, ThirdpartyProductDto> tpResultMap = findTpResultMap(ppIds);
        BigDecimal amount = BigDecimal.ZERO;
        List<OrdersProduct> ordersProducts = new ArrayList<>();
        for (GoodsDto a : goodsDtoList) {
            ThirdpartyProductDto dto = tpResultMap.get(a.getProductId());
            if (!a.getPlatformPrice().equals(dto.getPlatformPrice())) {
                log.error("商家传递的平台售价跟自有平台售价不一致.");
                a.setPlatformPrice(dto.getPlatformPrice());
            }
            amount = amount.add(dto.getSalePrice().multiply(new BigDecimal(a.getNum())));
            OrdersProduct ordersProduct = new OrdersProduct();
            ordersProduct.setOrderSn(orderSn);
            ordersProduct.setPartnerSn(cashierDto.getSn());
            ordersProduct.setPartnerId(partnerId);
            ordersProduct.setPartnerProductId(a.getProductId());
            ordersProduct.setProductName(dto.getProductName());
            ordersProduct.setProductNum(a.getNum());
            ordersProduct.setProductTypeNo(dto.getProductTypeNo());
            ordersProduct.setProductTypeName(dto.getProductTypeName());
            ordersProduct.setProductDescription(dto.getProductDescription());
            ordersProduct.setProductCoverImage(dto.getProductCoverImage());
            ordersProduct.setPrimeCosts(dto.getPrimeCosts());
            ordersProduct.setSalePrice(dto.getSalePrice());
            ordersProduct.setProductModel(dto.getProductModel());
            ordersProduct.setProductId(dto.getProductId());
            ordersProduct.setPlatformPrice(dto.getPlatformPrice());
            ordersProduct.setProductSalePrice(dto.getSalePrice());
            ordersProducts.add(ordersProduct);
        }
        ReceivingAddressDto receivingAddressDto = cashierDto.getReceivingAddress();
        OrdersLogistics ordersLogistics = new OrdersLogistics();
        ordersLogistics.setOrderSn(orderSn);
        ordersLogistics.setPartnerSn(cashierDto.getSn());
        ordersLogistics.setPartnerId(partnerId);
        ordersLogistics.setReceiver(receivingAddressDto.getName());
        ordersLogistics.setReceiverAddress(receivingAddressDto.getAddress());
        ordersLogistics.setReceiverMobile(receivingAddressDto.getMobile());
        ordersLogistics.setDeliveryStatus(DeliveryStatusEnums.DONT_DELIVERY.getStatus());
        ordersLogistics.setBusinessDeliveryStatus(DeliveryStatusEnums.DONT_DELIVERY.getStatus());

        Orders orders = new Orders();
        orders.setOrderSn(orderSn);
        orders.setPartnerSn(cashierDto.getSn());
        orders.setPartnerId(partnerId);
        orders.setAmount(amount);
        orders.setBuyTime(new Date());
        orders.setStatus(OrdersStatus.WAIT_PAY.getStatus());
        orders.setOrderStatus(OrdersFinishStatus.PROCESSING.getStatus());
        orderManager.insert(orders, ordersProducts, ordersLogistics);
        CashierResult cashierResult = new CashierResult();
        cashierResult.setOrderSn(orderSn);
        cashierResult.setSn(cashierDto.getSn());
        cashierResult.setGoodsDtos(goodsDtoList);
        return cashierResult;
    }

    @Override
    public void delivery(String sn) {
        Orders orders = findOrdersByPartnerSn(sn);
        if (orders == null) {
            throw new BizException("查找不到相关订单信息");
        }
        Orders updateO = new Orders(orders.getId());
        updateO.setStatus(OrdersStatus.CONFIRM_SEND.getStatus());
        updateO.setDeliveryStatus(DeliveryStatusEnums.DELIVERY.getStatus());
        updateO.setConfirmTime(new Date());
        OrdersLogistics ordersLogistics = findOrdersLogisticsByPartnerSn(sn);
        OrdersLogistics updateOl = new OrdersLogistics(ordersLogistics.getId());
        updateOl.setDeliveryStatus(DeliveryStatusEnums.DELIVERY.getStatus());
        orderManager.insert(updateO, updateOl);
    }

    @Override
    public Orders findOrdersByPartnerSn(String partnerSn) {
        return orderManager.findOrdersByPartnerSn(partnerSn);
    }

    @Override
    public OrdersLogistics findOrdersLogisticsByPartnerSn(String partnerSn) {
        return orderManager.findOrdersLogisticsByPartnerSn(partnerSn);
    }

    public Map<Long, ThirdpartyProductDto> findTpResultMap(List<Long> ppIds) {
        List<ThirdpartyProductDto> tpList = contentService.detailList(ppIds);
        if (tpList == null) {
            throw new BizException("产品不可以为空!");
        }
        Map<Long, ThirdpartyProductDto> result = new HashMap<>();
        tpList.forEach(a -> result.put(a.getId(), a));
        return result;
    }
}
