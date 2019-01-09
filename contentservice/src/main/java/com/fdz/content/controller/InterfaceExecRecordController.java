package com.fdz.content.controller;

import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.content.dto.RecordDto;
import com.fdz.content.service.PartnerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@ApiVersion("1")
@RequestMapping("/content/record")
@Api("合作伙伴信息接口")
public class InterfaceExecRecordController {

    @Resource
    private PartnerService partnerService;

    @ApiOperation("记录执行计划")
    @PostMapping("/create")
    RestResponse<?> create(@RequestBody RecordDto dto) {
        partnerService.record(dto);
        return RestResponse.success(null);
    }

}
