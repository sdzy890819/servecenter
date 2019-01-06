package com.fdz.thirdpartygateway.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ThirdpartyRequest {

    public static final String CHANNEL_TYPE = "01";

    @NotNull(message = "渠道不可以为空")
    private String channel;

    @NotNull(message = "版本不可以为空")
    private String version;

    @NotNull(message = "方法不可以为空")
    private String method;

    @NotNull(message = "数据不可以为空")
    private String data;

    @NotNull(message = "验签不可以为空")
    private String sign;

    @NotNull(message = "渠道类型不可以为空")
    private String channelType;

}
