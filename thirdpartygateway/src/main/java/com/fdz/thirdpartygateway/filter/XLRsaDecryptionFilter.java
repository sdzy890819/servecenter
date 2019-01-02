package com.fdz.thirdpartygateway.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.constant.Constants;
import com.fdz.common.exception.BizException;
import com.fdz.common.utils.HttpServletUtils;
import com.fdz.common.utils.StringUtils;
import com.fdz.thirdpartygateway.config.ApplicationProperties;
import com.fdz.thirdpartygateway.contants.ThirdparyConstants;
import com.fdz.thirdpartygateway.utils.ServletUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Map;

@Slf4j
public class XLRsaDecryptionFilter extends ZuulFilter {

    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

    private ApplicationProperties applicationProperties;

    private ObjectMapper objectMapper;

    public XLRsaDecryptionFilter(ApplicationProperties applicationProperties, ObjectMapper objectMapper) {
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
    }

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
        return RequestContext.getCurrentContext().getRequest().getMethod().equalsIgnoreCase("POST");
    }

    @Override
    public Object run() {
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();
            Object channelSource = request.getAttribute(ThirdparyConstants.Common.CHANNEL_SOURCE);
            if (channelSource != null) {
                String channelSrouceString = (String) channelSource;
                if (StringUtils.isNotBlank(channelSrouceString) && ThirdparyConstants.Common.XL_CHANNEL.equalsIgnoreCase(channelSrouceString)) {
                    Map<String, String> args = objectMapper.readValue(HttpServletUtils.getRequestBody(request), new TypeReference<Map<String, String>>() {
                    });
                    String data = args.get("data");
                    //boolean bool = XLRSAUtil.inspectionSign(data, args.get("signMsg"), applicationProperties.getXlPro().getPublicKey());
                    if (true) {
                        //String decodeData = XLRSAUtil.resultAnalysis(data, applicationProperties.getXlPro().getPrivateKey());
                        String decodeData = "";
                        log.debug("打印笑脸解密后的数据: {}", decodeData);
                        HttpServletRequest servletRequest = ServletUtils.buildRequest(request, decodeData.getBytes(Charset.forName("UTF-8")));
                        ctx.getZuulRequestHeaders().put("content-type", APPLICATION_JSON_CHARSET_UTF_8);
                        ctx.getZuulRequestHeaders().put(Constants.OpenApi.XL_REQUEST_NO, args.get("requestNo"));
                        ctx.setRequest(servletRequest);
                    }
                } else if (StringUtils.isNotBlank(channelSrouceString) && ThirdparyConstants.Common.RONG360_CHANNEL.equalsIgnoreCase(channelSrouceString)) {
                    Map<String, String> args = objectMapper.readValue(HttpServletUtils.getRequestBody(request), new TypeReference<Map<String, String>>() {
                    });
                    String data = args.get("biz_data");
                    String sign = args.get("sign");
                    //boolean bool = ReceiveClient.inspect(applicationProperties.getRong360().getPublicKey(), data, sign);
                    if (true) {
                        log.debug("打印融360数据: {}", data);
                        HttpServletRequest servletRequest = ServletUtils.buildRequest(request, data.getBytes(Charset.forName("UTF-8")));
                        ctx.getZuulRequestHeaders().put("content-type", APPLICATION_JSON_CHARSET_UTF_8);
                        ctx.getZuulRequestHeaders().put(Constants.OpenApi.XL_REQUEST_NO, args.get("requestNo"));
                        ctx.setRequest(servletRequest);
                    } else {
                        throw new BizException("无效请求");
                    }

                }
            }
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("识别失败", e);
        }
        return null;
    }

}
