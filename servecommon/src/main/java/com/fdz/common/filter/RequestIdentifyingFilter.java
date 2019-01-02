package com.fdz.common.filter;

import com.fdz.common.constant.Constants;
import com.fdz.common.utils.HttpServletUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class RequestIdentifyingFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String sessionId = request.getSession().getId();
        if (log.isDebugEnabled()) {
            log.debug("");
        }
        request.setAttribute(Constants.Common.MDC_SESSION_ID, sessionId);
        String remoteIp = HttpServletUtils.getRemoteIPAddress(request);
        MDC.put(Constants.Common.MDC_SESSION_ID, sessionId);
        MDC.put(Constants.Common.MDC_REMOTE_IP, remoteIp);
        requestContext.addZuulRequestHeader(Constants.Common.X_SESSION_ID_HEADER, sessionId);
        requestContext.addZuulRequestHeader(Constants.Common.X_REMOTE_IP_HEADER, remoteIp);
        return null;
    }
}
