package com.fdz.thirdpartygateway.filter;

import com.fdz.common.constant.Constants;
import com.fdz.common.security.SecurityUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * 用户认证.
 */
@Slf4j
public class PassportAuthenticationFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String userId = SecurityUtils.getCurrenUserIdByAuthentication();
        ctx.addZuulRequestHeader(Constants.Common.X_USER_INFO_HEADER, userId);
        MDC.put("userID", userId);
        return null;
    }
}
