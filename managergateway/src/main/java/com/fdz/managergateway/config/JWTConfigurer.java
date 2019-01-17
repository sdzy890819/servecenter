package com.fdz.managergateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.constant.Constants;
import com.fdz.common.security.jwt.JWTAuthenticationFilter;
import com.fdz.common.security.jwt.TokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private TokenProvider tokenProvider;
    private AuthenticationManager authenticationManager;
    private ObjectMapper objectMapper;

    public JWTConfigurer(TokenProvider tokenProvider, AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void configure(HttpSecurity http) {
        JWTAuthenticationFilter customFilter = new JWTAuthenticationFilter(tokenProvider, Constants.Common.TOKEN_M, authenticationManager, objectMapper);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
