package com.fdz.content.controller;


import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.content.service.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@ApiVersion("1")
@RequestMapping("/content/job")
@Api("Job执行接口")
public class JobController {

    @Resource
    private JobService jobService;


    @ApiOperation("自动执行同步接口")
    @GetMapping("/exec-record")
    RestResponse execRecord() {
        jobService.execRecord();
        return RestResponse.success(null);
    }


}
