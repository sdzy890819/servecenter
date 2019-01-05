package com.fdz.content.service;

import com.fdz.content.domain.Product;
import com.fdz.content.domain.ProductImage;
import com.fdz.content.domain.ProductType;
import com.fdz.content.manager.ProductManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductManager productManager;

    public ProductImage selectProductImageByPrimaryKey(Long id) {
        return productManager.selectProductImageByPrimaryKey(id);
    }

    public Product selectProductByPrimaryKey(Long id) {
        return productManager.selectProductByPrimaryKey(id);
    }

    public ProductType selectProductTypeByPrimaryKey(Long id) {
        return productManager.selectProductTypeByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(ProductImage productImage) {
        return productManager.updateByPrimaryKeySelective(productImage);
    }

    public int updateByPrimaryKeySelective(Product product) {
        return productManager.updateByPrimaryKeySelective(product);
    }

    public int updateByPrimaryKeySelective(ProductType productType) {
        return productManager.updateByPrimaryKeySelective(productType);
    }

    public int insertSelective(ProductImage record) {
        return productManager.insertSelective(record);
    }

    public int insertSelective(Product record) {
        return productManager.insertSelective(record);
    }

    public int insertSelective(ProductType record) {
        return productManager.insertSelective(record);
    }
}
