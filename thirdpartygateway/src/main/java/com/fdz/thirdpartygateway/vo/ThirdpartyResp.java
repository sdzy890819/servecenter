package com.fdz.thirdpartygateway.vo;

import com.fdz.common.constant.Constants;
import com.fdz.common.rest.rsarest.vo.ThirdpartyResponse;
import lombok.Data;

@Data
public class ThirdpartyResp {

    public static final String CHANNEL_TYPE = "01";

    private String channel;

    private String version = Constants.Common.THIRDPARTY_VERSION;

    private String respData;

    private String respSign;

    private String channelType = CHANNEL_TYPE;

    private int code = ThirdpartyResponse.SUCCESS_CODE;

    private String message = "成功";


}
