package com.fdz.content.controller;


import com.fdz.common.utils.Page;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.content.convert.DtoConvert;
import com.fdz.content.domain.PartnerProduct;
import com.fdz.content.domain.Product;
import com.fdz.content.dto.PartnerProductDto;
import com.fdz.content.dto.SearchPartnerProductDto;
import com.fdz.common.dto.SearchResult;
import com.fdz.content.dto.ThirdpartyProductDto;
import com.fdz.content.service.PartnerService;
import com.fdz.content.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@ApiVersion("1")
@RequestMapping("/content/partner-product")
@Api("合作伙伴产品接口")
public class PartnerProductController {

    @Resource
    private DtoConvert dtoConvert;

    @Resource
    private PartnerService partnerService;

    @Resource
    private ProductService productService;

    @ApiOperation("列表")
    @PostMapping("/search")
    RestResponse<?> search(@RequestBody SearchPartnerProductDto dto,
                           @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        PartnerProduct partnerProduct = dtoConvert.convert(dto);
        Page pageObj = new Page(page, pageSize);
        List<ThirdpartyProductDto> list = productService.list(partnerProduct, pageObj);
        SearchResult<List<ThirdpartyProductDto>> data = new SearchResult<>();
        data.setPage(pageObj);
        data.setData(list);
        data.setSearchCondition(dto);
        return RestResponse.success(data);
    }

    @ApiOperation("创建产品")
    @PostMapping("/create")
    RestResponse create(@RequestBody PartnerProductDto dto) {
        PartnerProduct partnerProduct = dtoConvert.convert(dto);
        Product product = productService.selectProductByPrimaryKey(partnerProduct.getProductId());
        if (partnerProduct.getSalePrice() == null || partnerProduct.getSalePrice().floatValue() < 1) {
            partnerProduct.setSalePrice(product.getSalePrice());
        }
        partnerService.insertSelective(partnerProduct);
        return RestResponse.success(null);
    }

    @ApiOperation("修改产品")
    @PostMapping("/update")
    RestResponse update(@RequestBody PartnerProductDto dto) {
        PartnerProduct partnerProduct = dtoConvert.convert(dto);
        Product product = productService.selectProductByPrimaryKey(partnerProduct.getProductId());
        if (partnerProduct.getSalePrice() == null || partnerProduct.getSalePrice().floatValue() < 1) {
            partnerProduct.setSalePrice(product.getSalePrice());
        }
        partnerService.updateByPrimaryKeySelective(partnerProduct);
        return RestResponse.success(null);
    }

    @ApiOperation("下架产品")
    @GetMapping("/{id}/status/{shelf}")
    RestResponse use(@PathVariable("id") Long id, @PathVariable("shelf") Boolean shelf) {
        PartnerProduct partnerProduct = new PartnerProduct(id);
        partnerProduct.setShelf(shelf);
        partnerService.updateByPrimaryKeySelective(partnerProduct);
        return RestResponse.success(null);
    }

    @ApiOperation("详情信息")
    @GetMapping("/detail/{id}")
    RestResponse<ThirdpartyProductDto> detail(@PathVariable("id") Long id) {
        ThirdpartyProductDto thirdpartProductDto = productService.detail(id);
        return RestResponse.success(thirdpartProductDto);
    }

    @ApiOperation("详情列表")
    @PostMapping("/detail-list")
    RestResponse<List<ThirdpartyProductDto>> detailList(@RequestBody List<Long> ids) {
        List<ThirdpartyProductDto> list = productService.findThirdpartProductDtoByIds(ids);
        return RestResponse.success(list);
    }
}
