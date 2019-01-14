package com.fdz.order.controller;

import com.fdz.common.dto.SearchResult;
import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.order.convert.DtoConvert;
import com.fdz.order.domain.PaymentRecord;
import com.fdz.order.dto.PaymentRecordDto;
import com.fdz.order.dto.PaymentRecordSearchDto;
import com.fdz.order.service.OrderService;
import com.fdz.order.service.content.ContentService;
import com.fdz.order.service.content.dto.PartnerRestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@ApiVersion("1")
@RequestMapping("/order/payment-record")
@Api("还款记录")
public class PaymentController {

    @Resource
    private DtoConvert dtoConvert;

    @Resource
    private OrderService orderService;

    @Resource
    private ContentService contentService;

    @ApiOperation("还款记录信息")
    @PostMapping("/search")
    RestResponse<SearchResult<List<PaymentRecord>>> search(PaymentRecordSearchDto dto,
                                                           @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                           @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        Page pageObj = new Page(page, pageSize);
        List<PaymentRecord> list = orderService.searchPaymentRecord(dto, pageObj);
        if (StringUtils.isNotEmpty(list)) {
            List<Long> partnerIds = new ArrayList<>();
            list.forEach(a -> partnerIds.add(a.getPartnerId()));
            Map<Long, PartnerRestResult> map = contentService.findPartnerByIdResultMap(partnerIds);
            list.forEach(a -> a.setPartnerName(map.get(a.getPartnerId()) != null ? map.get(a.getPartnerId()).getName() : ""));
        }
        SearchResult<List<PaymentRecord>> searchResult = new SearchResult<>();
        searchResult.setData(list);
        searchResult.setPage(pageObj);
        searchResult.setSearchCondition(dto);
        return RestResponse.success(searchResult);
    }


    @ApiOperation("创建还款记录")
    @PostMapping("/create")
    RestResponse create(@RequestBody PaymentRecordDto dto) {
        PaymentRecord paymentRecord = dtoConvert.convert(dto);
        orderService.addRecord(paymentRecord);
        return RestResponse.success(null);
    }


}
