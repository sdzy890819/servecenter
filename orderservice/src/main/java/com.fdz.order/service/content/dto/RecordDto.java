package com.fdz.order.service.content.dto;

import lombok.Data;

@Data
public class RecordDto {
    private Long partnerId;

    private Byte interfaceType;

    private String data;
}
