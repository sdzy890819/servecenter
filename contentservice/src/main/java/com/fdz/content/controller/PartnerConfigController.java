package com.fdz.content.controller;

import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.content.convert.DtoConvert;
import com.fdz.content.domain.PartnerInterfaceConfig;
import com.fdz.content.dto.PartnerConfigDto;
import com.fdz.content.service.PartnerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@ApiVersion("1")
@RequestMapping("/partner/config")
@Api("合作伙伴配置信息接口")
public class PartnerConfigController {

    @Resource
    private PartnerService partnerService;

    @Resource
    private DtoConvert dtoConvert;

    @ApiOperation("添加合作伙伴配置信息")
    @PostMapping
    RestResponse<?> config(@RequestBody PartnerConfigDto dto) {
        PartnerInterfaceConfig interfaceConfig = partnerService.findConfigByPartnerAndType(dto.getPartnerId(), dto.getInterfaceType());
        if (interfaceConfig != null) {
            PartnerInterfaceConfig update = new PartnerInterfaceConfig(interfaceConfig.getId());
            update.setInterfaceUrl(dto.getInterfaceUrl());
            partnerService.updateByPrimaryKeySelective(update);
        } else {
            PartnerInterfaceConfig insert = new PartnerInterfaceConfig();
            insert.setInterfaceUrl(dto.getInterfaceUrl());
            insert.setInterfaceType(dto.getInterfaceType());
            insert.setPartnerId(dto.getPartnerId());
            partnerService.insertSelective(insert);
        }
        return RestResponse.success(null);
    }

    @ApiOperation("删除合作伙伴配置信息")
    @GetMapping("/delete/{parterId}/{type}")
    RestResponse delete(@PathVariable("parterId") Long parterId, @PathVariable("type") byte type) {
        PartnerInterfaceConfig interfaceConfig = partnerService.findConfigByPartnerAndType(parterId, type);
        if (interfaceConfig != null) {
            PartnerInterfaceConfig delete = new PartnerInterfaceConfig(interfaceConfig.getId());
            delete.setDelete(true);
            partnerService.updateByPrimaryKeySelective(delete);
        }
        return RestResponse.success(null);
    }

}
