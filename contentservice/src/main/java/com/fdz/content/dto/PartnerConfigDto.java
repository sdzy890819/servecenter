package com.fdz.content.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PartnerConfigDto {

    @NotNull(message = "公司信息不可为空")
    private Long partnerId;

    private String interfaceUrl;

    private Byte interfaceType;

}
