package com.fdz.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsDto {

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 售卖价格
     */
    private BigDecimal platformPrice;

}
