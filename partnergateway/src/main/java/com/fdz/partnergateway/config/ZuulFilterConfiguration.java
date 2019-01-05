package com.fdz.partnergateway.config;

import com.fdz.common.config.CustomZuulFallback;
import com.fdz.common.filter.RequestIdentifyingFilter;
import com.fdz.partnergateway.filter.PassportAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * zuul配置
 */
@Configuration
public class ZuulFilterConfiguration {

    @Bean
    public RequestIdentifyingFilter requestIdentifyingFilter() {
        return new RequestIdentifyingFilter();
    }

    @Bean
    public PassportAuthenticationFilter passportAuthenticationFilter() {
        return new PassportAuthenticationFilter();
    }

    @Bean
    public CustomZuulFallback customZuulFallback() {
        return new CustomZuulFallback();
    }

}
