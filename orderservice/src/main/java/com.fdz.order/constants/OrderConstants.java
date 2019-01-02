package com.fdz.order.constants;

public class OrderConstants {

    /**
     * 业务码
     */
    public static final class BusinessCode {
        public static final String NEED_CAPTCHACODE_MESSAGE = "需要输入验证码";
        public static final int NEED_CAPTCHACODE = 190101;
        public static final String CAPTCHACODE_ERROR = "验证码输入错误";
        public static final String VERIFY_CODE_SEND_ERROR = "验证码发送失败!";
        public static final String SYSTEM_EXCEPTION = "系统繁忙，请稍后重试";
        public static final String SYSTEM_FREUENT_OPERATION_MESSAGE = "操作频繁,请稍后重试";

        public static final int REPAYMENT_HAS_SETTLED_CODE = 190201;
        public static final String REPAYMENT_HAS_SETTLED_MESSAGE = "还款计划已经结清";
        public static final int REPAYMENT_BANK_CARD_DISABLE_CODE = 190202;
        public static final String REPAYMENT_BANK_CARD_DISABLE_MESSAGE = "您选择的银行卡禁用，请稍后重试";
        public static final int REPAYMENT_REQUEST_FAILD_CODE = 190203;
        public static final String REPAYMENT_REQUEST_FAILD_MESSAGE = "还款操作失败，请稍后重试";
        public static final int REPAYMENT_HAS_ONE_ORDER_PAYING_CODE = 190204;
        public static final String REPAYMENT_HAS_ONE_ORDER_PAYING_MESSAGE = "您有订单正在处理中，请稍后重试";

        public static final int PRODUCT_NOT_FOUND_CODE = 190300;
        public static final String PRODUCT_NOT_FOUND_MESSAGE = "未找到产品信息";

        public static final String MAX_MEMBER_REDUCTION_TITLE = "加入会员 - 借款服务费最高可免%S元";
        public static final String MEMBER_REDUCTION_TITLE = "本次借款服务费已减免%S元";


        public static final String REPAYMENT_TERM_NUM = "第%d期";


    }

    public static final class Sms {
        public static final String LOAN_MESSAGE = "您申请的借款{0}元，在尾号{1}的银行卡中已到账。";
        public static final String REPAYMENT_SUCCESS = "您于{0}还款{1}元，本次借款已结清。";
    }

    /**
     * Redis
     */
    public static final class RedisKey {
        public static final String COUNT_VERIFY_CODE = "SMS_VERIFYCODE_COUNT_MOBILE_{0}_TYPE_{1}";
        public static final String CAPTCHA_PHONE_PREFIX = "SMS_CAPTCHA_MOBILE_{0}";
        public static final String MOBILE_LOCK_VERIFY_CODE = "SMS_VERIFYCODE_MOBILE_#{mobile}_TYPE_#{type}";
        public static final String SMS_VERIFY_CODE = "SMS_VERIFY_{0}_TYPE_{1}";
        public static final String REPAYMENT_BANK_NOT_CAN_USE_PREFIX = "payment";
        public static final String REPAYMENT_DEDUCT_REQUEST = "REPAYMENT_DEDUCT_REQUEST_{repaymentId}";
        public static final String DEDUCT_SCHEDULE = "REPAYMENT_DEDUCT_REQUEST_#{creditRepaymentInfo.repaymentInfoId}";
        public static final String DEDUCT_PROCESS = "REPAYMENT_DEDUCT_PROCESS_#{repayResultDto.bizOrderId}";
        public static final String SIGN_LOCK = "SIGN_LOCK_#{orderSn}";
        public static final String HAS_ORDER_LOCK = "ORDER_USER_#{userId}";
        public static final String HAS_ORDER_BORROW_LOCK = "ORDER_USER_BORROW_#{userId}";
        public static final String BORROW_USER_PORUDCTID = "TO_BORROW_USER_PORUDCTID_{0}";
        public static final String DEDUCT_ORDER_INFO = "DEDUCT_BIZ_ORDER_ID_{0}";
    }

    public static final class SQL {
        public static final String CREDIT_REPAYMENT_INFO_COLOMNS = "  repayment_info_id, credit_repaymen_id, user_id, order_sn, num, plan_principal, handling_charge," +
                "service_money, plan_service_money, third_service_money, third_principal, third_interest," +
                "plan_interest, prepayment_penalty, prepayment_date, prepayment_amount, diff_value," +
                "end_interest_date, plan_date, real_date, overdue_days, overdue_interest, biz_status," +
                "is_borrower_repay, baddebt_date, baddebt_amount, amount_repayment, create_time, modify_time," +
                "remark, is_delete, pay_sn, account_start_time,first_principal,second_principal,payment_method,ident ";
        public static final String CREDIT_REPAYMENT_COLOMNS = " credit_repayment_id, user_id, order_sn, term_num, plan_principal, handling_charge, " +
                " service_money, plan_service_money, third_service_money, third_principal, third_interest," +
                " plan_interest, diff_value, start_interest_date, plan_date, real_date, overdue_days," +
                " overdue_interest, prepayment_date, prepayment_amount, prepayment_penalty, biz_status," +
                " is_borrower_repay, amount_repayment, loan_release_batch_no, asset_id, create_time," +
                " is_delete, modify_time, remark, prepayment_times, pay_sn,first_principal,second_principal,payment_method,ident ";
    }

    public static final class RepaymentType {
        public static final byte SINGLE_PHASE_REPAYMENT = 1;
        public static final byte ALL_SETTLE_CLEAR = 2;
    }

    public static final class Repayments {
        public static final int CREREPEMENT_IS_BORROWE_REPAY_NOT_SETTLED = 0;//未还
        public static final int CREREPEMENT_IS_BORROWE_REPAY_SETTLED = 1;//已经还清

        //业务状态（100正常未还、200正常已还、300逾期i已还款、400提前还款、500坏账回购、600逾期未还）
        public static final int BIZ_STATUS = 100; // 100正常未还
        public static final int BIZ_STATUS_REPAY = 200; // 200正常已还
        public static final int BIZ_STATUS_REPAY_OVER = 300;// 300逾期已还款
        public static final int BIZ_STATUS_AD = 400;// 400提前还款
    }

    public static final class Sql {
        public static final String BASE_COLUMN_REDUCTION = "id, user_id, repayment_info_id, create_time, credit_repayment_id, modify_time, reduction_money, " +
                "    repayment_money, reduction_success_time, reduction_remark_id, reduction_state, expire_time, " +
                "    operate_name, create_by, update_by, is_delete, valid, remark";
    }

    public static final class LoanTermUnit {
        public static final byte YEAR = 1;
        public static final String YEAR_MESSAGE = "年";
        public static final byte QUARTER = 2;
        public static final String QUARTER_MESSAGE = "季度";
        public static final byte MONTH = 3;
        public static final String MONTH_MESSAGE = "月";
        public static final byte WEEK = 4;
        public static final String WEEK_MESSAGE = "周";
        public static final byte DAY = 5;
        public static final String DAY_MESSAGE = "天";
        public static final byte HOUR = 6;
        public static final String HOUR_MESSAGE = "小时";
        public static final byte TIMES = 7;
        public static final String TIMES_MESSAGE = "次";
    }

    public static final class InputAssetState {
        public static final byte PROCESSING = 0;
        public static final byte SUCCESS = 1;
        public static final byte FAILD = 2;
    }

    public static final class SignState {
        public static final byte PROCESSING = 0;
        public static final byte SUCCESS = 1;
        public static final byte PAY_FAILD = 2;
        public static final byte SIGN_FAILD = 3;
    }
}
