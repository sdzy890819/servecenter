package com.fdz.content.manager;

import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.content.domain.Product;
import com.fdz.content.domain.ProductImage;
import com.fdz.content.domain.ProductType;
import com.fdz.content.mapper.ProductImageMapper;
import com.fdz.content.mapper.ProductMapper;
import com.fdz.content.mapper.ProductTypeMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class ProductManager {


    @Resource
    private ProductImageMapper productImageMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductTypeMapper productTypeMapper;

    @Resource
    private PartnerManager partnerManager;

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

    public List<Product> findProductByIds(List<Long> ids) {
        return productMapper.findProductByIds(ids);
    }

    public List<ProductImage> findImagesByProductIds(List<Long> productIds) {
        return productImageMapper.findByIds(productIds);
    }

    public List<ProductType> findTypeByIds(List<Long> ids) {
        return productTypeMapper.findByIds(ids);
    }

    public List<ProductType> findTypeBySn(List<String> snlist) {
        return productTypeMapper.findTypeBySn(snlist);
    }


    public int countProduct() {
        return productMapper.countProduct();
    }

    public int countSearchProduct(Product product) {
        return productMapper.countSearchProduct(product);
    }

    public List<Product> searchProductList(Product product, Page page) {
        return productMapper.searchProductList(product, page);
    }

    public List<Product> listProduct(Page page) {
        return productMapper.listProduct(page);
    }

    public int insertSelective(Product product, List<String> images) {
        int p = insertSelective(product);
        insertImagesList(product.getId(), images);
        return p;
    }

    public int updateSelective(Product product, List<String> images) {
        int p = updateByPrimaryKeySelective(product);
        productImageMapper.deleteByProductId(product.getId());
        insertImagesList(product.getId(), images);
        return p;
    }

    public int insertImagesList(Long id, List<String> images) {
        if (StringUtils.isNotEmpty(images)) {
            List<ProductImage> list = new ArrayList<>();
            images.forEach(a -> {
                if (StringUtils.isNotNull(a)) {
                    ProductImage productImage = new ProductImage();
                    productImage.setProductId(id);
                    productImage.setProductImage(a);
                    list.add(productImage);
                }
            });
            return productImageMapper.insertList(list);
        }
        return 0;
    }

    public ProductType findProductTypeBySn(String sn) {
        return productTypeMapper.findProductTypeBySn(sn);
    }

    public List<ProductType> findAllTypes() {
        return productTypeMapper.findAllTypes();
    }

    public List<ProductImage> findProductImages(Long productId) {
        return productImageMapper.findProductImages(productId);
    }

    public List<Product> findAll() {
        return productMapper.findAll();
    }

    public int queryTypeBySnAndNameCount(String typeName, String sn) {
        return productTypeMapper.queryTypeBySnAndNameCount(typeName, sn);
    }

    public int queryProductByType(String productTypeNo) {
        return productMapper.queryProductByType(productTypeNo);
    }

}
