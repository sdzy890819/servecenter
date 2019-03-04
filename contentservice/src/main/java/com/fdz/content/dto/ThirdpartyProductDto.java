package com.fdz.content.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ThirdpartyProductDto {

    private Long id;

    private BigDecimal salePrice;

    private BigDecimal platformPrice;

    private BigDecimal primeCosts;

    private Boolean shelf;

    private String productName;

    private String productTypeNo;

    private String productTypeName;

    private String productDescription;

    private String productCoverImage;

    private String productModel;

    private Long partnerId;

    private Long productId;

    private String partnerName;

    private BigDecimal productSalePrice;

    private BigDecimal serviceFee;

    private List<String> productImages;

}
