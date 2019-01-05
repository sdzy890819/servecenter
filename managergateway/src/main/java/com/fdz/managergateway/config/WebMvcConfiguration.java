package com.fdz.managergateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mobile.device.DeviceResolverRequestFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * 加载基础配置。
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Resource
    private ApplicationProperties applicationProperties;

    /**
     * 当用户cors配置为ture时。所有请求应用。当前config配置
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "application.cors", value = "enabled", havingValue = "true")
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = applicationProperties.getCors().getConfig();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    FilterRegistrationBean deviceResolverRequestFilter() {
        FilterRegistrationBean filter = new FilterRegistrationBean(new DeviceResolverRequestFilter());
        filter.addUrlPatterns("/*");
        return filter;
    }


}
