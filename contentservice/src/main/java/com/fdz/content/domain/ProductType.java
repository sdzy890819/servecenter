package com.fdz.content.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

@Data
public class ProductType extends BaseEntity {

    private String productTypeName;

    private String sn;

    public ProductType() {
        super();
    }

    public ProductType(Long id) {
        super(id);
    }
}