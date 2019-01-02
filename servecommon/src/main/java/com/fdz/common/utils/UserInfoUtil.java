package com.fdz.common.utils;


public class UserInfoUtil {

    public static String getSex(String idCard) {
        if (StringUtils.isNotBlank(idCard) || idCard.length() != 15 && idCard.length() != 18) {
            return "";
        }
        int num = Integer.parseInt(idCard.length() == 15 ? idCard.substring(14, 15) : idCard.substring(16, 17));
        return num % 2 == 0 ? "女" : "男";

    }

}
