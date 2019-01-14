package com.fdz.order.controller;

import com.fdz.common.dto.SearchResult;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.order.domain.PaymentRecord;
import com.fdz.order.dto.PaymentRecordSearchDto;
import com.fdz.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@ApiVersion("1")
@RequestMapping("/order/payment-record")
@Api("还款记录")
public class PaymentController {


    @Resource
    private OrderService orderService;

    @ApiOperation("还款记录信息")
    @PostMapping("/search")
    RestResponse<SearchResult<List<PaymentRecord>>> search(PaymentRecordSearchDto dto,
                                                           @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                           @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {

        return RestResponse.success(null);
    }

}
