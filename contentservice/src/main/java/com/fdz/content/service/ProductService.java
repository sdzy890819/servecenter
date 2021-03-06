package com.fdz.content.service;

import com.fdz.common.utils.Page;
import com.fdz.content.domain.PartnerProduct;
import com.fdz.content.domain.Product;
import com.fdz.content.domain.ProductImage;
import com.fdz.content.domain.ProductType;
import com.fdz.content.dto.ThirdpartProductDto;
import com.fdz.content.dto.ThirdpartyProductDto;

import java.util.List;
import java.util.Map;

public interface ProductService {

    ProductImage selectProductImageByPrimaryKey(Long id);

    Product selectProductByPrimaryKey(Long id);

    ProductType selectProductTypeByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProductImage productImage);

    int updateByPrimaryKeySelective(Product product);

    int updateByPrimaryKeySelective(ProductType productType);

    int insertSelective(ProductImage record);

    int insertSelective(Product record);

    int insertSelective(ProductType record);

    List<ThirdpartProductDto> list(Long partnerId, Page page);

    ThirdpartyProductDto detail(Long partnerProductId);

    List<ThirdpartyProductDto> findThirdpartProductDtoByIds(List<Long> partnerProductIds);

    List<Product> listProduct(Page page);

    List<Product> searchProduct(Product product, Page page);

    int insertSelective(Product product, List<String> images);

    int updateSelective(Product product, List<String> images);

    List<ThirdpartyProductDto> list(PartnerProduct partnerProduct, Page page);

    Map<String, String> findProductTypeResultMapBySn(List<String> snlist);

    List<ProductType> findAllTypes();

    List<ProductImage> findProductImages(Long productId);

    List<Product> findAll();

    int queryTypeBySnAndNameCount(String typeName, String sn);

    int queryProductByType(String productTypeNo);
}
