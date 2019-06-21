package com.fdz.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fdz.common.utils.StringUtils;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LogisticsEnum {

    EMS("邮政包裹");

    private String logistics;

    LogisticsEnum(String logistics) {
        this.logistics = logistics;
    }

    /**
     * 识别并获取到信息
     *
     * @param logistics
     * @return
     */
    public static LogisticsEnum get(String logistics) {
        if (StringUtils.isNotBlank(logistics)) {
            for (LogisticsEnum logisticsEnum : LogisticsEnum.values()) {
                if (logisticsEnum.getLogistics().equals(logistics)) {
                    return logisticsEnum;
                }
            }
        }
        return null;
    }

}
