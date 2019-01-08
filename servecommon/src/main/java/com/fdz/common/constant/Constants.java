package com.fdz.common.constant;

import com.fdz.common.exception.BizException;
import com.fdz.common.utils.StringUtils;

import java.nio.charset.Charset;

public final class Constants {

    private Constants() {
    }


    public static final class Common {
        public static final String MDC_SESSION_ID = "currentSessionId";
        public static final String MDC_REMOTE_IP = "remoteIp";
        public static final String X_REMOTE_IP_HEADER = "X_REMOTE_IP";
        public static final String X_SESSION_ID_HEADER = "X_SESSION_ID";
        public static final String UNKNOWN = "unknown";
        public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");

        public static final String TOKEN_P = "P-Authorization-Token";
        public static final String TOKEN_M = "M-Authorization-Token";
        public static final String M_USER_INFO_HEADER = "M-Forwarded-Principal";
        public static final String P_USER_INFO_HEADER = "P-Forwarded-Principal";
        public static final long JWT_TTL = 30 * 24 * 60 * 60 * 1000L;

        public static final String UNIQUE_KEY_HEADER = "UNIQUE-KEY";

        public static final String PROD_PROFILE = "prod";
        public static final String PRE_PROFILE = "pre";

        public static final String THIRDPARTY_VERSION = "1";

        public static final boolean needRealPay(String active) {
            return StringUtils.isNotBlank(active) && PROD_PROFILE.equals(active);
        }
    }

    public static final class BusinessCode {
        public static final int UNKNOWN_CODE = 400;
        public static final String UNKNOWN_MESSAGE = "未知错误";
        public static final int REQUEST_MANY_TIMES = 100104;
        public static final String REQUEST_MANY_TIMES_MESSAGE = "同一时间请求次数过多";
        public static final String VERIFY_CODE_MANY_TIMES_MESSAGE = "发送验证码过于频繁";
        public static final int THIRDPARTY_ERROR_CODE = 100101;
        public static final String THIRDPARTY_ERROR_MESSAGE = "业务：{0}, 调用错误";

        public static final int USER_NOT_LOGIN = 546;
        public static final String USER_NOT_LOGIN_MESSAGE = "您还未登录或者登录超时，请重新登录后再次操作";

        public static final int THIRDPARTY_ERROR_JSON = 100190;
        public static final int THIRDPARTY_ERROR_RSA = 100191;
    }

    public static final class RedisKey {
        public static final String LOCK_PREFIX = "LOCK_";
    }

    public static final class Symbol {
        public static final String QUESTIONS_MARK = "?";
        public static final String EQUAL_MARK = "=";
        public static final String AND = "&";
    }

    public enum ExceptionConstants {
        DEFAULT_LOCK_BIZEXCEPTION {
            public BizException throwException() {
                return new BizException(Constants.BusinessCode.REQUEST_MANY_TIMES, Constants.BusinessCode.REQUEST_MANY_TIMES_MESSAGE);
            }
        },
        SEND_VERIFY_CODE_BIZEXCEPTION {
            public BizException throwException() {
                return new BizException(Constants.BusinessCode.REQUEST_MANY_TIMES, Constants.BusinessCode.VERIFY_CODE_MANY_TIMES_MESSAGE);
            }
        };

        public BizException throwException() {
            throw new BizException();
        }
    }

    public static final class Sql {
        public static final String BASE_SQL = " id, create_time, modify_time, create_by, modify_by, remark,is_delete ";
        public static final String NOT_DELETED = " where is_delete=0 ";
        public static final String DEFAULT_ORDER = " order by id desc ";
        public static final String DEFAULT_ORDER_ASC = " order by id asc ";
        public static final String LIMIT_SQL = " limit #{page.start}, #{page.pageSize} ";
        public static final String LIMIT_ONLY_ONE = " limit 1";
    }

}
