package com.fdz.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class CashierResult {

    private String sn;

    private String orderSn;

    private List<GoodsDto> goodsDtos;
}
