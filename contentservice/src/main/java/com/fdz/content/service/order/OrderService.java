package com.fdz.content.service.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class OrderService {


    @Resource
    private OrderRestClient orderRestClient;

    public boolean initAccount(Long partnerId) {
        return orderRestClient.initAccount(partnerId).isSuccess();
    }
}
