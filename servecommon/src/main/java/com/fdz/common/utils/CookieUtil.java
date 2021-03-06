package com.fdz.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ADMIN on 16/11/24.
 */
@Slf4j
public class CookieUtil {

    private static final String UTF8 = "utf-8";

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        response.addCookie(addcookie(name, value, maxAge));
    }


    public static Cookie addcookie(String name, String value, int maxAge) {
        Cookie cookie = null;
        try {
            cookie = new Cookie(name, URLEncoder.encode(value, UTF8));
            cookie.setPath("/");
            if (maxAge > 0) {
                cookie.setMaxAge(maxAge);
            }
        } catch (UnsupportedEncodingException e) {
            log.error("设置Cookie错误", e);
        }
        return cookie;
    }

    public static String getCookieVal(HttpServletRequest request, String name) {
        Map<String, String> cookieMap = getCookieMap(request);
        if (cookieMap.containsKey(name)) {
            return cookieMap.get(name);
        } else {
            return null;
        }
    }

    public static void delCookieVal(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (name != null && !"".equals(name)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
    }

    private static Map<String, String> getCookieMap(HttpServletRequest request) {
        Map<String, String> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        try {
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    cookieMap.put(cookie.getName(), URLDecoder.decode(cookie.getValue(), UTF8));
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("错误", e);
        }
        return cookieMap;
    }


    //----获取Ip
    public static String getRemoteIp(HttpServletRequest request) {

        if (request.getHeader("x-forwarded-for") == null || "".equals(request.getHeader("x-forwarded-for"))) {
            return request.getRemoteAddr();
        }
        return request.getHeader("x-forwarded-for");
    }
}
