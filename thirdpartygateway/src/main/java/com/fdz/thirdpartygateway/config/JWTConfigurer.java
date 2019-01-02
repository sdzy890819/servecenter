package com.fdz.thirdpartygateway.config;

import com.fdz.common.constant.Constants;
import com.fdz.common.security.jwt.JWTAuthenticationFilter;
import com.fdz.common.security.jwt.TokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private TokenProvider tokenProvider;

    public JWTConfigurer(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        JWTAuthenticationFilter customFilter = new JWTAuthenticationFilter(tokenProvider, Constants.Common.TOKEN_X);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
