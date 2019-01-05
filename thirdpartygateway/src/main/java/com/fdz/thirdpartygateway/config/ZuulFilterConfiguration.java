package com.fdz.thirdpartygateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.config.CustomZuulFallback;
import com.fdz.common.filter.RequestIdentifyingFilter;
import com.fdz.thirdpartygateway.filter.XLRsaDecryptionFilter;
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
    public XLRsaDecryptionFilter xlRsaDecryptionFilter(ApplicationProperties applicationProperties, ObjectMapper objectMapper) {
        return new XLRsaDecryptionFilter(applicationProperties, objectMapper);
    }

    @Bean
    public CustomZuulFallback customZuulFallback() {
        return new CustomZuulFallback();
    }


}
