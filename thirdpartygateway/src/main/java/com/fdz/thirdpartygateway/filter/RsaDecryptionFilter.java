package com.fdz.thirdpartygateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.constant.Constants;
import com.fdz.common.exception.BizException;
import com.fdz.common.utils.HttpServletUtils;
import com.fdz.common.utils.RSAUtil;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.utils.UserDisassembly;
import com.fdz.thirdpartygateway.config.ApplicationProperties;
import com.fdz.thirdpartygateway.contants.ThirdparyConstants;
import com.fdz.thirdpartygateway.service.content.ContentService;
import com.fdz.thirdpartygateway.service.content.dto.PartnerRestResult;
import com.fdz.thirdpartygateway.utils.ServletUtils;
import com.fdz.thirdpartygateway.vo.ThirdpartyRequest;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;

@Slf4j
public class RsaDecryptionFilter extends ZuulFilter {

    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

    private final ApplicationProperties applicationProperties;

    private final ObjectMapper objectMapper;

    private final ContentService contentService;

    public RsaDecryptionFilter(ApplicationProperties applicationProperties, ObjectMapper objectMapper, ContentService contentService) {
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
        this.contentService = contentService;
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
                if (StringUtils.isNotBlank(channelSrouceString) && ThirdparyConstants.Common.PARTNER_CHANNEL.equalsIgnoreCase(channelSrouceString)) {
                    ThirdpartyRequest thirdpartyRequest = objectMapper.readValue(HttpServletUtils.getRequestBody(request), ThirdpartyRequest.class);
                    String data = thirdpartyRequest.getData();
                    PartnerRestResult partnerRestResult = contentService.findPartnerByUniqueKey(thirdpartyRequest.getChannel());
                    if (partnerRestResult == null) {
                        throw new BizException("渠道不存在.");
                    }
                    boolean bool = RSAUtil.inspectionSign(data, thirdpartyRequest.getSign(), partnerRestResult.getPublicKey());
                    if (true) {
                        String decodeData = RSAUtil.resultAnalysis(data, applicationProperties.getMyRsaKey().getPrivateKey());
                        log.debug("打印解密后的数据信息: {}", decodeData);
                        HttpServletRequest servletRequest = ServletUtils.buildRequest(request, decodeData.getBytes(Charset.forName("UTF-8")));
                        ctx.getZuulRequestHeaders().put("content-type", APPLICATION_JSON_CHARSET_UTF_8);
                        ctx.getZuulRequestHeaders().put(Constants.Common.UNIQUE_KEY_HEADER, partnerRestResult.getUniqueKey());
                        ctx.getZuulRequestHeaders().put(Constants.Common.P_USER_INFO_HEADER, UserDisassembly.assembleP(partnerRestResult.getId()));
                        ctx.setRequest(servletRequest);
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
