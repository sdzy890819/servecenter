package com.fdz.common.utils;

public class UserDisassembly {

    private static final String SPLIT_SYMBOL = "_";

    private static final String START_P_SYMBOL = "P";

    private static final String START_M_SYMBOL = "M";

    /**
     * 拆解出用户
     *
     * @param principal
     * @return
     */
    public static String disassembly(String principal) {
        if (StringUtils.isNotEmpty(principal)) {
            return principal.split(SPLIT_SYMBOL)[1];
        }
        return null;
    }

    /**
     * 组装出C用户
     *
     * @param user
     * @return
     */
    public static String assembleP(Long user) {
        return START_P_SYMBOL.concat(SPLIT_SYMBOL).concat(String.valueOf(user));
    }

    /**
     * 组装出X用户
     *
     * @param user
     * @return
     */
    public static String assembleM(Long user) {
        return START_M_SYMBOL.concat(SPLIT_SYMBOL).concat(String.valueOf(user));
    }

    public static boolean isP(String userId) {
        return userId.startsWith(START_P_SYMBOL);
    }

    public static boolean isM(String userId) {
        return userId.startsWith(START_M_SYMBOL);
    }

}
