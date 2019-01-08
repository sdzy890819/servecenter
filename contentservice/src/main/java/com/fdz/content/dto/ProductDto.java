package com.fdz.content.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {

    private Long id;

    private String productName;

    private String productTypeNo;

    private String productDescription;

    private String productCoverImage;

    private BigDecimal primeCosts;

    private BigDecimal salePrice;

    private Boolean status;

    private String productModel;

    private List<String> productImages;

}
