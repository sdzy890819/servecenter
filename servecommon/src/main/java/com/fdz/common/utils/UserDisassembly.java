package com.fdz.common.utils;

public class UserDisassembly {

    private static final String SPLIT_SYMBOL = "_";

    private static final String START_C_SYMBOL = "C";

    private static final String START_X_SYMBOL = "X";

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
    public static String assembleC(Long user) {
        return START_C_SYMBOL.concat(SPLIT_SYMBOL).concat(String.valueOf(user));
    }

    /**
     * 组装出X用户
     *
     * @param user
     * @return
     */
    public static String assembleX(Long user) {
        return START_X_SYMBOL.concat(SPLIT_SYMBOL).concat(String.valueOf(user));
    }

    public static boolean isC(String userId) {
        return userId.startsWith(START_C_SYMBOL);
    }


}
