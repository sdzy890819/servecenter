package com.fdz.order.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdersProduct extends BaseEntity {

    private String orderSn;

    private String partnerSn;

    private Long partnerId;

    private Long partnerProductId;

    private String productName;

    private Integer productNum;

    private String productTypeNo;

    private String productTypeName;

    private String productDescription;

    private String productCoverImage;

    private BigDecimal primeCosts;

    private BigDecimal salePrice;

    private String productModel;

    private Long productId;

    private BigDecimal partnerSalePrice;

    private BigDecimal partnerPlatformPrice;

    public OrdersProduct() {
        super();
    }

    public OrdersProduct(Long id) {
        super(id);
    }

}