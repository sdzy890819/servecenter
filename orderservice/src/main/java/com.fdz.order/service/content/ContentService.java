package com.fdz.order.service.content;

import com.fdz.common.exception.BizException;
import com.fdz.common.web.RestResponse;
import com.fdz.order.service.content.dto.PartnerRestResult;
import com.fdz.order.service.content.dto.RecordDto;
import com.fdz.order.service.content.dto.ThirdpartyProductDto;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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

    @HystrixCommand(fallbackMethod = "detailFallbak")
    public ThirdpartyProductDto detail(Long id) {
        return contentRestClient.detail(id).getData();
    }

    public ThirdpartyProductDto detailFallbak(Long id, Throwable throwable) {
        throw new BizException("查找不到当前产品.");
    }

    @HystrixCommand(fallbackMethod = "detailListFallbak")
    public List<ThirdpartyProductDto> detailList(List<Long> ids) {
        return contentRestClient.detailList(ids).getData();
    }

    public List<ThirdpartyProductDto> detailListFallbak(List<Long> ids, Throwable throwable) {
        throw new BizException("查找不到相关产品");
    }

    public void create(RecordDto dto) {
        RestResponse restResponse = contentRestClient.create(dto);
        log.info("记录执行计划， 合作伙伴: {} 执行状态: {}", dto.getPartnerId(), restResponse.isSuccess());
    }

    @HystrixCommand(fallbackMethod = "findPartnerByIdResultMapFallback")
    public Map<Long, PartnerRestResult> findPartnerByIdResultMap(List<Long> partnerIds) {
        return contentRestClient.findPartnerByIdResultMap(partnerIds).getData();
    }

    public Map<Long, PartnerRestResult> findPartnerByIdResultMapFallback(List<Long> partnerIds) {
        return new HashMap<>(0);
    }

}
