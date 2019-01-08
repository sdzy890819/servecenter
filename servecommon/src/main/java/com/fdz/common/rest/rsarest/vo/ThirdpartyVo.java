package com.fdz.common.rest.rsarest.vo;

import com.fdz.common.constant.Constants;
import lombok.Data;

@Data
public class ThirdpartyVo {

    public static final String CHANNEL_TYPE = "01";

    private String channel;

    private String version = Constants.Common.THIRDPARTY_VERSION;

    private String data;

    private String sign;

    private String channelType = CHANNEL_TYPE;

}
