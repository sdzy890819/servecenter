package com.fdz.order.dto;

import com.fdz.common.enums.PayStatusEnums;
import com.fdz.common.enums.PaymentTypeEnums;
import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class RechargeRecordResult {

    private String paySn;

    private Date payTime;

    private Byte paymentType;

    private BigDecimal amount;

    private Byte payStatus;


    //--解释字段.
    private String payTimeStr;

    private String paymentTypeStr;

    private String payStatusStr;

    public String getPaymentTypeStr() {
        if (paymentType != null) {
            return PaymentTypeEnums.get(paymentType).getText();
        }
        return "";
    }

    public String getPayStatusStr() {
        if (payStatus != null) {
            return PayStatusEnums.get(payStatus).getText();
        }
        return "";
    }

    public String getPayTimeStr() {
        if (payTime != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(payTime);
        }
        return "";
    }
}
