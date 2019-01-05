package com.fdz.content.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

@Data
public class ProductImage extends BaseEntity {

    private String productImage;

    private Long productId;

    public ProductImage() {
        super();
    }

    public ProductImage(Long id) {
        super(id);
    }
}