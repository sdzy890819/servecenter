package com.fdz.partnergateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.security.Http401UnauthorizedEntryPoint;
import com.fdz.common.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Resource
    private ApplicationProperties applicationProperties;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private UserDetailsService userCheckService;

    @Bean
    public TokenProvider tokenProvider() {
        return new TokenProvider(applicationProperties.getSecurityConf().getAuthentication().getJwt().getSecret(),
                applicationProperties.getSecurityConf().getAuthentication().getJwt().getTokenValidityInSeconds() * 1000,
                applicationProperties.getSecurityConf().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe() * 1000);
    }

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
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/v1/partner/login").permitAll()
                .antMatchers("/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .apply(securityConfigurerAdapter(authenticationManager));

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

    private JWTConfigurer securityConfigurerAdapter(AuthenticationManager authenticationManager) {
        return new JWTConfigurer(tokenProvider(), authenticationManager);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> list = new ArrayList<>();
        list.add(daoAuthenticationProvider());
        ProviderManager providerManager = new ProviderManager(list);
        return providerManager;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userCheckService);
        return daoAuthenticationProvider;
    }
}

