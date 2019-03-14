package com.fdz.thirdpartygateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.config.CustomZuulFallback;
import com.fdz.common.filter.RequestIdentifyingFilter;
import com.fdz.thirdpartygateway.filter.RsaDecryptionFilter;
import com.fdz.thirdpartygateway.filter.RsaPostFilter;
import com.fdz.thirdpartygateway.filter.ZuulExceptionFilter;
import com.fdz.thirdpartygateway.service.content.ContentService;
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
    public RsaDecryptionFilter xlRsaDecryptionFilter(ApplicationProperties applicationProperties, ObjectMapper objectMapper, ContentService contentService) {
        return new RsaDecryptionFilter(applicationProperties, objectMapper, contentService);
    }

    @Bean
    public CustomZuulFallback customZuulFallback() {
        return new CustomZuulFallback();
    }

    @Bean
    public RsaPostFilter rsaPostFilter(ContentService contentService, ApplicationProperties applicationProperties, ObjectMapper objectMapper) {
        return new RsaPostFilter(contentService, applicationProperties, objectMapper);
    }

    @Bean
    public ZuulExceptionFilter zuulExceptionFilter(ObjectMapper objectMapper) {
        return new ZuulExceptionFilter(objectMapper);
    }


    /*@Bean
    public UrlPathFilter urlPathFilter() {
        return new UrlPathFilter();
    }*/

}
