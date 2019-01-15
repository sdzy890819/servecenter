package com.fdz.order.dto;

import lombok.Data;

import javax.validation.constraints.Min;
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
    @Min(1)
    private Integer num;

    /**
     * 售卖价格
     */
    private BigDecimal platformPrice;

}
