package com.fdz.common.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.constant.Constants;
import com.fdz.common.exception.BizException;
import com.fdz.common.utils.JWTUtil;
import com.fdz.common.utils.StringUtils;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


public class JwtAuthenitcationTmpFilter implements HandlerInterceptor {

    private String jwt = "jwt";

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        JWTUtil jwtUtil = new JWTUtil();
        Claims claims = null;
        try {
            claims = jwtUtil.parseJWT(request.getHeader("Authorization"));
            Map<String, String> map = objectMapper.readValue(claims.getSubject(), new TypeReference<Map<String, String>>() {
            });
            String userId = map.get("userId");
            if (StringUtils.isNotBlank(userId)) {
                request.setAttribute(Constants.Common.X_USER_INFO_HEADER, userId);
            } else {
                throw new BizException(101001, "Access Denied");
            }
        } catch (Exception e) {
            throw new BizException(101001, "Access Denied");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
