package com.fdz.common.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 用于对敏感字段打掩码
 */
public class WordMaskUtils {

    private static final List<String> MOBILE_KEYS = Arrays.asList("mobile", "phone");
    private static final List<String> ID_CARD_KEYS = Arrays.asList("idCard", "id_card");
    private static final List<String> CARD_NO_KEYS = Arrays.asList("cardNo", "card_no");

    /**
     * 银行卡打掩码
     *
     * @param cardNO
     * @return
     */
    public static String cardNoMask(String cardNO) {
        if (StringUtils.isNull(cardNO)) {
            return "";
        }
        if (cardNO.length() < 10) {
            return cardNO;
        }
        return new StringBuffer().append(cardNO.substring(0, 6))
                .append("******").append(cardNO.substring(cardNO.length() - 4)).toString();
    }

    /**
     * 身份证号打掩码
     *
     * @param idCard
     * @return
     */
    public static String idCardMask(String idCard) {
        if (StringUtils.isNull(idCard)) {
            return "";
        }
        if (idCard.length() != 15 && idCard.length() != 18) {
            return idCard;
        }
        return new StringBuffer().append(idCard.substring(0, 6)).append("********")
                .append(idCard.substring(idCard.length() - 4)).toString();
    }

    /**
     * 手机号打掩码
     *
     * @param mobile
     * @return
     */
    public static String mobileMask(String mobile) {
        if (StringUtils.isNull(mobile)) {
            return "";
        }
        if (mobile.length() != 11) {
            return mobile;
        }
        return new StringBuffer(mobile.substring(0, 3)).append("****").append(mobile.substring(7)).toString();
    }

    public static String mapMask(Map content) {
        if (content == null || content.size() <= 0) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        content.forEach((k, v) -> {
            if (MOBILE_KEYS.contains(k)) {
                buffer.append(k).append("=").append(mobileMask(v.toString())).append(",");
            } else if (ID_CARD_KEYS.contains(k)) {
                buffer.append(k).append("=").append(idCardMask(v.toString())).append(",");
            } else if (CARD_NO_KEYS.contains(k)) {
                buffer.append(k).append("=").append(cardNoMask(v.toString())).append(",");
            } else {
                buffer.append(k).append("=").append(v).append(",");
            }
        });
        String str = buffer.toString();
        if (StringUtils.isNull(str)) {
            return "";
        } else {
            return str.substring(0, str.length() - 1);
        }
    }

}
