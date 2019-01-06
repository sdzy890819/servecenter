package com.fdz.thirdpartygateway.service.content;

import com.fdz.common.exception.BizException;
import com.fdz.thirdpartygateway.service.content.dto.PartnerRestResult;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ContentService {

    @Resource
    private ContentRestClient contentRestClient;

    @HystrixCommand(fallbackMethod = "findPartnerByUniqueKeyFallback")
    public PartnerRestResult findPartnerByUniqueKey(String key) {
        return contentRestClient.getPartnerByUniqueKey(key).getData();
    }

    public PartnerRestResult findPartnerByUniqueKeyFallback(String key, Throwable throwable) {
        throw new BizException("未识别的机构信息");
    }
}
