package com.fdz.common.security;

import com.fdz.common.constant.Constants;
import com.fdz.common.exception.BizException;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.utils.UserDisassembly;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public final class SecurityUtils {

    private SecurityUtils() {
    }

    private static String getForwardedLoginUserId(String headerName) {
        String userId = "";
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (servletRequestAttributes != null) {
                HttpServletRequest request = servletRequestAttributes.getRequest();
                if (request != null) {
                    String candidate = request.getHeader(headerName);
                    if (!StringUtils.isEmpty(candidate)) {
                        userId = candidate;
                    }
                }
            }
        } catch (Exception e) {
            log.error("getForwardedLoginUserId error", e);
        }
        return userId;
    }

    public static String getCurrentLoginUserIdByManager() {
        return getForwardedLoginUserId(Constants.Common.M_USER_INFO_HEADER);
    }

    public static Long checkLoginAndGetUserByManager() {
        String userId = getCurrentLoginUserIdByManager();
        if (StringUtils.isNotBlank(userId)) {
            return Long.parseLong(UserDisassembly.disassembly(userId));
        }
        throw new BizException("未登录, 请登录后再操作");
    }

    public static Long checkLoginAndGetUserByPartner() {
        String userId = getCurrentLoginUserIdByPartner();
        if (StringUtils.isNotBlank(userId)) {
            return Long.parseLong(UserDisassembly.disassembly(userId));
        }
        throw new BizException("未登录, 请登录后再操作");
    }

    public static String getCurrentLoginUserIdByPartner() {
        return getForwardedLoginUserId(Constants.Common.P_USER_INFO_HEADER);
    }

    public static boolean checkPLogin() {
        return Boolean.valueOf(getForwardedLoginUserId(Constants.Common.P_USER_IS_LOGIN));
    }


    public static String getCurrenUserIdByAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userId = null;

        // 1. Attempts to obtain user info from security context
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                userId = springSecurityUser.getUsername();
            }
        }
        return userId;
    }

    /**
     * 组装token返回。
     *
     * @param token
     * @return
     */
    public static String assembleToken(String token) {
        return "Bearer " + token;
    }

    /**
     * @param bearToken
     * @return
     */
    public static String disassembleToken(String bearToken) {
        if (StringUtils.isNotBlank(bearToken) && bearToken.startsWith("Bearer ")) {
            return bearToken.substring(7, bearToken.length());
        }
        return bearToken;
    }
}
