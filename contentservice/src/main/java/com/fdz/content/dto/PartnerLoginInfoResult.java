package com.fdz.content.dto;

import lombok.Data;

@Data
public class PartnerLoginInfoResult {

    private String realName;

    private Long parentId;

    private String parentName;

    private String parentShortName;
}
