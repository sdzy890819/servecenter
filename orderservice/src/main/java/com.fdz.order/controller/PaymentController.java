package com.fdz.order.controller;

import com.fdz.common.dto.SearchResult;
import com.fdz.common.enums.PaymentTypeEnums;
import com.fdz.common.security.SecurityUtils;
import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.order.convert.DtoConvert;
import com.fdz.order.domain.Account;
import com.fdz.order.domain.PaymentRecord;
import com.fdz.order.dto.AccountResult;
import com.fdz.order.dto.PaymentRecordDto;
import com.fdz.order.dto.PaymentRecordSearchDto;
import com.fdz.order.dto.RechargeRecordResult;
import com.fdz.order.service.OrderService;
import com.fdz.order.service.content.ContentService;
import com.fdz.order.service.content.dto.PartnerRestResult;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    RestResponse<SearchResult<List<PaymentRecord>>> search(@RequestBody PaymentRecordSearchDto dto,
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

    @ApiOperation("还款记录信息")
    @PostMapping("/partner/search")
    RestResponse<SearchResult<List<PaymentRecord>>> partnerSearch(@RequestBody PaymentRecordSearchDto dto,
                                                                  @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        Long partnerId = SecurityUtils.checkLoginAndGetUserByPartner();
        if (dto == null) {
            dto = new PaymentRecordSearchDto();
        }
        dto.setPartnerId(partnerId);
        return search(dto, page, pageSize);
    }

    @ApiOperation("首页充值记录信息")
    @PostMapping("/partner/index")
    RestResponse<SearchResult<List<PaymentRecord>>> partnerSearch(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        Long partnerId = SecurityUtils.checkLoginAndGetUserByPartner();
        PaymentRecordSearchDto dto = new PaymentRecordSearchDto();
        dto.setPartnerId(partnerId);
        dto.setPaymentTypeList(Lists.newArrayList(PaymentTypeEnums.RECHARGE.getType(), PaymentTypeEnums.CASH.getType()));
        RestResponse<SearchResult<List<PaymentRecord>>> resultRestResponse = search(dto, page, pageSize);
        SearchResult<List<PaymentRecord>> old = resultRestResponse.getData();
        SearchResult<List<RechargeRecordResult>> searchResult = new SearchResult<>();
        searchResult.setData(dtoConvert.convertPaymentList(old.getData()));
        searchResult.setPage(old.getPage());
        return RestResponse.success(searchResult);
    }

    @ApiOperation("创建还款记录")
    @PostMapping("/create")
    RestResponse create(@RequestBody PaymentRecordDto dto) {
        PaymentRecord paymentRecord = dtoConvert.convert(dto);
        orderService.addRecord(paymentRecord);
        return RestResponse.success(null);
    }

    @ApiOperation("初始化账户.")
    @PostMapping("/init/account/{partnerId}")
    RestResponse initAccount(@PathVariable("partnerId") Long partnerId) {
        Account account = new Account();
        account.setAmount(BigDecimal.ZERO);
        account.setFreezingAmount(BigDecimal.ZERO);
        account.setPartnerId(partnerId);
        account.setRemark("初始化数据");
        orderService.insertSelective(account);
        return RestResponse.success(null);
    }


    /**
     * 机构使用
     *
     * @return
     */
    @ApiOperation("获取机构账户信息")
    @GetMapping("/partner/account")
    RestResponse<?> account() {
        Long partnerId = SecurityUtils.checkLoginAndGetUserByPartner();
        Account account = orderService.findAccountByPartnerId(partnerId);
        PartnerRestResult partnerRestResult = contentService.findPartnerByIdResultMap(Lists.newArrayList(partnerId)).get(partnerId);
        AccountResult accountResult = new AccountResult();
        accountResult.setAmount(account.getAmount());
        accountResult.setContactMobile(partnerRestResult.getContactMobile());
        accountResult.setContacts(partnerRestResult.getContacts());
        accountResult.setFreezingAmount(account.getFreezingAmount());
        accountResult.setPartnerName(partnerRestResult.getName());
        return RestResponse.success(accountResult);
    }


}
