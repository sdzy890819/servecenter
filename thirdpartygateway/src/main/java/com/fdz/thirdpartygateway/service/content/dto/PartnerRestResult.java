package com.fdz.thirdpartygateway.service.content.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartnerRestResult {

    private Long id;

    private Long createTime;

    private Long modifyTime;

    private String createBy;

    private String modifyBy;

    private String remark;

    private Boolean delete;

    private String name;

    private String shortName;

    private Byte nature;

    private String code;

    private String contacts;

    private String contactMobile;

    private BigDecimal serviceRate;

    private String publicKey;

    private String uniqueKey;

}
