package com.fdz.thirdpartygateway.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.utils.HttpServletUtils;
import com.fdz.common.utils.StringUtils;
import com.fdz.thirdpartygateway.contants.ThirdparyConstants;
import com.fdz.thirdpartygateway.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@Slf4j
public class XLRsaAuthenticationFilter extends GenericFilterBean {

    private ObjectMapper objectMapper;

    public XLRsaAuthenticationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (!request.getMethod().equals("POST")) {
            log.debug("接口:{}非post方法,不执行数据解密服务", request.getServletPath());
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String body = HttpServletUtils.getRequestBody(request);
        log.debug("接收到request原始信息: {}", body);
        if (StringUtils.isNotBlank(body)) {
            Map<String, Object> args = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {
            });
            String principal = "THIRD_PARTY";
            if (args.get("version") != null && args.get("channelNum") != null && args.get("signMsg") != null) {
                String version = String.valueOf(args.get("version"));
                String channelNum = String.valueOf(args.get("channelNum"));
                String signMsg = String.valueOf(args.get("signMsg"));
                if (StringUtils.isNotBlank(version) && StringUtils.isNotBlank(channelNum) && StringUtils.isNotBlank(signMsg)) {
                    request.setAttribute(ThirdparyConstants.Common.CHANNEL_SOURCE, ThirdparyConstants.Common.XL_CHANNEL);
                    Authentication authentication = new UsernamePasswordAuthenticationToken("XL", null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    principal = "XL";
                }
            } else if (args.get("sign") != null && args.get("biz_data") != null) {
                request.setAttribute(ThirdparyConstants.Common.CHANNEL_SOURCE, ThirdparyConstants.Common.RONG360_CHANNEL);
                Authentication authentication = new UsernamePasswordAuthenticationToken("RONG360", null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                principal = "RONG360";
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(ServletUtils.buildRequest(request, body.getBytes(Charset.forName("UTF-8"))), servletResponse);
        } else {
            filterChain.doFilter(request, servletResponse);
        }

    }


}
