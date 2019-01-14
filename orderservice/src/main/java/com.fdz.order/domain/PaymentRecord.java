package com.fdz.order.domain;

import com.fdz.common.domain.BaseEntity;
import com.fdz.common.enums.PayStatusEnums;
import com.fdz.common.enums.PaymentTypeEnums;
import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class PaymentRecord extends BaseEntity {

    private String sn;

    private String orderSn;

    private Byte paymentType;

    private BigDecimal amount;

    private BigDecimal surplusAmount;

    private Date payTime;

    private Byte payStatus;

    private String paySn;

    private Byte payRoute;

    private Long partnerId;

    private Boolean frozen;

    public PaymentRecord() {
        super();
    }

    public PaymentRecord(Long id) {
        super(id);
    }

    private String partnerName;

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