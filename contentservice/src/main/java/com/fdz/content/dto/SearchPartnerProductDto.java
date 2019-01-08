package com.fdz.content.dto;

import lombok.Data;

@Data
public class SearchPartnerProductDto {

    private Long partnerId;

    private Long productId;

    private Boolean shelf;
}
