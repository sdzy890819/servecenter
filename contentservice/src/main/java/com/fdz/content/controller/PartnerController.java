package com.fdz.content.controller;


import com.fdz.common.exception.BizException;
import com.fdz.common.security.SecurityUtils;
import com.fdz.common.utils.EncryptUtil;
import com.fdz.common.utils.Page;
import com.fdz.common.utils.RSAUtil;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.content.convert.DtoConvert;
import com.fdz.content.domain.Partner;
import com.fdz.content.domain.PartnerUser;
import com.fdz.content.dto.PageDataResult;
import com.fdz.content.dto.PartnerDto;
import com.fdz.content.dto.PartnerResult;
import com.fdz.content.dto.PartnerUserDto;
import com.fdz.content.service.PartnerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
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
        Map<String, String> map = RSAUtil.initRSAKey(RSAUtil.ALGORITHM_RSA_PRIVATE_KEY_LENGTH);
        partner.setMyKey(map.get(RSAUtil.PRIVATE_KEY));
        partner.setPublicKey(map.get(RSAUtil.PUBLIC_KEY));
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
                if (StringUtils.isBlank(partner.getMyKey()) && StringUtils.isBlank(partner.getMyPublicKey())) {
                    Map<String, String> map = RSAUtil.initRSAKey(RSAUtil.ALGORITHM_RSA_PRIVATE_KEY_LENGTH);
                    partner.setMyKey(map.get(RSAUtil.PRIVATE_KEY));
                    partner.setPublicKey(map.get(RSAUtil.PUBLIC_KEY));
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
    @PostMapping("/ids")
    RestResponse<Map<Long, Partner>> findPartnerByIdResultMap(@RequestBody List<Long> partnerIds) {
        List<Partner> list = partnerService.findPartnerByIds(partnerIds);
        Map<Long, Partner> result = new HashMap<>();
        if (StringUtils.isNotEmpty(list)) {
            list.forEach(a -> result.put(a.getId(), a));
        }
        return RestResponse.success(result);
    }

    @ApiOperation("获取合作伙伴列表")
    @GetMapping("/all")
    RestResponse<List<Partner>> findPartnerList() {
        List<Partner> list = partnerService.findAll();
        return RestResponse.success(list);
    }

    @ApiOperation("获取机构用户信息")
    @GetMapping("/user/{partnerId}")
    RestResponse user(@PathVariable("partnerId") Long partnerId) {
        PartnerUser partnerUser = partnerService.findUserByPartnerId(partnerId);
        return RestResponse.success(partnerUser);
    }


    @ApiOperation("合作伙伴用户信息")
    @PostMapping("/user/update")
    RestResponse updateUser(@RequestBody PartnerUserDto dto) {
        if (dto.getPartnerId() == null) {
            throw new BizException("机构ID不可以为空");
        }
        PartnerUser old = partnerService.findUserByPartnerId(dto.getPartnerId());
        if (old != null) {
            PartnerUser partnerUser = new PartnerUser(old.getId());
            if (StringUtils.isNotBlank(dto.getUserName())) {
                partnerUser.setUserName(dto.getUserName());
            }
            if (StringUtils.isNotBlank(dto.getPassword())) {
                partnerUser.setPassword(EncryptUtil.encryptPwd(StringUtils.isNotBlank(dto.getUserName()) ? dto.getUserName() : old.getUserName(), dto.getPassword()));
            }
            if (StringUtils.isNotBlank(dto.getRealName())) {
                partnerUser.setRealName(dto.getRealName());
            }
            partnerService.updateByPrimaryKeySelective(partnerUser);
        } else {
            if (StringUtils.isBlank(dto.getUserName()) || StringUtils.isBlank(dto.getPassword())) {
                throw new BizException("用户名或者密码不可以为空");
            }
            PartnerUser partnerUser = dtoConvert.convert(dto);
            String pwd = EncryptUtil.encryptPwd(partnerUser.getUserName(), partnerUser.getPassword());
            partnerUser.setPassword(pwd);
            partnerService.insertSelective(partnerUser);
        }
        return RestResponse.success(null);
    }

}
