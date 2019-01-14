package com.fdz.content.controller;

import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.content.convert.DtoConvert;
import com.fdz.content.domain.Partner;
import com.fdz.content.domain.PartnerInterfaceConfig;
import com.fdz.content.dto.PageDataResult;
import com.fdz.content.dto.PartnerConfigDto;
import com.fdz.content.service.PartnerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@ApiVersion("1")
@RequestMapping("/content/partner/config")
@Api("合作伙伴配置信息接口")
public class PartnerConfigController {

    @Resource
    private PartnerService partnerService;

    @Resource
    private DtoConvert dtoConvert;

    @ApiOperation("添加合作伙伴配置信息")
    @PostMapping("/create")
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


    @ApiOperation("查询合作伙伴配置信息")
    @PostMapping("/search")
    RestResponse<?> search(@RequestBody PartnerInterfaceConfig partnerInterfaceConfig,
                           @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {

        Page pageObj = new Page(page, pageSize);
        List<PartnerInterfaceConfig> list = partnerService.searchConfig(partnerInterfaceConfig, pageObj);
        if (StringUtils.isNotEmpty(list)) {
            List<Long> partnerIds = new ArrayList<>();
            list.forEach(a -> partnerIds.add(a.getPartnerId()));
            Map<Long, Partner> partnerMap = partnerService.findPartnerByIdResultMap(partnerIds);
            list.forEach(a -> a.setPartnerName(partnerMap != null && partnerMap.get(a.getPartnerId()) != null ? partnerMap.get(a.getPartnerId()).getName() : ""));
        }
        PageDataResult pageDataResult = new PageDataResult();
        pageDataResult.setPage(pageObj);
        pageDataResult.setData(list);
        pageDataResult.setSearchCondition(partnerInterfaceConfig);
        return RestResponse.success(pageDataResult);
    }

    @ApiOperation("删除合作伙伴配置信息")
    @GetMapping("/delete/{id}")
    RestResponse<?> delete(@PathVariable("id") Long id) {
        PartnerInterfaceConfig partnerInterfaceConfig = new PartnerInterfaceConfig(id);
        partnerInterfaceConfig.setDelete(true);
        partnerService.updateByPrimaryKeySelective(partnerInterfaceConfig);
        return RestResponse.success(null);
    }

    @ApiOperation("获取合作伙伴配置信息")
    @GetMapping("/detail/{id}")
    RestResponse<?> detail(@PathVariable("id") Long id) {
        PartnerInterfaceConfig partnerInterfaceConfig = partnerService.selectPartnerInterfaceConfigByPrimaryKey(id);
        return RestResponse.success(partnerInterfaceConfig);
    }

}
