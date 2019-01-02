package com.fdz.thirdpartygateway.utils;

import com.netflix.zuul.http.ServletInputStreamWrapper;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServletUtils {


    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return buildArgs(params);
    }

    /**
     * 如果参数传错为数组，只获取数组的最后一个参数
     *
     * @param params
     * @return
     */
    private static Map<String, String> buildArgs(Map<String, String[]> params) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String[]> me : params.entrySet()) {
            String[] values = me.getValue();
            map.put(me.getKey(), values[values.length - 1]);
        }
        return map;
    }

    public static HttpServletRequest buildRequest(HttpServletRequest request, byte[] bodyBytes) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public ServletInputStream getInputStream() throws IOException {
                return new ServletInputStreamWrapper(bodyBytes);
            }

            @Override
            public int getContentLength() {
                return bodyBytes.length;
            }

            @Override
            public long getContentLengthLong() {
                return bodyBytes.length;
            }

            @Override
            public String getContentType() {
                return APPLICATION_JSON_CHARSET_UTF_8;
            }
        };
    }


}
