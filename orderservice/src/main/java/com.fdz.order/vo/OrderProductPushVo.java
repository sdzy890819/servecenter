package com.fdz.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderProductPushVo {

    private Long id;

    private String productName;

    private Integer productNum;

    private String productTypeName;

    private String productTypeNo;

    private String productDescription;

    private String productCoverImage;

    private BigDecimal salePrice;

    private BigDecimal platformPrice;

    private String productModel;
}
