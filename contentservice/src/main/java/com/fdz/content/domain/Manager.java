package com.fdz.content.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

@Data
public class Manager extends BaseEntity {
    private String userName;

    private String password;

    private String realName;

    public Manager() {
        super();
    }

    public Manager(Long id) {
        super(id);
    }

}