package com.fdz.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SearchOrdersDto {

    private String orderSn;

    private String partnerSn;

    private String receiverMobile;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date buyStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date buyEndTime;

    private Byte deliveryStatus;
}
