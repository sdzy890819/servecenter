package com.fdz.content.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartnerProductResult {

    private Long id;

    private Long productId;

    private BigDecimal salePrice;

    private BigDecimal platformPrice;

    private Boolean shelf;

    private String productName;

    private String productTypeNo;

    private String productDescription;

    private String productCoverImage;

    private Boolean status;

    private String productModel;

}
