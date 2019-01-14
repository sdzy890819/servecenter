package com.fdz.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public final class StreamUtils {
    private StreamUtils() {
    }

    /**
     * 根据流获取byte
     *
     * @param in
     * @return
     */
    public static byte[] getBytes(InputStream in) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buf)) != -1) {
                byteArrayOutputStream.write(buf, 0, bytesRead);
            }
            in.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("getBytes IOException", e);
        }
        return new byte[0];
    }

    /**
     * 获取 url的文件流。base
     *
     * @param urlPath
     * @return
     */
    public static String baseUrl(String urlPath) {
        try {
            URL url = new URL(urlPath);
            InputStream in = url.openStream();
            byte[] imageBytes = getBytes(in);
            return new String(Base64Utils.encode(imageBytes), "UTF-8");
        } catch (MalformedURLException e) {
            log.error("读取图片 malformed url 出现错误：", e.getMessage());
        } catch (IOException e) {
            log.error("读取图片 io 出现错误：", e.getMessage());
        } catch (Exception e) {
            log.error("读取图片-base code出现错误：", e.getMessage());
        }
        return null;
    }

}
