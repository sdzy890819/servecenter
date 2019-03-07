package com.fdz.common.rest.rsarest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fdz.common.constant.Constants;
import com.fdz.common.exception.BizException;
import com.fdz.common.rest.RestRequest;
import com.fdz.common.rest.rsarest.vo.ThirdpartyResponse;
import com.fdz.common.rest.rsarest.vo.ThirdpartyResult;
import com.fdz.common.rest.rsarest.vo.ThirdpartyVo;
import com.fdz.common.utils.RSAUtil;
import com.fdz.common.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@Data
@Slf4j
public abstract class RsaRestRequest extends RestRequest {

    public RsaRestRequest() {
        super();
    }

    public <Result> ThirdpartyResponse<Result> request(String url, String channel, Map<String, Object> data, String publicKey, String privateKey) {
        return request(url, channel, data, publicKey, privateKey, new TypeReference<ThirdpartyResponse<Result>>() {
        });
    }

    public <Result> ThirdpartyResponse<Result> request(String url, String channel, Map<String, Object> data, String publicKey, String privateKey, TypeReference typeReference) {
        ThirdpartyResult thirdpartyResult = request(url, initParam(channel, data, publicKey, privateKey));
        if (StringUtils.isNotBlank(thirdpartyResult.getRespData()) && StringUtils.isNotBlank(thirdpartyResult.getRespSign())) {
            boolean bool = RSAUtil.inspectionSign(thirdpartyResult.getRespData(), thirdpartyResult.getRespSign(), publicKey);
            if (bool) {
                String decodeData = RSAUtil.resultAnalysis(thirdpartyResult.getRespData(), privateKey);
                if (StringUtils.isNotBlank(decodeData)) {
                    try {
                        ThirdpartyResponse<Result> result = getObjectMapper().readValue(decodeData, typeReference);
                        return result;
                    } catch (IOException e) {
                        throw new BizException(Constants.BusinessCode.THIRDPARTY_ERROR_JSON, "执行decodeData失败");
                    }
                }
            } else {
                throw new BizException(Constants.BusinessCode.THIRDPARTY_ERROR_RSA, "验签失败");
            }
        } else {
            log.warn("被调用方未返回data、signMsg信息。不做解密处理, url = {}, channel = {}", url, channel);
        }
        return null;
    }

    /**
     * 请求request
     *
     * @return <Result>
     */
    public ThirdpartyResult request(String url, ThirdpartyVo thirdpartyVo) {
        try {
            Map<String, Object> jsonMap = getObjectMapper().readValue(getObjectMapper().writeValueAsString(thirdpartyVo), new TypeReference<Map<String, Object>>() {
            });
            String result = doPostJson(url, jsonMap);
            return getObjectMapper().readValue(result, ThirdpartyResult.class);
        } catch (IOException e) {
            throw new BizException("Json转换错误");
        }
    }

    private ThirdpartyVo initParam(String channel, Map<String, Object> data, String publicKey, String privateKey) {
        ThirdpartyVo thirdpartyVo = new ThirdpartyVo();
        thirdpartyVo.setChannel(channel);
        try {
            String encodeData = RSAUtil.buildRSAEncryptByPublicKey(getObjectMapper().writeValueAsString(data), publicKey);
            String sign = RSAUtil.buildRSASignByPrivateKey(encodeData, privateKey);
            thirdpartyVo.setData(encodeData);
            thirdpartyVo.setSign(sign);
        } catch (JsonProcessingException e) {
            throw new BizException("加密失败, 无法对当前数据加密, channel" + channel);
        }
        return thirdpartyVo;
    }

}
