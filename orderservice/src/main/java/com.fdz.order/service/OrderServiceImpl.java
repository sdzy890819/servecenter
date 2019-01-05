package com.fdz.order.service;

import com.fdz.order.manager.OrderManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderManager orderManager;
    

}
