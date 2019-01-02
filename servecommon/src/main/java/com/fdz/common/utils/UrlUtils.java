package com.fdz.common.utils;

import com.fdz.common.exception.BizException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by wusongsong on 2018/4/23.
 */
public class UrlUtils {

    public static String encode(String url) {
        try {
            String encodeURL = URLEncoder.encode(url, "UTF-8");
            return encodeURL;
        } catch (UnsupportedEncodingException e) {
            throw new BizException("url加密异常", e);
        }
    }

    public static String decode(String url) {
        try {
            return URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new BizException("url解密异常,", e);
        }
    }
}
