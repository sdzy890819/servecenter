package com.fdz.order.manager;

import com.fdz.order.domain.Orders;
import com.fdz.order.domain.OrdersLogistics;
import com.fdz.order.domain.OrdersProduct;
import com.fdz.order.mapper.OrdersLogisticsMapper;
import com.fdz.order.mapper.OrdersMapper;
import com.fdz.order.mapper.OrdersProductMapper;
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
        return p;
    }

    public int insert(Orders orders, OrdersLogistics ordersLogistics) {
        int p = insertSelective(orders);
        p = p + insertSelective(ordersLogistics);
        return p;
    }

    public Orders findOrdersByPartnerSn(String partnerSn) {
        return ordersMapper.findOrdersByPartnerSn(partnerSn);
    }

    public OrdersLogistics findOrdersLogisticsByPartnerSn(String partnerSn) {
        return ordersLogisticsMapper.findOrdersLogisticsByPartnerSn(partnerSn);
    }

}
