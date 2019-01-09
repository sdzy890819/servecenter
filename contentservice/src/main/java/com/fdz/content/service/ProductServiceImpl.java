package com.fdz.content.service;

import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.content.domain.PartnerProduct;
import com.fdz.content.domain.Product;
import com.fdz.content.domain.ProductImage;
import com.fdz.content.domain.ProductType;
import com.fdz.content.dto.ThirdpartProductDto;
import com.fdz.content.dto.ThirdpartyProductDto;
import com.fdz.content.manager.PartnerManager;
import com.fdz.content.manager.ProductManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductManager productManager;

    @Resource
    private PartnerManager partnerManager;

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

    @Override
    public List<ThirdpartProductDto> list(Page page) {
        Integer count = partnerManager.countPartnerProduct();
        page.setCount(count);
        if (page.isQuery()) {
            List<PartnerProduct> list = partnerManager.listPartnerProduct(page);
            return list(list);
        }
        return null;
    }

    @Override
    public ThirdpartyProductDto detail(Long partnerProductId) {
        PartnerProduct partnerProduct = partnerManager.selectPartnerProductByPrimaryKey(partnerProductId);
        Product product = productManager.selectProductByPrimaryKey(partnerProduct.getProductId());
        ProductType productType = productManager.findProductTypeBySn(product.getProductTypeNo());
        List<Long> productIds = new ArrayList<>();
        productIds.add(product.getId());
        Map<Long, List<String>> productImages = findImagesByIdsResultMap(productIds);
        ThirdpartyProductDto thirdpartProductDto = new ThirdpartyProductDto();
        thirdpartProductDto.setPlatformPrice(partnerProduct.getPlatformPrice());
        thirdpartProductDto.setPrimeCosts(product.getPrimeCosts());
        thirdpartProductDto.setProductCoverImage(product.getProductCoverImage());
        thirdpartProductDto.setProductDescription(product.getProductDescription());
        thirdpartProductDto.setProductModel(product.getProductModel());
        thirdpartProductDto.setPartnerId(partnerProduct.getPartnerId());
        thirdpartProductDto.setProductId(product.getId());
        thirdpartProductDto.setProductName(product.getProductName());
        thirdpartProductDto.setProductTypeNo(product.getProductTypeNo());
        thirdpartProductDto.setProductTypeName(productType.getProductTypeName());
        thirdpartProductDto.setSalePrice(partnerProduct.getSalePrice());
        thirdpartProductDto.setShelf(partnerProduct.getShelf() && product.getStatus());
        thirdpartProductDto.setProductImages(productImages != null ? productImages.get(product.getId()) : null);
        thirdpartProductDto.setId(partnerProductId);
        thirdpartProductDto.setProductSalePrice(product.getSalePrice());
        return thirdpartProductDto;
    }

    @Override
    public List<ThirdpartyProductDto> findThirdpartProductDtoByIds(List<Long> partnerProductIds) {
        List<PartnerProduct> list = partnerManager.findPPByProductIds(partnerProductIds);
        if (StringUtils.isNotEmpty(list)) {
            return listInner(list);
        }
        return null;
    }

    @Override
    public List<ThirdpartyProductDto> list(PartnerProduct partnerProduct, Page page) {
        Integer count = partnerManager.countSearchPartnerProduct(partnerProduct);
        page.setCount(count);
        if (page.isQuery()) {
            List<PartnerProduct> list = partnerManager.searchPartnerProduct(partnerProduct, page);
            return listInner(list);
        }
        return null;
    }

    private List<ThirdpartProductDto> list(List<PartnerProduct> list) {
        List<Long> productIds = new ArrayList<>();
        for (PartnerProduct partnerProduct : list) {
            productIds.add(partnerProduct.getProductId());
        }
        Map<Long, Product> productMap = findProductByIdsResultMap(productIds);
        List<String> snlist = new ArrayList<>();
        productMap.forEach((k, v) -> snlist.add(v.getProductTypeNo()));
        Map<Long, List<String>> productImagesMap = findImagesByIdsResultMap(productIds);
        Map<Long, ProductType> productTypeMap = findTypeBySnResultMap(snlist);
        List<ThirdpartProductDto> result = new ArrayList<>();
        list.stream().forEach(a -> {
            Product product = productMap.get(a.getId());
            List<String> productImages = productImagesMap.get(a.getId());
            ProductType productType = productTypeMap.get(product.getProductTypeNo());
            ThirdpartProductDto thirdpartProductDto = new ThirdpartProductDto();
            thirdpartProductDto.setPlatformPrice(a.getPlatformPrice());
            thirdpartProductDto.setProductCoverImage(product.getProductCoverImage());
            thirdpartProductDto.setProductDescription(product.getProductDescription());
            thirdpartProductDto.setProductModel(product.getProductModel());
            thirdpartProductDto.setProductName(product.getProductName());
            thirdpartProductDto.setProductTypeNo(product.getProductTypeNo());
            thirdpartProductDto.setProductTypeName(productType.getProductTypeName());
            thirdpartProductDto.setSalePrice(a.getSalePrice());
            thirdpartProductDto.setShelf(a.getShelf() && product.getStatus());
            thirdpartProductDto.setProductImages(productImages);
            thirdpartProductDto.setId(a.getId());
            result.add(thirdpartProductDto);
        });
        return result;
    }

    private List<ThirdpartyProductDto> listInner(List<PartnerProduct> list) {
        List<Long> productIds = new ArrayList<>();
        for (PartnerProduct partnerProduct : list) {
            productIds.add(partnerProduct.getProductId());
        }
        Map<Long, Product> productMap = findProductByIdsResultMap(productIds);
        List<String> snlist = new ArrayList<>();
        productMap.forEach((k, v) -> snlist.add(v.getProductTypeNo()));
        Map<Long, List<String>> productImagesMap = findImagesByIdsResultMap(productIds);
        Map<Long, ProductType> productTypeMap = findTypeBySnResultMap(snlist);
        List<ThirdpartyProductDto> result = new ArrayList<>();
        list.stream().forEach(a -> {
            Product product = productMap.get(a.getId());
            List<String> productImages = productImagesMap.get(a.getId());
            ProductType productType = productTypeMap.get(product.getProductTypeNo());
            ThirdpartyProductDto thirdpartProductDto = new ThirdpartyProductDto();
            thirdpartProductDto.setPlatformPrice(a.getPlatformPrice());
            thirdpartProductDto.setProductCoverImage(product.getProductCoverImage());
            thirdpartProductDto.setProductDescription(product.getProductDescription());
            thirdpartProductDto.setProductModel(product.getProductModel());
            thirdpartProductDto.setPartnerId(a.getPartnerId());
            thirdpartProductDto.setProductId(product.getId());
            thirdpartProductDto.setProductName(product.getProductName());
            thirdpartProductDto.setProductTypeNo(product.getProductTypeNo());
            thirdpartProductDto.setProductTypeName(productType.getProductTypeName());
            thirdpartProductDto.setSalePrice(a.getSalePrice());
            thirdpartProductDto.setShelf(a.getShelf() && product.getStatus());
            thirdpartProductDto.setProductImages(productImages);
            thirdpartProductDto.setId(a.getId());
            thirdpartProductDto.setProductSalePrice(product.getSalePrice());
            result.add(thirdpartProductDto);
        });
        return result;
    }


    public List<ProductType> findTypeByIds(List<Long> ids) {
        return productManager.findTypeByIds(ids);
    }

    public List<ProductType> findTypeBySn(List<String> snlist) {
        return productManager.findTypeBySn(snlist);
    }

    public Map<Long, ProductType> findTypeBySnResultMap(List<String> snlist) {
        List<ProductType> list = productManager.findTypeBySn(snlist);
        Map<Long, ProductType> result = new HashMap<>();
        if (StringUtils.isNotEmpty(list)) {
            list.stream().forEach(a -> result.put(a.getId(), a));
        }
        return result;
    }

    /**
     * 获取产品列表根据ID列表
     *
     * @param productIds
     * @return
     */
    public List<Product> findProductByIds(List<Long> productIds) {
        List<Product> products = productManager.findProductByIds(productIds);
        return products;
    }

    /**
     * 获取产品Map根据ID列表
     *
     * @param productIds
     * @return
     */
    public Map<Long, Product> findProductByIdsResultMap(List<Long> productIds) {
        List<Product> list = findProductByIds(productIds);
        Map<Long, Product> result = new HashMap<>();
        if (StringUtils.isNotEmpty(list)) {
            list.stream().forEach(a -> result.put(a.getId(), a));
        }
        return result;
    }

    public List<ProductImage> findImagesByIds(List<Long> productIds) {
        return productManager.findImagesByProductIds(productIds);
    }

    /**
     * @param productIds
     * @return
     */
    public Map<Long, List<String>> findImagesByIdsResultMap(List<Long> productIds) {
        List<ProductImage> list = findImagesByIds(productIds);
        Map<Long, List<String>> result = new HashMap<>();
        if (StringUtils.isNotEmpty(list)) {
            for (ProductImage productImage : list) {
                List<String> tmp = result.get(productImage.getProductId());
                if (StringUtils.isEmpty(tmp)) {
                    tmp = new ArrayList<>();
                    result.put(productImage.getProductId(), tmp);
                }
                tmp.add(productImage.getProductImage());
            }
        }
        return result;
    }

    @Override
    public List<Product> listProduct(Page page) {
        Integer count = productManager.countProduct();
        page.setCount(count);
        if (page.isQuery()) {
            return productManager.listProduct(page);
        }
        return null;
    }

    @Override
    public List<Product> searchProduct(Product product, Page page) {
        Integer count = productManager.countSearchProduct(product);
        page.setCount(count);
        if (page.isQuery()) {
            return productManager.searchProductList(product, page);
        }
        return null;
    }

    @Override
    public int insertSelective(Product product, List<String> images) {
        return productManager.insertSelective(product, images);
    }

    @Override
    public int updateSelective(Product product, List<String> images) {
        return productManager.updateSelective(product, images);
    }
}
