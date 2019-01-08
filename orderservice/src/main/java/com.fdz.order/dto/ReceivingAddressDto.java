package com.fdz.order.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ReceivingAddressDto {

    /**
     * 收货人
     */
    @NotNull(message = "收货人不可以为空")
    private String name;

    /**
     * 地址
     */
    @NotNull(message = "收货地址不可以为空")
    private String address;

    /**
     * 手机号
     */
    @NotNull(message = "收货人手机号不可以为空")
    private String mobile;
}
