package com.fdz.content.manager;

import com.fdz.content.domain.Product;
import com.fdz.content.domain.ProductImage;
import com.fdz.content.domain.ProductType;
import com.fdz.content.mapper.ProductImageMapper;
import com.fdz.content.mapper.ProductMapper;
import com.fdz.content.mapper.ProductTypeMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Repository
@Transactional
public class ProductManager {


    @Resource
    private ProductImageMapper productImageMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductTypeMapper productTypeMapper;

    public ProductImage selectProductImageByPrimaryKey(Long id) {
        return productImageMapper.selectByPrimaryKey(id);
    }

    public Product selectProductByPrimaryKey(Long id) {
        return productMapper.selectByPrimaryKey(id);
    }

    public ProductType selectProductTypeByPrimaryKey(Long id) {
        return productTypeMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(ProductImage productImage) {
        return productImageMapper.updateByPrimaryKeySelective(productImage);
    }

    public int updateByPrimaryKeySelective(Product product) {
        return productMapper.updateByPrimaryKeySelective(product);
    }

    public int updateByPrimaryKeySelective(ProductType productType) {
        return productTypeMapper.updateByPrimaryKeySelective(productType);
    }

    public int insertSelective(ProductImage record) {
        return productImageMapper.insertSelective(record);
    }

    public int insertSelective(Product record) {
        return productMapper.insertSelective(record);
    }

    public int insertSelective(ProductType record) {
        return productTypeMapper.insertSelective(record);
    }

}
