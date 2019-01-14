package com.fdz.content.controller;


import com.fdz.common.exception.BizException;
import com.fdz.common.security.SecurityUtils;
import com.fdz.common.utils.EncryptUtil;
import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.content.convert.DtoConvert;
import com.fdz.content.domain.Partner;
import com.fdz.content.domain.PartnerUser;
import com.fdz.content.dto.PageDataResult;
import com.fdz.content.dto.PartnerDto;
import com.fdz.content.dto.PartnerResult;
import com.fdz.content.service.PartnerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@ApiVersion("1")
@RequestMapping("/content/partner")
@Api("合作伙伴信息接口")
public class PartnerController {

    @Resource
    private DtoConvert dtoConvert;

    @Resource
    private PartnerService partnerService;

    @ApiOperation("获取当前合作伙伴信息")
    @GetMapping("/info")
    RestResponse<PartnerResult> info() {
        Long userId = SecurityUtils.checkLoginAndGetUserByPartner();
        PartnerUser partnerUser = partnerService.selectPartnerUserByPrimaryKey(userId);
        Partner partner = partnerService.selectPartnerByPrimaryKey(partnerUser.getPartnerId());
        PartnerResult partnerResult = new PartnerResult();
        partnerResult.setContactMobile(StringUtils.hideSomeString(partner.getContactMobile()));
        partnerResult.setContacts(StringUtils.hideSomeString(partner.getContacts()));
        partnerResult.setName(StringUtils.hideSomeString(partner.getName()));
        partnerResult.setShortName(StringUtils.hideSomeString(partner.getShortName()));
        return RestResponse.success(partnerResult);
    }

    @ApiOperation("获取合作伙伴列表")
    @PostMapping("/search")
    RestResponse<PageDataResult<List<Partner>>> search(@RequestBody PartnerDto dto,
                                                       @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                       @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        Page pagObj = new Page(page, pageSize);
        List<Partner> list = partnerService.searchPartner(dto, pagObj);
        PageDataResult pageDataResult = new PageDataResult();
        pageDataResult.setData(list);
        pageDataResult.setPage(pagObj);
        return RestResponse.success(pageDataResult);
    }

    @ApiOperation("创建合作伙伴")
    @PostMapping("/create")
    RestResponse create(@RequestBody PartnerDto dto) {
        Partner partner = dtoConvert.convert(dto);
        String uniqueKey = EncryptUtil.md5(UUID.randomUUID().toString());
        partner.setUniqueKey(uniqueKey);
        partnerService.insertSelective(partner);
        return RestResponse.success(null);
    }

    @ApiOperation("修改合作伙伴")
    @PostMapping("/update")
    RestResponse update(@RequestBody PartnerDto dto) {
        if (dto.getId() != null) {
            Partner partner = partnerService.selectPartnerByPrimaryKey(dto.getId());
            if (partner != null) {
                if (StringUtils.isNotBlank(dto.getCode())) {
                    partner.setCode(dto.getCode());
                }
                if (StringUtils.isNotBlank(dto.getName())) {
                    partner.setName(dto.getName());
                }
                if (StringUtils.isNotBlank(dto.getShortName())) {
                    partner.setShortName(dto.getShortName());
                }
                if (dto.getNature() != null) {
                    partner.setNature(dto.getNature());
                }
                if (StringUtils.isNotBlank(dto.getContacts())) {
                    partner.setContacts(dto.getContacts());
                }
                if (StringUtils.isNotBlank(dto.getContactMobile())) {
                    partner.setContactMobile(dto.getContactMobile());
                }
                if (dto.getServiceRate() != null) {
                    partner.setServiceRate(dto.getServiceRate());
                }
                if (StringUtils.isNotBlank(dto.getPublicKey())) {
                    partner.setPublicKey(dto.getPublicKey());
                }
                partnerService.updateByPrimaryKeySelective(partner);
            } else {
                throw new BizException("选择的合作伙伴不存在");
            }
        } else {
            throw new BizException("选择的合作伙伴错误");
        }
        return RestResponse.success(null);
    }

    @ApiOperation("删除合作伙伴")
    @GetMapping("/delete/{id}")
    RestResponse delete(@PathVariable("id") Long id) {
        Partner partner = new Partner(id);
        partner.setDelete(true);
        partnerService.updateByPrimaryKeySelective(partner);
        return RestResponse.success(null);
    }

    @ApiOperation("根据唯一KEY获取合作伙伴信息")
    @GetMapping("/unique-key/{uniqueKey}")
    RestResponse<Partner> getPartnerByUniqueKey(@PathVariable("uniqueKey") String uniqueKey) {
        Partner partner = partnerService.findPartnerByUniqueKey(uniqueKey);
        return RestResponse.success(partner);
    }

    @ApiOperation("根据ID获取合作伙伴信息")
    @GetMapping("/id/{id}")
    RestResponse<Partner> findPartnerById(@PathVariable("id") Long id) {
        Partner partner = partnerService.selectPartnerByPrimaryKey(id);
        return RestResponse.success(partner);
    }

    @ApiOperation("根据所有id获取合作伙伴信息")
    @PostMapping("/v1/content/partner/ids")
    RestResponse<Map<Long, Partner>> findPartnerByIdResultMap(@RequestBody List<Long> partnerIds) {
        return RestResponse.success(partnerService.findPartnerByIds(partnerIds));
    }

    @ApiOperation("获取合作伙伴列表")
    @GetMapping("/all")
    RestResponse<List<Partner>> findPartnerList() {
        List<Partner> list = partnerService.findAll();
        return RestResponse.success(list);
    }

}
