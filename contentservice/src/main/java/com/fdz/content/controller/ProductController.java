package com.fdz.content.controller;

import com.fdz.common.dto.SearchResult;
import com.fdz.common.exception.BizException;
import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.content.convert.DtoConvert;
import com.fdz.content.domain.Product;
import com.fdz.content.domain.ProductImage;
import com.fdz.content.domain.ProductType;
import com.fdz.content.dto.PageDataResult;
import com.fdz.content.dto.ProductDto;
import com.fdz.content.dto.SearchProductDto;
import com.fdz.content.service.PartnerService;
import com.fdz.content.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@ApiVersion("1")
@RequestMapping("/content/product")
@Api("产品信息")
public class ProductController {

    @Resource
    private DtoConvert dtoConvert;

    @Resource
    private ProductService productService;

    @Resource
    private PartnerService partnerService;

    @ApiOperation("商品分类列表")
    @GetMapping("/type/all")
    RestResponse<List<ProductType>> allType() {
        List<ProductType> list = productService.findAllTypes();
        return RestResponse.success(list);
    }


    @ApiOperation("产品列表")
    @GetMapping("/list")
    RestResponse<PageDataResult<List<Product>>> list(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                     @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        Page pageObj = new Page(page, pageSize);
        List<Product> list = productService.listProduct(pageObj);
        PageDataResult<List<Product>> data = new PageDataResult<>();
        data.setPage(pageObj);
        data.setData(list);
        return RestResponse.success(data);
    }

    @ApiOperation("产品详情")
    @GetMapping("/detail/{id}")
    RestResponse<Product> detail(@PathVariable("id") Long id) {
        Product product = productService.selectProductByPrimaryKey(id);
        if (product != null) {
            List<ProductImage> list = productService.findProductImages(id);
            product.setProductImages(list);
        }
        return RestResponse.success(product);
    }

    @ApiOperation("搜索产品")
    @PostMapping("/search")
    RestResponse<SearchResult<List<Product>>> search(@RequestBody SearchProductDto dto,
                                                     @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                     @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        Page pageObj = new Page(page, pageSize);
        Product searchProduct = dtoConvert.convert(dto);
        List<Product> list = productService.searchProduct(searchProduct, pageObj);
        if (StringUtils.isNotEmpty(list)) {
            List<String> ids = new ArrayList<>();
            list.forEach(a -> ids.add(a.getProductTypeNo()));
            Map<String, String> typeMap = productService.findProductTypeResultMapBySn(ids);
            list.forEach(a -> a.setProductTypeName(typeMap.get(a.getProductTypeNo())));
        }
        SearchResult<List<Product>> data = new SearchResult<>();
        data.setPage(pageObj);
        data.setData(list);
        data.setSearchCondition(dto);
        return RestResponse.success(data);
    }

    @ApiOperation("禁用启用产品")
    @GetMapping("/{id}/status/{status}")
    RestResponse use(@PathVariable("id") Long id, @PathVariable("status") Boolean status) {
        if (!status) {
            Integer count = partnerService.countPartnerProductByProductId(id);
            if (count != null && count > 0) {
                throw new BizException("还有合作伙伴配置当前产品，不可禁用");
            }
        }
        Product product = new Product(id);
        product.setStatus(status);
        productService.updateByPrimaryKeySelective(product);
        return RestResponse.success(null);
    }

    @ApiOperation("删除产品")
    @GetMapping("/delete/{id}")
    RestResponse delete(@PathVariable("id") Long id) {
        Integer count = partnerService.countPartnerProductByProductId(id);
        if (count != null && count > 0) {
            throw new BizException("还有合作伙伴配置当前产品，不可禁用");
        }
        Product product = new Product(id);
        product.setDelete(true);
        productService.updateByPrimaryKeySelective(product);
        return RestResponse.success(null);
    }


    @ApiOperation("创建产品")
    @PostMapping("/create")
    RestResponse create(@RequestBody ProductDto dto) {
        Product product = dtoConvert.convert(dto);
        productService.insertSelective(product, dto.getProductImages());
        return RestResponse.success(null);
    }

    @ApiOperation("修改产品")
    @PostMapping("/update")
    RestResponse update(@RequestBody ProductDto dto) {
        Product product = dtoConvert.convert(dto);
        productService.updateByPrimaryKeySelective(product);
        partnerService.syncProduct(product.getId());
        return RestResponse.success(null);
    }

}
