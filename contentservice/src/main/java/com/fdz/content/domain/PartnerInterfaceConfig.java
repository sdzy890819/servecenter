package com.fdz.content.domain;

import com.fdz.common.domain.BaseEntity;
import com.fdz.common.enums.InterfaceTypeEnums;
import lombok.Data;

@Data
public class PartnerInterfaceConfig extends BaseEntity {

    private Long partnerId;

    private String interfaceUrl;

    private Byte interfaceType;


    public PartnerInterfaceConfig() {
        super();
    }

    public PartnerInterfaceConfig(Long id) {
        super(id);
    }

    private String interfaceTypeStr;

    public String getInterfaceTypeStr() {
        if (interfaceType != null) {
            return InterfaceTypeEnums.get(interfaceType).getText();
        }
        return "";
    }

    private String partnerName;
}