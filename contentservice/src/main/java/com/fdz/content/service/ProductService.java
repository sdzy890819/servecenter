package com.fdz.content.service;

import com.fdz.content.domain.Product;
import com.fdz.content.domain.ProductImage;
import com.fdz.content.domain.ProductType;

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

}
