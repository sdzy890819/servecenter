package com.fdz.thirdpartygateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.constant.Constants;
import com.fdz.common.exception.BizException;
import com.fdz.common.utils.RSAUtil;
import com.fdz.common.utils.StringUtils;
import com.fdz.thirdpartygateway.config.ApplicationProperties;
import com.fdz.thirdpartygateway.contants.ThirdparyConstants;
import com.fdz.thirdpartygateway.service.content.ContentService;
import com.fdz.thirdpartygateway.service.content.dto.PartnerRestResult;
import com.fdz.thirdpartygateway.vo.ThirdpartyResp;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RsaPostFilter extends ZuulFilter {

    private final ContentService contentService;

    private final ApplicationProperties applicationProperties;

    private final ObjectMapper objectMapper;

    public RsaPostFilter(ContentService contentService, ApplicationProperties applicationProperties, ObjectMapper objectMapper) {
        this.contentService = contentService;
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public String filterType() {
        return "POST";
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
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            Object channelSource = ctx.getZuulRequestHeaders().get(ThirdparyConstants.Common.CHANNEL_SOURCE);
            if (channelSource != null) {
                String channelSrouceString = (String) channelSource;
                if (StringUtils.isNotBlank(channelSrouceString) && ThirdparyConstants.Common.PARTNER_CHANNEL.equalsIgnoreCase(channelSrouceString)) {
                    String uniqueKey = ctx.getZuulRequestHeaders().get(Constants.Common.UNIQUE_KEY_HEADER);
                    PartnerRestResult partnerRestResult = contentService.findPartnerByUniqueKey(uniqueKey);
                    ThirdpartyResp thirdpartyResp = new ThirdpartyResp();
                    thirdpartyResp.setChannel(uniqueKey);
                    String encodeData = RSAUtil.buildRSAEncryptByPublicKey(ctx.getResponseBody(), partnerRestResult.getPublicKey());
                    String sign = RSAUtil.buildRSASignByPrivateKey(encodeData, applicationProperties.getMyRsaKey().getPrivateKey());
                    thirdpartyResp.setRespData(encodeData);
                    thirdpartyResp.setRespSign(sign);
                    ctx.setResponseBody(objectMapper.writeValueAsString(thirdpartyResp));
                }
            }
        } catch (Exception e) {
            throw new BizException("DONT TALK，KISS ME ", e);
        }

        return null;
    }
}
