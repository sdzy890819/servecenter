package com.fdz.thirdpartygateway.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.constant.Constants;
import com.fdz.common.exception.BizException;
import com.fdz.common.rest.rsarest.vo.ThirdpartyResponse;
import com.fdz.common.utils.RSAUtil;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.web.RestResponse;
import com.fdz.thirdpartygateway.config.ApplicationProperties;
import com.fdz.thirdpartygateway.contants.ThirdparyConstants;
import com.fdz.thirdpartygateway.service.content.ContentService;
import com.fdz.thirdpartygateway.service.content.dto.PartnerRestResult;
import com.fdz.thirdpartygateway.vo.ThirdpartyResp;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

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
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
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
                    InputStream stream = ctx.getResponseDataStream();
                    String body = StreamUtils.copyToString(new GZIPInputStream(stream), Charset.forName("UTF-8"));
                    RestResponse<Object> restResponse = objectMapper.readValue(body, new TypeReference<RestResponse<Object>>() {
                    });
                    if (restResponse.getCode() == 0) {
                        restResponse.setCode(ThirdpartyResponse.SUCCESS_CODE);
                    } else {
                        restResponse.setCode(ctx.getResponseStatusCode());
                    }
                    String encodeData = RSAUtil.buildRSAEncryptByPublicKey(objectMapper.writeValueAsString(restResponse), partnerRestResult.getPublicKey());
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
