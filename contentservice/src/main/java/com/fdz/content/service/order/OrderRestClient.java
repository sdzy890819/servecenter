package com.fdz.content.service.order;

import com.fdz.common.web.RestResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("orderservice")
public interface OrderRestClient {

    @PostMapping("/v1/order/payment-record/init/account/{partnerId}")
    RestResponse initAccount(@PathVariable("partnerId") Long partnerId);

}
