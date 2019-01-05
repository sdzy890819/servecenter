package com.fdz.order.manager;

import com.fdz.order.mapper.OrdersLogisticsMapper;
import com.fdz.order.mapper.OrdersMapper;
import com.fdz.order.mapper.OrdersProductMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Repository
@Transactional
public class OrderManager {

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private OrdersLogisticsMapper ordersLogisticsMapper;

    @Resource
    private OrdersProductMapper ordersProductMapper;


}
