package com.fdz.content.config;

import com.fdz.common.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityConfiguration {

    @Resource
    private ApplicationProperties applicationProperties;

    @Resource
    private UserDetailsService partnerService;

    @Resource
    private UserDetailsService managerService;

    @Bean
    public TokenProvider tokenProvider() {
        return new TokenProvider(applicationProperties.getSecurityConf().getAuthentication().getJwt().getSecret(),
                applicationProperties.getSecurityConf().getAuthentication().getJwt().getTokenValidityInSeconds() * 1000,
                applicationProperties.getSecurityConf().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe() * 1000);
    }

    @Bean
    public TokenProvider tokenProviderManager() {
        return new TokenProvider(applicationProperties.getSecurityConf().getAuthentication().getJwt().getSecret2(),
                applicationProperties.getSecurityConf().getAuthentication().getJwt().getTokenValidityInSeconds() * 1000,
                applicationProperties.getSecurityConf().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe() * 1000);
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(partnerService);
        return daoAuthenticationProvider;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProviderCustomer() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(managerService);
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> list = new ArrayList<>();
        list.add(daoAuthenticationProvider());
        list.add(daoAuthenticationProviderCustomer());
        ProviderManager providerManager = new ProviderManager(list);
        return providerManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

