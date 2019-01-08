package com.fdz.content.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartnerProduct extends BaseEntity {

    private Long productId;

    private BigDecimal salePrice;

    private BigDecimal platformPrice;

    private Boolean shelf;

    private Long partnerId;

    public PartnerProduct() {
        super();
    }

    public PartnerProduct(Long id) {
        super(id);
    }
}