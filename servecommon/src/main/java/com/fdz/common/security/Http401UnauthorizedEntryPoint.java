package com.fdz.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.constant.Constants;
import com.fdz.common.web.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint, InitializingBean {

    private final ObjectMapper objectMapper;
    private static final int ACCESS_DENIED = Constants.BusinessCode.USER_NOT_LOGIN;

    public Http401UnauthorizedEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        log.debug("Pre-authenticated entry point called. Rejecting access");
        RestResponse<?> restResponse = RestResponse.error(ACCESS_DENIED, "用户未登录");
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        objectMapper.writeValue(response.getOutputStream(), restResponse);

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(objectMapper, "com.fasterxml.jackson.databind.ObjectMapper is required");
    }
}
