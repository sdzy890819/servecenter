package com.fdz.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PaymentRecordSearchDto {

    private Long partnerId;

    private String paySn;

    private String orderSn;

    private Byte payStatus;

    private Byte paymentType;

    private List<Byte> paymentTypeList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payEndTime;
}
