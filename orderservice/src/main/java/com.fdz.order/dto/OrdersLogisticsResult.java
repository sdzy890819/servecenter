package com.fdz.order.dto;

import com.fdz.common.enums.DeliveryStatusEnums;
import lombok.Data;

@Data
public class OrdersLogisticsResult {

    private String receiver;

    private String receiverProvince;

    private String receiverCity;

    private String receiverArea;

    private String receiverAddress;

    private String receiverMobile;

    private String logistics;

    private String logisticsSn;

    private String logisticsStatus;

    private Byte deliveryStatus;

    private Byte businessDeliveryStatus;

    private String deliveryStatusStr;

    private String businessDeliveryStatusStr;

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
}
