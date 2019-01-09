package com.fdz.content.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ThirdpartProductDto {

    private Long id;

    private BigDecimal salePrice;

    private BigDecimal platformPrice;

    private Boolean shelf;

    private String productName;

    private String productTypeNo;

    private String productTypeName;

    private String productDescription;

    private String productCoverImage;

    private String productModel;

    private List<String> productImages;

}
