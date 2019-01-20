package com.fdz.content.controller;


import com.fdz.common.aspect.ann.NotTracked;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.content.oss.OssClientManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Random;

@RestController
@ApiVersion("1")
@RequestMapping("/content/upload")
@Api("产品信息")
public class UploadController {

    @Resource
    private OssClientManager ossClientManager;


    @ApiOperation("上传信息")
    @NotTracked
    @PostMapping(value = "/image")
    RestResponse upload(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        String[] fileName = file.getOriginalFilename().split(".");
        String suffix = fileName[fileName.length - 1].toLowerCase();
        if ("jpeg".equals(suffix)) {
            suffix = "jpg";
        }
        Random random = new Random();
        String name = System.currentTimeMillis() + random.nextInt(10000) + "." + suffix;
        String pathName = ossClientManager.upload(name, file.getInputStream());
        return RestResponse.success(pathName);
    }
}
