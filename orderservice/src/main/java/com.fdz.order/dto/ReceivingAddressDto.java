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

    @NotNull(message = "省份不可以为空, 省")
    private String province;

    @NotNull(message = "城市不可以为空，市")
    private String city;

    @NotNull(message = "区域不可以为空, 区")
    private String area;

    @NotNull(message = "详细收货地址不可以为空")
    private String address;

    @NotNull(message = "收货人手机号不可以为空")
    private String mobile;
}
