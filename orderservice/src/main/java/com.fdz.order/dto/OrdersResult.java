package com.fdz.order.dto;

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


    //----
    private String buyTimeStr;

    private String businessDeliveryTimeStr;

    private String statusStr;

    public String getStatusStr() {
        if (status != null) {
            OrdersStatus ordersStatus = OrdersStatus.get(status);
            return ordersStatus.getStatusText();
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
