package com.fdz.thirdpartygateway.vo;

import com.fdz.common.constant.Constants;
import lombok.Data;

@Data
public class ThirdpartyResp {

    public static final String CHANNEL_TYPE = "01";

    private String channel;

    private String version = Constants.Common.THIRDPARTY_VERSION;

    private String respData;

    private String respSign;

    private String channelType = CHANNEL_TYPE;



}
