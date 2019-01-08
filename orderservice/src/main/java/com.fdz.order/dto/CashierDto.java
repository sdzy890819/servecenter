package com.fdz.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class CashierDto {

    /**
     * 订单号
     */
    private String sn;

    private List<GoodsDto> goodsList;

    private ReceivingAddressDto receivingAddress;

}
