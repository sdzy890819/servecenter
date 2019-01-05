package com.fdz.content.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

@Data
public class PartnerUser extends BaseEntity {

    private String userName;

    private String password;

    private String realName;

    private Long partnerId;

    public PartnerUser() {
        super();
    }

    public PartnerUser(Long id) {
        super(id);
    }
}