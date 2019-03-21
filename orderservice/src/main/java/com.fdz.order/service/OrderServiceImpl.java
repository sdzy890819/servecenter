package com.fdz.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.aspect.ann.Lock;
import com.fdz.common.enums.*;
import com.fdz.common.exception.BizException;
import com.fdz.common.utils.IDGenerator;
import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.order.convert.DtoConvert;
import com.fdz.order.domain.*;
import com.fdz.order.dto.*;
import com.fdz.order.manager.OrderManager;
import com.fdz.order.service.content.ContentService;
import com.fdz.order.service.content.dto.PartnerRestResult;
import com.fdz.order.service.content.dto.RecordDto;
import com.fdz.order.service.content.dto.ThirdpartyProductDto;
import com.fdz.order.vo.OrderProductPushVo;
import com.fdz.order.vo.OrderPushVo;
import com.fdz.order.vo.OrderStatusPushVo;
import com.fdz.order.vo.OrdersLogisticsPushVo;
import com.google.common.collect.Lists;
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

    @Resource
    private DtoConvert dtoConvert;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private IDGenerator paymentIDGenerator;

    @Override
    public CashierResult shopping(CashierDto cashierDto, Long partnerId) {
        Orders oldOrders = orderManager.findOrdersByPartnerSn(cashierDto.getSn());
        if (oldOrders != null) {
            CashierResult cashierResult = new CashierResult();
            cashierResult.setOrderSn(oldOrders.getOrderSn());
            cashierResult.setSn(cashierDto.getSn());
            cashierResult.setGoodsDtos(cashierDto.getGoodsList());
            return cashierResult;
        } else {
            List<GoodsDto> goodsDtoList = cashierDto.getGoodsList();
            if (StringUtils.isEmpty(goodsDtoList)) {
                throw new BizException("产品不可以为空!");
            }
            String orderSn = String.valueOf(orderIDGenerator.getId());
            List<Long> ppIds = new ArrayList<>();
            goodsDtoList.forEach(a -> ppIds.add(a.getProductId()));
            Map<Long, ThirdpartyProductDto> tpResultMap = findTpResultMap(ppIds);
            BigDecimal amount = BigDecimal.ZERO;
            BigDecimal platformAmount = BigDecimal.ZERO;
            BigDecimal costAmount = BigDecimal.ZERO;
            BigDecimal infoAmount = BigDecimal.ZERO;
            List<OrdersProduct> ordersProducts = new ArrayList<>();
            for (GoodsDto a : goodsDtoList) {
                ThirdpartyProductDto dto = tpResultMap.get(a.getProductId());
                if (!a.getPlatformPrice().equals(dto.getPlatformPrice())) {
                    log.error("商家传递的平台售价跟自有平台售价不一致.");
                    a.setPlatformPrice(dto.getPlatformPrice());
                }
                amount = amount.add(dto.getSalePrice().multiply(new BigDecimal(a.getNum())));
                platformAmount = platformAmount.add(dto.getPlatformPrice().multiply(new BigDecimal(a.getNum())));
                costAmount = costAmount.add(dto.getPrimeCosts().multiply(new BigDecimal(a.getNum())));
                infoAmount = infoAmount.add(dto.getServiceFee().multiply(new BigDecimal(a.getNum())));
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
                ordersProduct.setProductSalePrice(dto.getProductSalePrice());
                ordersProducts.add(ordersProduct);
            }
            ReceivingAddressDto receivingAddressDto = cashierDto.getReceivingAddress();
            //--物流信息
            OrdersLogistics ordersLogistics = new OrdersLogistics();
            ordersLogistics.setOrderSn(orderSn);
            ordersLogistics.setPartnerSn(cashierDto.getSn());
            ordersLogistics.setPartnerId(partnerId);
            ordersLogistics.setReceiver(receivingAddressDto.getName());
            ordersLogistics.setReceiverAddress(receivingAddressDto.getAddress());
            ordersLogistics.setReceiverProvince(receivingAddressDto.getProvince());
            ordersLogistics.setReceiverCity(receivingAddressDto.getCity());
            ordersLogistics.setReceiverArea(receivingAddressDto.getArea());
            ordersLogistics.setReceiverMobile(receivingAddressDto.getMobile());
            ordersLogistics.setDeliveryStatus(DeliveryStatusEnums.DONT_DELIVERY.getStatus());
            ordersLogistics.setBusinessDeliveryStatus(DeliveryStatusEnums.DONT_DELIVERY.getStatus());
            PartnerRestResult partnerRestResult = contentService.findPartnerByIdResultMap(Lists.newArrayList(partnerId)).get(partnerId);
            //--拼接order
            Orders orders = new Orders();
            orders.setOrderSn(orderSn);
            orders.setPlatformAmount(platformAmount);
            orders.setCostAmount(costAmount);
            orders.setPartnerSn(cashierDto.getSn());
            orders.setPartnerId(partnerId);
            orders.setAmount(amount);
            orders.setInfoAmount(infoAmount);
            orders.setBuyTime(new Date());
            orders.setStatus(OrdersStatus.WAIT_PAY.getStatus());
            orders.setOrderStatus(OrdersFinishStatus.PROCESSING.getStatus());
            List<PaymentRecord> paymentRecords = pay(partnerRestResult, orders);
            orderManager.insert(orders, ordersProducts, ordersLogistics, paymentRecords);
            CashierResult cashierResult = new CashierResult();
            cashierResult.setOrderSn(orderSn);
            cashierResult.setSn(cashierDto.getSn());
            cashierResult.setGoodsDtos(goodsDtoList);
            sendOrdersExecRecord(orderSn, partnerId);
            return cashierResult;
        }
    }

    /**
     * 支付
     *
     * @param partner
     * @param orders
     * @return
     */
    public List<PaymentRecord> pay(PartnerRestResult partner, Orders orders) {
        List<PaymentRecord> list = new ArrayList<>();
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setSn(orders.getPartnerSn());
        paymentRecord.setOrderSn(orders.getOrderSn());
        paymentRecord.setPaymentType(PaymentTypeEnums.PAY.getType());
        paymentRecord.setAmount(orders.getAmount());
        paymentRecord.setPayRoute(PayRouteEnums.SELF.getRoute());
        paymentRecord.setFrozen(true);
        paymentRecord.setPartnerId(orders.getPartnerId());
        list.add(paymentRecord);
        PaymentRecord paymentRecord2 = new PaymentRecord();
        paymentRecord2.setSn(orders.getPartnerSn());
        paymentRecord2.setOrderSn(orders.getOrderSn());
        paymentRecord2.setPaymentType(PaymentTypeEnums.INFO.getType());
        paymentRecord2.setAmount(orders.getInfoAmount());
        paymentRecord2.setPayRoute(PayRouteEnums.SELF.getRoute());
        paymentRecord2.setFrozen(true);
        paymentRecord2.setPartnerId(orders.getPartnerId());
        list.add(paymentRecord2);
        return list;
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
        update(updateO, updateOl);
    }

    public void update(Orders update, OrdersLogistics update2) {
        orderManager.update(update, update2);
    }

    @Override
    public Orders findOrdersByPartnerSn(String partnerSn) {
        return orderManager.findOrdersByPartnerSn(partnerSn);
    }

    @Override
    public Orders findOrdersByOrderSn(String orderSn) {
        return orderManager.findOrdersByOrderSn(orderSn);
    }

    @Override
    public OrdersLogistics findOrdersLogisticsByPartnerSn(String partnerSn) {
        return orderManager.findOrdersLogisticsByPartnerSn(partnerSn);
    }

    @Override
    public OrdersLogistics findOrdersLogisticsByOrderSn(String orderSn) {
        return orderManager.findOrdersLogisticsByOrderSn(orderSn);
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

    @Override
    public List<OrdersResult> searchOrders(SearchOrdersDto dto, Page page) {
        Integer count = orderManager.searchOrdersCount(dto);
        page.setCount(count);
        if (page.isQuery()) {
            List<OrdersResult> results = new ArrayList<>();
            List<OrdersAndLogistics> list = orderManager.searchOrders(dto, page);
            if (StringUtils.isNotEmpty(list)) {
                for (int i = 0; i < list.size(); i++) {
                    OrdersAndLogistics ordersAndLogistics = list.get(i);
                    OrdersResult ordersResult = dtoConvert.convertOrdersResult(ordersAndLogistics);
                    OrdersLogisticsResult ordersLogisticsResult = dtoConvert.convertOrdersLogisticsResult(ordersAndLogistics);
                    ordersResult.setOrdersLogisticsResult(ordersLogisticsResult);
                    results.add(ordersResult);
                }
            }
            return results;
        }
        return null;
    }

    @Override
    public OrdersResult findOrdersResult(String orderSn) {
        Orders orders = orderManager.findOrdersByOrderSn(orderSn);
        List<OrdersProduct> ordersProducts = orderManager.findOrdersProductsByOrderSn(orderSn);
        OrdersLogistics ordersLogistics = orderManager.findOrdersLogisticsByOrderSn(orderSn);
        OrdersResult ordersResult = dtoConvert.convertOrdersResult(orders);
        List<OrdersProductResult> ordersProductResults = dtoConvert.convertOrdersProductResult(ordersProducts);
        ordersResult.setOrdersProductResults(ordersProductResults);
        OrdersLogisticsResult ordersLogisticsResult = dtoConvert.convertOrdersLogisticsResult(ordersLogistics);
        ordersResult.setOrdersLogisticsResult(ordersLogisticsResult);
        return ordersResult;
    }

    @Override
    public void businessDelivery(LogisticsDto dto) {
        Orders orders = orderManager.findOrdersByOrderSn(dto.getOrderSn());
        if (orders.getOrderStatus() == OrdersStatus.RECEIVED.getStatus()) {
            throw new BizException("订单已经签收，不可更改物流信息");
        }
        OrdersLogistics ordersLogistics = orderManager.findOrdersLogisticsByOrderSn(dto.getOrderSn());
        Orders updateOrder = new Orders(orders.getId());
        updateOrder.setStatus(OrdersStatus.DELIVERED.getStatus());
        updateOrder.setBusinessDeliveryStatus(DeliveryStatusEnums.DELIVERY.getStatus());
        updateOrder.setBusinessDeliveryTime(new Date());
        OrdersLogistics updateLogistics = new OrdersLogistics(ordersLogistics.getId());
        updateLogistics.setBusinessDeliveryStatus(DeliveryStatusEnums.DELIVERY.getStatus());
        updateLogistics.setLogistics(dto.getLogistics());
        updateLogistics.setLogisticsSn(dto.getLogisticsSn());
        updateLogistics.setLogisticsStatus(dto.getLogisticsStatus());
        ordersLogistics.setLogisticsAmount(dto.getLogisticsAmount());
        update(updateOrder, updateLogistics);
        sendOrdersExecRecord(orders.getOrderSn(), orders.getPartnerId());
        sendStatusExecRecord(orders.getOrderSn());
    }

    @Override
    public void businessDelivery(String orderSn) {
        Orders orders = orderManager.findOrdersByOrderSn(orderSn);
        if (orders == null) {
            throw new BizException("订单不存在.");
        }
        if (orders.getOrderStatus() == OrdersStatus.RECEIVED.getStatus()) {
            throw new BizException("订单已经签收，不可更改物流信息");
        }
        Orders updateOrder = new Orders(orders.getId());
        updateOrder.setBusinessDeliveryTime(new Date());
        updateOrder.setBusinessDeliveryStatus(DeliveryStatusEnums.DELIVERY.getStatus());
        updateOrder.setStatus(OrdersStatus.DELIVERED.getStatus());
        OrdersLogistics ordersLogistics = orderManager.findOrdersLogisticsByOrderSn(orderSn);
        OrdersLogistics updateOrdersLogistics = new OrdersLogistics(ordersLogistics.getId());
        updateOrdersLogistics.setBusinessDeliveryStatus(DeliveryStatusEnums.DELIVERY.getStatus());
        update(updateOrder, updateOrdersLogistics);
        sendStatusExecRecord(orderSn);
    }

    @Override
    public void receive(String orderSn) {
        Orders orders = orderManager.findOrdersByOrderSn(orderSn);
        Orders updateOrder = new Orders(orders.getId());
        updateOrder.setEndTime(new Date());
        updateOrder.setStatus(OrdersStatus.RECEIVED.getStatus());
        updateOrder.setOrderStatus(OrdersFinishStatus.FINISHED.getStatus());
        OrdersLogistics ordersLogistics = orderManager.findOrdersLogisticsByOrderSn(orderSn);
        OrdersLogistics updateOrdersLogistics = new OrdersLogistics(ordersLogistics.getId());
        updateOrdersLogistics.setLogisticsStatus(OrdersStatus.RECEIVED.getStatusText());
        update(updateOrder, updateOrdersLogistics);
        sendStatusExecRecord(orderSn);
    }

    /**
     * 保存订单状态推送
     *
     * @param orderSn
     */
    public void sendStatusExecRecord(String orderSn) {
        Orders orders = orderManager.findOrdersByOrderSn(orderSn);
        RecordDto recordDto = new RecordDto();
        OrderStatusPushVo vo = new OrderStatusPushVo();
        vo.setOrderSn(orderSn);
        vo.setSn(orders.getPartnerSn());
        vo.setStatus(orders.getStatus());
        try {
            recordDto.setData(objectMapper.writeValueAsString(vo));
        } catch (JsonProcessingException e) {
            throw new BizException("Json转字符串异常", e);
        }
        recordDto.setInterfaceType(InterfaceTypeEnums.SYNC_ORDER_STATUS.getType());
        recordDto.setPartnerId(orders.getPartnerId());
        contentService.create(recordDto);
    }

    public OrderStatusPushVo findOrderStatusPushVo(String partnerSn) {
        Orders orders = orderManager.findOrdersByPartnerSn(partnerSn);
        OrderStatusPushVo vo = new OrderStatusPushVo();
        vo.setOrderSn(orders.getOrderSn());
        vo.setSn(orders.getPartnerSn());
        vo.setStatus(orders.getStatus());
        return vo;
    }

    /**
     * 发送订单推送
     *
     * @param orderSn
     * @param partnerId
     */
    public void sendOrdersExecRecord(String orderSn, Long partnerId) {
        OrderPushVo orderPushVo = findOrderPushVo(orderSn);
        RecordDto recordDto = new RecordDto();
        try {
            recordDto.setData(objectMapper.writeValueAsString(orderPushVo));
        } catch (JsonProcessingException e) {
            throw new BizException("Json转字符串异常", e);
        }
        recordDto.setInterfaceType(InterfaceTypeEnums.SYNC_ORDER_INFO.getType());
        recordDto.setPartnerId(partnerId);
        contentService.create(recordDto);
    }

    /**
     * 拉取订单信息
     *
     * @param orderSn
     * @return
     */
    public OrderPushVo findOrderPushVo(String orderSn) {
        Orders orders = orderManager.findOrdersByOrderSn(orderSn);
        OrdersLogistics ordersLogistics = orderManager.findOrdersLogisticsByOrderSn(orderSn);
        List<OrdersProduct> ordersProducts = orderManager.findOrdersProductsByOrderSn(orderSn);

        OrderPushVo orderPushVo = dtoConvert.convert(orders);
        orderPushVo.setSn(orders.getPartnerSn());
        List<OrderProductPushVo> orderProductPushVos = dtoConvert.convert(ordersProducts);
        OrdersLogisticsPushVo ordersLogisticsPushVo = dtoConvert.convert(ordersLogistics);
        orderPushVo.setProducts(orderProductPushVos);
        orderPushVo.setLogistics(ordersLogisticsPushVo);
        return orderPushVo;
    }

    @Override
    public List<DeliveryInfo> statistics(Integer days) {
        Calendar calendar = Calendar.getInstance();
        Date end = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date start = calendar.getTime();
        List<OrderStatistics> deliveryList = orderManager.findOrderStatistics(start, end);
        if (deliveryList != null) {
            List<Long> partnerIds = new ArrayList<>();
            deliveryList.forEach(a -> {
                if (!partnerIds.contains(a.getPartnerId())) {
                    partnerIds.add(a.getPartnerId());
                }
            });
            Map<Long, PartnerRestResult> partnerMap = StringUtils.isNotEmpty(partnerIds) ? contentService.findPartnerByIdResultMap(partnerIds) : new HashMap<>(0);
            Map<String, DeliveryInfo> resultMap = new HashMap<>();
            deliveryList.forEach(a -> {
                String tmp = a.getOrderDate() + a.getPartnerId();
                DeliveryInfo deliveryInfo = resultMap.get(tmp);
                if (resultMap.get(tmp) == null) {
                    deliveryInfo = new DeliveryInfo();
                    resultMap.put(tmp, deliveryInfo);

                }
                deliveryInfo.setDate(a.getOrderDate());
                deliveryInfo.setPartnerId(a.getPartnerId());
                PartnerRestResult partnerRestResult = partnerMap.get(a.getPartnerId());
                deliveryInfo.setPartnerName(partnerRestResult != null ? partnerRestResult.getName() : "");
                DeliveryStatusEnums deliveryStatusEnums = DeliveryStatusEnums.get(a.getDeliveryStatus());
                switch (deliveryStatusEnums) {
                    case DELIVERY: {
                        deliveryInfo.setDeliveryAndAmount(a.getNum() + "/" + a.getAmount());
                        break;
                    }
                    case DONT_DELIVERY: {
                        deliveryInfo.setWaitDeliveryAndAmount(a.getNum() + "/" + a.getAmount());
                        break;
                    }
                    default:
                        break;
                }
            });
            List<DeliveryInfo> result = new ArrayList<>(resultMap.values());
            return result;
        }
        return null;
    }

    @Override
    public List<PaymentRecord> searchPaymentRecord(PaymentRecordSearchDto dto, Page page) {
        Integer count = orderManager.searchPaymentRecordCount(dto);
        page.setCount(count);
        if (page.isQuery()) {
            return orderManager.searchPaymentRecord(dto, page);
        }
        return null;
    }

    @Override
    public int insertSelective(PaymentRecord record) {
        return orderManager.insertSelective(record);
    }

    @Override
    public PaymentRecord selectPaymentRecordByPrimaryKey(Long id) {
        return orderManager.selectPaymentRecordByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(PaymentRecord record) {
        return orderManager.updateByPrimaryKeySelective(record);
    }

    public int insertSelective(Account record) {
        return orderManager.insertSelective(record);
    }

    public Account selectAccountByPrimaryKey(Long id) {
        return orderManager.selectAccountByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(Account record) {
        return orderManager.updateByPrimaryKeySelective(record);
    }

    public Account findAccountByPartnerId(Long partnerId) {
        return orderManager.findAccountByPartnerId(partnerId);
    }


    @Override
    @Lock(key = "PAYMENT_RECORD_#{paymentRecord.partnerId}")
    public void addRecord(PaymentRecord paymentRecord) {
        paymentRecord.setPayRoute(PayRouteEnums.SELF.getRoute());
        paymentRecord.setFrozen(false);
        orderManager.calc(paymentRecord);
    }

    @Override
    public PaymentRecord findRecordByPartnerIdAndTypeAndOrderSnAndFrozen(Long partnerId, Byte paymentType, String orderSn, Boolean frozen) {
        return orderManager.findRecordByPartnerIdAndTypeAndOrderSnAndFrozen(partnerId, paymentType, orderSn, frozen);
    }

}
