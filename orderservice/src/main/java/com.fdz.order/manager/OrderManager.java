package com.fdz.order.manager;

import com.fdz.common.utils.Page;
import com.fdz.order.domain.*;
import com.fdz.order.dto.SearchOrdersDto;
import com.fdz.order.mapper.OrdersLogisticsMapper;
import com.fdz.order.mapper.OrdersMapper;
import com.fdz.order.mapper.OrdersProductMapper;
import com.fdz.order.mapper.OrdersTrackMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    public int insert(Orders orders, List<OrdersProduct> list, OrdersLogistics ordersLogistics) {
        int p = insertSelective(orders);
        p = p + insertSelective(ordersLogistics);
        p = p + insertOrdersProducts(list);
        firstInsertStatus(orders.getOrderSn(), orders.getPartnerSn(), orders.getStatus(), (byte) 0);
        return p;
    }

    public int update(Orders orders, OrdersLogistics ordersLogistics) {
        if (orders.getOrderStatus() != null) {
            insertStatus(orders.getId(), orders.getStatus());
        }
        int p = updateByPrimaryKeySelective(orders);
        p = p + updateByPrimaryKeySelective(ordersLogistics);
        return p;
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

}
