package com.fdz.content.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartnerProductDto {

    private Long id;
    
    private Long productId;

    private BigDecimal salePrice;

    private BigDecimal platformPrice;

    private Boolean shelf;

    private Long partnerId;
}
