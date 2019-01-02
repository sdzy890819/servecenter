package com.fdz.common.utils;

import com.fdz.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public final class HttpServletUtils {

    private HttpServletUtils() {
    }

    /**
     * 获取用户IP
     *
     * @param request
     * @return
     */
    public static String getRemoteIPAddress(HttpServletRequest request) {
        String remoteIP = request.getHeader("X-Forwarded-For");
        if (remoteIP == null || remoteIP.length() == 0 || Constants.Common.UNKNOWN.equalsIgnoreCase(remoteIP)) {
            remoteIP = request.getHeader("Proxy-Client-IP");
        }
        if (remoteIP == null || remoteIP.length() == 0 || Constants.Common.UNKNOWN.equalsIgnoreCase(remoteIP)) {
            remoteIP = request.getHeader("WL-Proxy-Client-IP");
        }
        if (remoteIP == null || remoteIP.length() == 0 || Constants.Common.UNKNOWN.equalsIgnoreCase(remoteIP)) {
            remoteIP = request.getRemoteAddr();
        }
        if (remoteIP != null && remoteIP.length() > 0) {
            String[] ips = remoteIP.split(",");
            for (int i = 0; i < ips.length; i++) {
                if (ips[i].trim().length() > 0 && !Constants.Common.UNKNOWN.equalsIgnoreCase(ips[i].trim())) {
                    remoteIP = ips[i].trim();
                    break;
                }
            }
        }
        return remoteIP;
    }

    /**
     * 获取URL
     *
     * @param request
     * @return
     */
    public static String getAppURL(HttpServletRequest request) {
        if (request == null)
            return "";

        StringBuilder url = new StringBuilder();
        int port = request.getServerPort();
        if (port < 0) {
            port = 80; // Work around java.net.URL bug
        }
        String scheme = request.getScheme();
        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());
        if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
            url.append(':');
            url.append(port);
        }
        url.append(request.getContextPath());
        return url.toString();
    }

    /**
     * 获取请求体
     *
     * @param request
     * @return
     */
    public static String getRequestBody(HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder();
        try {
            InputStream in = request.getInputStream();
            BufferedReader bin = new BufferedReader(new InputStreamReader(in, Charset.forName("UtF-8")));
            String line = "";
            while ((line = bin.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (IOException e) {
            log.error("getRequestBody IOException", e);
            return null;
        }
    }

    /**
     * 获取请求参数、拼接成参数串
     *
     * @param request
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String getRequestParam(HttpServletRequest request) {
        Iterator iterator = request.getParameterMap().entrySet().iterator();
        StringBuilder param = new StringBuilder();
        int i = 0;
        while (iterator.hasNext()) {
            i++;
            Map.Entry entry = (Map.Entry) iterator.next();
            if (i == 1)
                param.append("?").append(entry.getKey()).append("=");
            else
                param.append("&").append(entry.getKey()).append("=");
            if (entry.getValue() instanceof String[]) {
                param.append(((String[]) entry.getValue())[0]);
            } else {
                param.append(entry.getValue());
            }
        }
        return param.toString();
    }

}
