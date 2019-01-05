package com.fdz.content.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Partner extends BaseEntity {

    private String name;

    private String shortName;

    private Byte nature;

    private String code;

    private String contacts;

    private String contactMobile;

    private BigDecimal serviceRate;

    private String publicKey;

    public Partner() {
        super();
    }

    public Partner(Long id) {
        super(id);
    }
}