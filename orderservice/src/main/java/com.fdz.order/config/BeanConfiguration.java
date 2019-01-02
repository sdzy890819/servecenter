package com.fdz.order.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class BeanConfiguration {

    @Resource
    private ApplicationProperties applicationProperties;

}
