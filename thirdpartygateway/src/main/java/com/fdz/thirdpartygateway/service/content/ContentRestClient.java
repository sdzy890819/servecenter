package com.fdz.thirdpartygateway.service.content;

import com.fdz.common.web.RestResponse;
import com.fdz.thirdpartygateway.service.content.dto.PartnerRestResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("contentservice")
public interface ContentRestClient {


    @GetMapping("/v1/content/partner/unique-key/{uniqueKey}")
    RestResponse<PartnerRestResult> getPartnerByUniqueKey(@PathVariable("uniqueKey") String uniqueKey);
}
