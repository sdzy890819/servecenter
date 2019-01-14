package com.fdz.order.service.content;

import com.fdz.common.web.RestResponse;
import com.fdz.order.service.content.dto.PartnerRestResult;
import com.fdz.order.service.content.dto.RecordDto;
import com.fdz.order.service.content.dto.ThirdpartyProductDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient("contentservice")
public interface ContentRestClient {


    @GetMapping("/v1/content/partner/unique-key/{uniqueKey}")
    RestResponse<PartnerRestResult> getPartnerByUniqueKey(@PathVariable("uniqueKey") String uniqueKey);

    @GetMapping("/v1/content/partner-product/detail/{id}")
    RestResponse<ThirdpartyProductDto> detail(@PathVariable("id") Long id);

    @PostMapping("/v1/content/partner-product/detail-list")
    RestResponse<List<ThirdpartyProductDto>> detailList(List<Long> ids);

    @PostMapping("/v1/content/record/create")
    RestResponse<?> create(@RequestBody RecordDto dto);

    @PostMapping("/v1/content/partner/ids")
    RestResponse<Map<Long, PartnerRestResult>> findPartnerByIdResultMap(@RequestBody List<Long> partnerIds);
}
