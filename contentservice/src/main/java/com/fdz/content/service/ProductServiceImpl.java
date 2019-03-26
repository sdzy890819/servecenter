package com.fdz.content.service;

import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.content.domain.*;
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

    @Resource
    private PartnerService partnerService;

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
    public List<ThirdpartProductDto> list(Long partnerId, Page page) {
        Integer count = partnerManager.countPartnerProduct(partnerId);
        page.setCount(count);
        if (page.isQuery()) {
            List<PartnerProduct> list = partnerManager.listPartnerProduct(partnerId, page);
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
        thirdpartProductDto.setServiceFee(partnerProduct.getServiceFee());
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
        Map<String, ProductType> productTypeMap = findTypeBySnResultMap(snlist);
        List<ThirdpartProductDto> result = new ArrayList<>();
        list.stream().forEach(a -> {
            Product product = productMap.get(a.getProductId());
            if (product != null) {
                List<String> productImages = productImagesMap.get(a.getProductId());
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
                thirdpartProductDto.setServiceFee(a.getServiceFee());
                result.add(thirdpartProductDto);
            }
        });
        return result;
    }

    private List<ThirdpartyProductDto> listInner(List<PartnerProduct> list) {
        List<Long> productIds = new ArrayList<>();
        List<Long> partnerIds = new ArrayList<>();
        for (PartnerProduct partnerProduct : list) {
            productIds.add(partnerProduct.getProductId());
            partnerIds.add(partnerProduct.getPartnerId());
        }
        Map<Long, Product> productMap = findProductByIdsResultMap(productIds);
        List<String> snlist = new ArrayList<>();
        productMap.forEach((k, v) -> snlist.add(v.getProductTypeNo()));
        Map<Long, List<String>> productImagesMap = findImagesByIdsResultMap(productIds);
        Map<String, ProductType> productTypeMap = findTypeBySnResultMap(snlist);
        Map<Long, Partner> partnerMap = partnerService.findPartnerByIdResultMap(partnerIds);
        List<ThirdpartyProductDto> result = new ArrayList<>();
        list.stream().forEach(a -> {
            Product product = productMap.get(a.getProductId());
            if (product != null) {
                List<String> productImages = productImagesMap.get(a.getProductId());
                ProductType productType = productTypeMap.get(product.getProductTypeNo());
                if (productType == null) {
                    productType = new ProductType();
                }
                ThirdpartyProductDto thirdpartProductDto = new ThirdpartyProductDto();
                thirdpartProductDto.setPlatformPrice(a.getPlatformPrice());
                thirdpartProductDto.setProductCoverImage(product.getProductCoverImage());
                thirdpartProductDto.setProductDescription(product.getProductDescription());
                thirdpartProductDto.setProductModel(product.getProductModel());
                thirdpartProductDto.setPartnerId(a.getPartnerId());
                thirdpartProductDto.setPartnerName(partnerMap.get(a.getPartnerId()) != null ? partnerMap.get(a.getPartnerId()).getName() : "");
                thirdpartProductDto.setProductId(product.getId());
                thirdpartProductDto.setProductName(product.getProductName());
                thirdpartProductDto.setProductTypeNo(product.getProductTypeNo());
                thirdpartProductDto.setProductTypeName(productType.getProductTypeName());
                thirdpartProductDto.setSalePrice(a.getSalePrice());
                thirdpartProductDto.setShelf(a.getShelf() && product.getStatus());
                thirdpartProductDto.setProductImages(productImages);
                thirdpartProductDto.setId(a.getId());
                thirdpartProductDto.setProductSalePrice(product.getSalePrice());
                thirdpartProductDto.setServiceFee(a.getServiceFee());
                thirdpartProductDto.setPrimeCosts(product.getPrimeCosts());
                result.add(thirdpartProductDto);
            }
        });
        return result;
    }


    public List<ProductType> findTypeByIds(List<Long> ids) {
        return productManager.findTypeByIds(ids);
    }

    public List<ProductType> findTypeBySn(List<String> snlist) {
        return productManager.findTypeBySn(snlist);
    }

    public Map<String, ProductType> findTypeBySnResultMap(List<String> snlist) {
        List<ProductType> list = productManager.findTypeBySn(snlist);
        Map<String, ProductType> result = new HashMap<>();
        if (StringUtils.isNotEmpty(list)) {
            list.stream().forEach(a -> result.put(a.getSn(), a));
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

    @Override
    public Map<String, String> findProductTypeResultMapBySn(List<String> snlist) {
        List<ProductType> list = findTypeBySn(snlist);
        if (StringUtils.isNotEmpty(list)) {
            Map<String, String> map = new HashMap<>();
            list.forEach(a -> map.put(a.getSn(), a.getProductTypeName()));
            return map;
        }
        return null;
    }

    @Override
    public List<ProductType> findAllTypes() {
        List<ProductType> list = productManager.findAllTypes();
        return list;
    }

    @Override
    public List<ProductImage> findProductImages(Long productId) {
        return productManager.findProductImages(productId);
    }

    @Override
    public List<Product> findAll() {
        return productManager.findAll();
    }

    @Override
    public int queryTypeBySnAndNameCount(String typeName, String sn) {
        return productManager.queryTypeBySnAndNameCount(typeName, sn);
    }

    @Override
    public int queryProductByType(String productTypeNo) {
        return productManager.queryProductByType(productTypeNo);
    }
}
