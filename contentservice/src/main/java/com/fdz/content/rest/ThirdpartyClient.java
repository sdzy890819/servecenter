package com.fdz.content.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.exception.BizException;
import com.fdz.common.rest.rsarest.RsaRestRequest;
import com.fdz.common.rest.rsarest.vo.ThirdpartyResponse;
import com.fdz.content.config.ApplicationProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Data
@Slf4j
@Component
public class ThirdpartyClient extends RsaRestRequest {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ApplicationProperties applicationProperties;

    /**
     * @param url
     * @return
     */
    public boolean sync(String url, String channel, Object data, String publicKey, String privateKey, Boolean syncRetEncode) {
        try {
            Map<String, Object> dataMap = objectMapper.readValue(objectMapper.writeValueAsString(data), new TypeReference<Map<String, Object>>() {
            });
            ThirdpartyResponse thirdpartyResponse = request(url, channel, dataMap, publicKey, privateKey, syncRetEncode);
            return thirdpartyResponse.isSuccess();
        } catch (IOException e) {
            throw new BizException("JSON转字符串错误, 请查证", e);
        }
    }


    @Override
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @Override
    protected String getBusinessName() {
        return "第三方同步信息接口";
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
