package com.fdz.thirdpartygateway.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.utils.HttpServletUtils;
import com.fdz.common.utils.StringUtils;
import com.fdz.thirdpartygateway.contants.ThirdparyConstants;
import com.fdz.thirdpartygateway.utils.ServletUtils;
import com.fdz.thirdpartygateway.vo.ThirdpartyRequest;
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
public class RsaAuthenticationFilter extends GenericFilterBean {

    private ObjectMapper objectMapper;

    public RsaAuthenticationFilter(ObjectMapper objectMapper) {
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
            Object channelType = args.get("channel");
            if (channelType != null && ThirdpartyRequest.CHANNEL_TYPE.equals((String) channelType)) {
                request.setAttribute(ThirdparyConstants.Common.CHANNEL_SOURCE, ThirdparyConstants.Common.PARTNER_CHANNEL);
                principal = "PARTNER";
            }
            String uri = null;
            if (request.getRequestURI().equals("/v1/thirdparty/interface")) {
                String method = String.valueOf(args.get("method"));
                if (StringUtils.isNotBlank(method)) {
                    uri = "/v1/" + method.replace("\\.", "/");
                }
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(ServletUtils.buildRequest(request, body.getBytes(Charset.forName("UTF-8")), uri), servletResponse);
        }
    }


}
