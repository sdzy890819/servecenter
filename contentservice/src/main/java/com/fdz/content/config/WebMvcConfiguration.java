package com.fdz.content.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Resource
    private ApplicationProperties applicationProperties;

    @Bean
    @ConditionalOnProperty(prefix = "application.cors", value = "enabled", havingValue = "true")
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = applicationProperties.getCors().getConfig();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
