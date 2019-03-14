package com.fdz.thirdpartygateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.constant.Constants;
import com.fdz.common.exception.BizException;
import com.fdz.common.rest.rsarest.vo.ThirdpartyResponse;
import com.fdz.thirdpartygateway.contants.ThirdparyConstants;
import com.fdz.thirdpartygateway.vo.ThirdpartyResp;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class ZuulExceptionFilter extends ZuulFilter {

    private final ObjectMapper objectMapper;

    public ZuulExceptionFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String filterType() {
        return FilterConstants.ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_ERROR_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getThrowable() != null;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Boolean encode = Boolean.valueOf(ctx.getZuulRequestHeaders().get(ThirdparyConstants.Common.RETURN_DECODE));
        Throwable throwable = ctx.getThrowable();
        String message = "";
        if (throwable.getCause() instanceof BizException) {
            message = (throwable.getCause()).getMessage();
        } else {
            message = "哎呀，服务好像出去旅游了. 稍等片刻，喝口茶";
        }
        Object returnObject = null;
        if (encode) {
            ThirdpartyResp thirdpartyResp = new ThirdpartyResp();
            String uniqueKey = ctx.getZuulRequestHeaders().get(Constants.Common.UNIQUE_KEY_HEADER);
            thirdpartyResp.setChannel(uniqueKey);
            thirdpartyResp.setCode(ThirdpartyResponse.ERROR_CODE);
            thirdpartyResp.setMessage(message);
            returnObject = thirdpartyResp;
        } else {
            ThirdpartyResponse thirdpartyResponse = new ThirdpartyResponse();
            thirdpartyResponse.setCode(ThirdpartyResponse.ERROR_CODE);
            thirdpartyResponse.setMessage(message);
            returnObject = thirdpartyResponse;
        }
        PrintWriter printWriter = null;
        try {
            ctx.setResponseStatusCode(200);
            printWriter = ctx.getResponse().getWriter();
            printWriter.write(objectMapper.writeValueAsString(returnObject));
            printWriter.flush();
        } catch (JsonProcessingException e) {
            log.error("出现异常，json转换错误");
        } catch (IOException e) {
            log.error("输出异常..");
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
        return null;
    }
}
