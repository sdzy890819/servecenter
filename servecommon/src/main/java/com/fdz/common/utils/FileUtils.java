package com.fdz.common.utils;


import com.fdz.common.exception.BizException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by wusongsong on 2018/1/25.
 */
public class FileUtils {

    public static boolean isOsLinux() {
        Properties properties = System.getProperties();
        String os = properties.getProperty("os.name");
        if (os != null && os.toLowerCase().indexOf("linux") > -1) {
            return true;
        } else {
            return false;
        }
    }

    public static void write(File file, InputStream inputStream) {
        byte[] buffer = new byte[1024];
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            while ((inputStream.read(buffer, 0, 1024)) > 0) {
                String tmp = new String(buffer, "UTF-8").trim();
                byte[] bytes = tmp.getBytes("UTF-8");
                fileOutputStream.write(bytes, 0, bytes.length);
                buffer = new byte[1024];
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.close(fileOutputStream);
        }
    }

    public static String getPath(String path) {
        String currentTimes = TimeUtils.getCurrentTimes() + "";
        String tmp = currentTimes.substring(6, 8) + "/" + currentTimes.substring(8, 10);
        path = StringUtils.isNull(path) ? tmp : path + "/" + tmp;
        return path;
    }

    public static String readFile(File file) {
        if (file == null || file.isDirectory()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String online = null;
            while ((online = br.readLine()) != null) {
                builder.append(online);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.close(br);
        }
    }

    public static List<String> readToList(String downLoanUrl) {
        BufferedReader br = null;
        List<String> strs = new ArrayList<>();
        try {
            URL url = new URL(downLoanUrl);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
            urlCon.setConnectTimeout(5000);
            urlCon.setReadTimeout(5000);
            br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            String tmp = null;
            while ((tmp = br.readLine()) != null) {
                strs.add(tmp);
            }
            return strs;
        } catch (IOException e) {
            throw new BizException("读取流异常", e);
        }
    }

    public static List<String> readFileToList(File file) {
        if (file == null || file.isDirectory()) {
            return null;
        }
        List<String> strs = new ArrayList<>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String online = null;
            while ((online = br.readLine()) != null) {
                strs.add(online.replace("\u0000", ""));
            }
            return strs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.close(br);
        }
    }

}