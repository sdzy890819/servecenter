package com.fdz.thirdpartygateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.security.Http401UnauthorizedEntryPoint;
import com.fdz.thirdpartygateway.filter.RsaAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint(ObjectMapper objectMapper) {
        return new Http401UnauthorizedEntryPoint(objectMapper);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(http401UnauthorizedEntryPoint(objectMapper))
                .and()
                .cors().and().csrf().disable().headers().frameOptions().disable()
                .and()
                .addFilterBefore(rsaAuthenticationFilter(objectMapper), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/**").authenticated()
                .anyRequest().permitAll();

    }

    @Bean
    public RsaAuthenticationFilter rsaAuthenticationFilter(ObjectMapper objectMapper) {
        return new RsaAuthenticationFilter(objectMapper);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/swagger-ui.html")
                .antMatchers("/webjars/**")
                .antMatchers("/swagger-resources")
                .antMatchers("/v2/api-docs")
                .antMatchers("/api-docs")
                .antMatchers("/api-docs/**")
                .antMatchers("/swagger-resources/**");
    }
}

