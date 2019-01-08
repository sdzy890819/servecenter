package com.fdz.order.controller;


import com.fdz.common.security.SecurityUtils;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.order.dto.CashierDto;
import com.fdz.order.dto.CashierResult;
import com.fdz.order.dto.DeliveryDto;
import com.fdz.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@ApiVersion("1")
@RequestMapping("/order/thirdparty")
@Api("提供给第三方接口")
public class ThirdpartyController {

    @Resource
    private OrderService orderService;

    @ApiOperation("商品购买")
    @PostMapping("/shopping")
    RestResponse<CashierResult> shopping(@RequestBody CashierDto cashierDto) {
        Long partnerId = SecurityUtils.checkLoginAndGetUserByPartner();
        CashierResult result = orderService.shopping(cashierDto, partnerId);
        return RestResponse.success(result);
    }

    @ApiOperation("确认发货")
    @PostMapping("/delivery")
    RestResponse<?> delivery(@RequestBody DeliveryDto dto) {
        Long partnerId = SecurityUtils.checkLoginAndGetUserByPartner();
        return RestResponse.success(null);
    }


}
