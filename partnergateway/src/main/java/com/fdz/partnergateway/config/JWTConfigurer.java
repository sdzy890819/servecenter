package com.fdz.partnergateway.config;

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

    public JWTConfigurer(TokenProvider tokenProvider, AuthenticationManager authenticationManager) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configure(HttpSecurity http) {
        JWTAuthenticationFilter customFilter = new JWTAuthenticationFilter(tokenProvider, Constants.Common.TOKEN_P, authenticationManager);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
