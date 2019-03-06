package com.fdz.content.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartnerDto {

    private Long id;

    private String name;

    private String shortName;

    private Byte nature;

    private String code;

    private String contacts;

    private String contactMobile;

    private BigDecimal serviceRate;

    private String publicKey;

    private String uniqueKey;

    private String myKey;

    private String myPublicKey;

}
