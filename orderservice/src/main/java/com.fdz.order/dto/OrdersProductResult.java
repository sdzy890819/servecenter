package com.fdz.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdersProductResult {

    private Long partnerProductId;

    private String productName;

    private Integer productNum;

    private String productTypeNo;

    private String productTypeName;

    private String productDescription;

    private String productCoverImage;

    private BigDecimal primeCosts;

    private BigDecimal salePrice;

    private BigDecimal platformPrice;

    private String productModel;

    private Long productId;

    private BigDecimal productSalePrice;

}
