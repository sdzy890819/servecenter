package com.fdz.content.domain;

import com.fdz.common.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class InterfaceExecRecord extends BaseEntity {

    private Long partnerId;

    private String interfaceUrl;

    private Byte interfaceType;

    private Byte status;

    private Date execTime;

    private String data;

    public InterfaceExecRecord() {
        super();
    }

    public InterfaceExecRecord(Long id) {
        super(id);
    }

}