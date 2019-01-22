package com.fdz.order.dto;

import com.fdz.common.enums.DeliveryStatusEnums;
import com.fdz.common.enums.OrdersStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
public class OrdersResult {

    private String orderSn;

    private String partnerSn;

    private Long partnerId;

    private BigDecimal amount;

    private BigDecimal costAmount;

    private Date buyTime;

    private Date confirmTime;

    private Date businessDeliveryTime;

    private Date endTime;

    private Byte deliveryStatus;

    private Byte status;

    private Byte orderStatus;

    private Byte businessDeliveryStatus;

    private OrdersLogisticsResult ordersLogisticsResult;

    private List<OrdersProductResult> ordersProductResults;

    private BigDecimal platformAmount;

    private BigDecimal infoAmount;

    //----
    private String buyTimeStr;

    private String businessDeliveryTimeStr;

    private String statusStr;

    private String deliveryStatusStr;

    private String businessDeliveryStatusStr;

    private String confirmTimeStr;

    private String endTimeStr;

    public String getConfirmTimeStr() {
        if (confirmTime != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(confirmTime);
        }
        return "";
    }

    public String getEndTimeStr() {
        if (endTime != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(endTime);
        }
        return "";
    }

    public String getDeliveryStatusStr() {
        if (deliveryStatus != null) {
            return DeliveryStatusEnums.get(deliveryStatus).getStatusText();
        }
        return "";
    }

    public String getBusinessDeliveryStatusStr() {
        if (businessDeliveryStatus != null) {
            return DeliveryStatusEnums.get(businessDeliveryStatus).getStatusText();
        }
        return "";
    }

    public String getStatusStr() {
        if (status != null) {
            OrdersStatus ordersStatus = OrdersStatus.get(status);
            return ordersStatus.getText();
        }
        return "";
    }

    public String getBuyTimeStr() {
        if (buyTime != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(buyTime);
        }
        return "";
    }

    public String getBusinessDeliveryTimeStr() {
        if (businessDeliveryTime != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(businessDeliveryTime);
        }
        return "";
    }

}
