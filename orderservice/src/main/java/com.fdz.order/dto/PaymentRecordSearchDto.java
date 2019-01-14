package com.fdz.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PaymentRecordSearchDto {

    private String partnerId;

    private String paySn;

    private String orderSn;

    private Byte payStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payEndTime;
}
