package com.fdz.content.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product extends BaseEntity {

    private String productName;

    private String productTypeNo;

    private String productDescription;

    private String productCoverImage;

    private BigDecimal primeCosts;

    private BigDecimal salePrice;

    private Boolean status;

    private String productModel;

    public Product() {
        super();
    }

    public Product(Long id) {
        super(id);
    }
}