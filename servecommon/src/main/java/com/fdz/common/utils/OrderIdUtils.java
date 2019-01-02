package com.fdz.common.utils;

import java.util.UUID;

/**
 * 订单号生成工具
 */
public class OrderIdUtils {

    /**
     * 32位uuid
     *
     * @return
     */
    public static String get32Uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 36位uuid
     *
     * @return
     */
    public static String get36Uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 下单渠道1位+支付渠道2位+业务类型1位+年月日8位+时分秒毫秒9位+4位随机数
     * 下单渠道：0代表PC、1代表Android 、2代表iOS、3代表H5
     * 支付渠道：01代表花呗、02代表P2P、03代表支付宝SDK、04代表微信SDK
     * 业务类型：0代表现金贷、1代表现金分期、2代表租赁分期、3代表会员
     *
     * @return
     */
    public static String getUserOrderId(String deviceType, String payChannel, String bizType) {

        return deviceType + payChannel + bizType +
                TimeUtils.formatNowTime(TimeUtils.DATE_FORMATTER_ALL_16) + ((int) (Math.random() * 10000));
    }

    //生成扣款订单号
    public static String generateDeductOrderId(byte payChannel) {
        String orderId = TimeUtils.formatNowTime(TimeUtils.DATE_FORMATTER_ALL);
        if (payChannel < 10) {
            orderId = orderId + "00" + payChannel;
        } else if (payChannel < 100) {
            orderId = orderId + "0" + payChannel;
        } else {
            orderId = orderId + "" + payChannel;
        }
        int random = (int) (Math.random() * 100000000);
        if (random < 10000000) {
            random += 10000000;
        }
        return orderId + random;
    }

}
