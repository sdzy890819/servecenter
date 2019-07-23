package com.fdz.order.manager;

import com.fdz.common.aspect.ann.Lock;
import com.fdz.common.enums.OrdersStatus;
import com.fdz.common.enums.PayStatusEnums;
import com.fdz.common.enums.PaymentTypeEnums;
import com.fdz.common.exception.BizException;
import com.fdz.common.utils.IDGenerator;
import com.fdz.common.utils.Page;
import com.fdz.order.domain.*;
import com.fdz.order.dto.PaymentRecordSearchDto;
import com.fdz.order.dto.SearchOrdersDto;
import com.fdz.order.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
@Transactional
public class OrderManager {

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private OrdersLogisticsMapper ordersLogisticsMapper;

    @Resource
    private OrdersProductMapper ordersProductMapper;

    @Resource
    private OrdersTrackMapper ordersTrackMapper;

    @Resource
    private PaymentRecordMapper paymentRecordMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private IDGenerator paySnIDGenerator;


    public int insert(Orders record) {
        return ordersMapper.insert(record);
    }

    public int insertSelective(Orders record) {
        return ordersMapper.insertSelective(record);
    }

    public Orders selectOrdersByPrimaryKey(Long id) {
        return ordersMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(Orders record) {
        return ordersMapper.updateByPrimaryKeySelective(record);
    }

    public int insert(OrdersLogistics record) {
        return ordersLogisticsMapper.insert(record);
    }

    public int insertSelective(OrdersLogistics record) {
        return ordersLogisticsMapper.insertSelective(record);
    }

    public OrdersLogistics selectOrdersLogisticsByPrimaryKey(Long id) {
        return ordersLogisticsMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(OrdersLogistics record) {
        return ordersLogisticsMapper.updateByPrimaryKeySelective(record);
    }

    public int insert(OrdersProduct record) {
        return ordersProductMapper.insert(record);
    }

    public int insertSelective(OrdersProduct record) {
        return ordersProductMapper.insertSelective(record);
    }

    public OrdersProduct selectOrdersProductByPrimaryKey(Long id) {
        return ordersProductMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(OrdersProduct record) {
        return ordersProductMapper.updateByPrimaryKeySelective(record);
    }

    public int insertOrdersProducts(List<OrdersProduct> list) {
        return ordersProductMapper.insertOrdersProducts(list);
    }

    @Lock(key = "PAYMENT_RECORD_#{orders.orderSn}")
    public int insert(Orders orders, List<OrdersProduct> list, OrdersLogistics ordersLogistics, List<PaymentRecord> paymentRecords) {
        int p = insertSelective(orders);
        p = p + insertSelective(ordersLogistics);
        p = p + insertOrdersProducts(list);
        paymentRecords.forEach(a -> calc(a));
        firstInsertStatus(orders.getOrderSn(), orders.getPartnerSn(), orders.getStatus(), (byte) 0);
        return p;
    }

    @Lock(key = "ORDER_UPDATE_#{orders.id}")
    public int update(Orders orders, OrdersLogistics ordersLogistics) {
        Orders ordersInfo = selectOrdersByPrimaryKey(orders.getId());
        if (orders.getStatus() != null) {
            insertStatus(orders.getId(), orders.getStatus());
            switch (OrdersStatus.get(orders.getStatus())) {
                case CONFIRM_SEND: {
                    PaymentRecord paymentRecord = findRecordByPartnerIdAndTypeAndOrderSnAndFrozen(ordersInfo.getPartnerId(), PaymentTypeEnums.PAY.getType(), ordersInfo.getOrderSn(), true);
                    PaymentRecord infoPaymentRecord = findRecordByPartnerIdAndTypeAndOrderSnAndFrozen(ordersInfo.getPartnerId(), PaymentTypeEnums.INFO.getType(), ordersInfo.getOrderSn(), true);
                    if (paymentRecord != null) {
                        updateRecord(paymentRecord);
                    }
                    if (infoPaymentRecord != null) {
                        updateRecord(infoPaymentRecord);
                    }
                    break;
                }
                default:
                    break;
            }
        }
        int p = updateByPrimaryKeySelective(orders);
        p = p + updateByPrimaryKeySelective(ordersLogistics);
        return p;
    }

    private void updateRecord(PaymentRecord info) {
        if (info.getFrozen()) {
            info.setPayStatus(PayStatusEnums.SUCCESS.getStatus());
            info.setPaySn(String.valueOf(paySnIDGenerator.getId()));
            info.setPayTime(new Date());
            info.setFrozen(false);
            Account account = accountMapper.findAccountByPartnerId(info.getPartnerId());
            BigDecimal freezing = account.getFreezingAmount().subtract(info.getAmount().abs());
            if (freezing.compareTo(info.getAmount().abs()) < 0) {
                freezing = BigDecimal.ZERO;
                log.error("账务异常, 出现冻结资金小于0的情况, 金额:{}, 冻结资金:{}, 订单号:{}", freezing, info.getAmount(), info.getOrderSn());
            }
            Account updateAccount = new Account(account.getId());
            updateAccount.setFreezingAmount(freezing);
            updateByPrimaryKeySelective(info);
            updateByPrimaryKeySelective(updateAccount);
        }
    }


    public int firstInsertStatus(String orderSn, String partnerSn, byte status, byte lastStatus) {
        OrdersTrack ordersTrack = new OrdersTrack();
        ordersTrack.setLastStatus(lastStatus);
        ordersTrack.setOrderSn(orderSn);
        ordersTrack.setPartnerSn(partnerSn);
        ordersTrack.setStatus(status);
        return insertSelective(ordersTrack);
    }

    /**
     * 更新后的状态更改
     *
     * @param id
     * @param status
     * @return
     */
    public int insertStatus(Long id, byte status) {
        Orders oldOrders = selectOrdersByPrimaryKey(id);
        OrdersTrack ordersTrack = new OrdersTrack();
        ordersTrack.setLastStatus(oldOrders.getStatus());
        ordersTrack.setOrderSn(oldOrders.getOrderSn());
        ordersTrack.setPartnerSn(oldOrders.getPartnerSn());
        ordersTrack.setStatus(status);
        return insertSelective(ordersTrack);
    }

    public Orders findOrdersByPartnerSn(String partnerSn) {
        return ordersMapper.findOrdersByPartnerSn(partnerSn);
    }

    public Orders findOrdersByOrderSn(String orderSn) {
        return ordersMapper.findOrdersByOrderSn(orderSn);
    }

    public OrdersLogistics findOrdersLogisticsByPartnerSn(String partnerSn) {
        return ordersLogisticsMapper.findOrdersLogisticsByPartnerSn(partnerSn);
    }

    public OrdersLogistics findOrdersLogisticsByOrderSn(String orderSn) {
        return ordersLogisticsMapper.findOrdersLogisticsByOrderSn(orderSn);
    }

    public int insertSelective(OrdersTrack record) {
        return ordersTrackMapper.insertSelective(record);
    }

    public OrdersTrack selectOrdersTrackByPrimaryKey(Long id) {
        return ordersTrackMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(OrdersTrack record) {
        return ordersTrackMapper.updateByPrimaryKeySelective(record);
    }

    public List<OrdersAndLogistics> searchOrders(SearchOrdersDto dto, Page page) {
        return ordersMapper.searchOrders(dto, page);
    }

    public Integer searchOrdersCount(SearchOrdersDto dto) {
        return ordersMapper.searchOrdersCount(dto);
    }

    public List<OrdersProduct> findOrdersProductsByOrderSn(String orderSn) {
        return ordersProductMapper.findOrdersProductsByOrderSn(orderSn);
    }

    public List<OrdersProduct> findOrdersProductByOrderSns(List<String> orderSns) {
        return ordersProductMapper.findOrdersProductByOrderSns(orderSns);
    }

    public List<OrderStatistics> findOrderStatistics(Date start, Date end) {
        return ordersMapper.findOrderStatistics(start, end);
    }

    public List<OrderStatistics> findOrderStatisticsByBusiness(Date start, Date end) {
        return ordersMapper.findOrderStatisticsByBusiness(start, end);
    }

    public Integer searchPaymentRecordCount(PaymentRecordSearchDto dto) {
        return paymentRecordMapper.searchPaymentRecordCount(dto);
    }

    public List<PaymentRecord> searchPaymentRecord(PaymentRecordSearchDto dto, Page page) {
        return paymentRecordMapper.searchPaymentRecord(dto, page);
    }

    public int insertSelective(PaymentRecord record) {
        return paymentRecordMapper.insertSelective(record);
    }

    public PaymentRecord selectPaymentRecordByPrimaryKey(Long id) {
        return paymentRecordMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(PaymentRecord record) {
        return paymentRecordMapper.updateByPrimaryKeySelective(record);
    }

    public int insertSelective(Account record) {
        return accountMapper.insertSelective(record);
    }

    public Account selectAccountByPrimaryKey(Long id) {
        return accountMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(Account record) {
        return accountMapper.updateByPrimaryKeySelective(record);
    }

    public Account findAccountByPartnerId(Long partnerId) {
        return accountMapper.findAccountByPartnerId(partnerId);
    }

    /**
     * 支付、计算、记录数据
     *
     * @param paymentRecord
     */
    public void calc(PaymentRecord paymentRecord) {
        Account account = findAccountByPartnerId(paymentRecord.getPartnerId());
        PaymentTypeEnums paymentTypeEnums = PaymentTypeEnums.get(paymentRecord.getPaymentType());
        BigDecimal amount = account.getAmount();
        BigDecimal freezingAmount = account.getFreezingAmount();
        switch (paymentTypeEnums) {
            case RECHARGE: {
                BigDecimal tmpAmount = paymentRecord.getAmount().abs();
                amount = amount.add(tmpAmount);
                paymentRecord.setSurplusAmount(amount);
                paymentRecord.setAmount(tmpAmount);
                break;
            }
            default: {
                BigDecimal tmpAmount = paymentRecord.getAmount().abs().multiply(new BigDecimal(-1));
                if (amount.compareTo(paymentRecord.getAmount()) < 0) {
                    throw new BizException("没有足够的钱" + paymentTypeEnums.getText());
                }
                amount = amount.add(tmpAmount);
                freezingAmount = freezingAmount.add(tmpAmount.abs());
                paymentRecord.setSurplusAmount(amount);
                paymentRecord.setAmount(tmpAmount);
                break;
            }
        }
        Account a = new Account(account.getId());
        a.setAmount(amount);
        a.setFreezingAmount(freezingAmount);
        updateByPrimaryKeySelective(a);
        insertSelective(paymentRecord);
    }

    public PaymentRecord findRecordByPartnerIdAndTypeAndOrderSnAndFrozen(Long partnerId, Byte paymentType, String orderSn, Boolean frozen) {
        return paymentRecordMapper.findRecordByPartnerIdAndTypeAndOrderSnAndFrozen(partnerId, paymentType, orderSn, frozen);
    }

    public List<OrdersAndLogistics> searchAllOrders(SearchOrdersDto dto) {
        return ordersMapper.searchAllOrders(dto);
    }

    public void updateLogisticsInfo(List<OrdersLogisticsInfo> logisticsList) {
        ordersLogisticsMapper.updateLogisticsInfo(logisticsList);
    }

//    public void updateLogisticInfo(OrdersLogisticsInfo ordersLogisticsInfo) {
//        OrdersLogistics oldLog = ordersLogisticsMapper.findOrdersLogisticsByOrderSn(ordersLogisticsInfo.getOrderSn());
//        OrdersLogistics ordersLogistics = new OrdersLogistics(oldLog.getId());
//        ordersLogistics.setLogistics(ordersLogisticsInfo.getLogistics());
//        ordersLogistics.setLogisticsStatus(ordersLogisticsInfo.getLogisticsStatus());
//        ordersLogistics.setLogisticsSn(ordersLogisticsInfo.getLogisticsSn());
//        ordersLogisticsMapper.updateByPrimaryKeySelective(ordersLogistics);
//        Orders oldOrders = findOrdersByOrderSn(ordersLogisticsInfo.getOrderSn());
//        Orders orders = new Orders(oldOrders.getId());
//        orders.setBusinessDeliveryStatus();
//        update();
//    }

    public List<String> findOrdersByTimeAndDelivered(Date time) {
        return ordersMapper.findOrdersByTimeAndDelivered(time);
    }

    public List<String> findOrdersByTimeAndWaitPay(Date time) {
        return ordersMapper.findOrdersByTimeAndWaitPay(time);
    }

    public void update(Orders orders) {
        updateByPrimaryKeySelective(orders);
        insertStatus(orders.getId(), orders.getStatus());
    }

}
