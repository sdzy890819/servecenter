package com.fdz.content.controller;


import com.fdz.common.dto.PageDto;
import com.fdz.common.utils.Page;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.content.convert.DtoConvert;
import com.fdz.content.dto.PageDataResult;
import com.fdz.content.dto.ThirdpartProductDto;
import com.fdz.content.service.PartnerService;
import com.fdz.content.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@ApiVersion("1")
@RequestMapping("/content/thirdparty/product")
@Api("提供给第三方接口")
public class ThirdpartyController {

    @Resource
    private DtoConvert dtoConvert;

    @Resource
    private ProductService productService;

    @Resource
    private PartnerService partnerService;

    @ApiOperation("产品列表")
    @PostMapping("/list")
    RestResponse<PageDataResult<List<ThirdpartProductDto>>> list(@RequestBody PageDto pageDto) {
        Page page = dtoConvert.convert(pageDto);
        List<ThirdpartProductDto> list = productService.list(page);
        PageDataResult<List<ThirdpartProductDto>> data = new PageDataResult<>();
        data.setPage(page);
        data.setData(list);
        return RestResponse.success(data);
    }


}
