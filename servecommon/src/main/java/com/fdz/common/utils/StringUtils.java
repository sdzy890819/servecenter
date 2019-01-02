package com.fdz.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;

import java.io.UnsupportedEncodingException;
import java.util.Collection;


@Slf4j
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static boolean isTrue(Long a) {
        return a != null && a > 0;
    }

    public static boolean isNotEmpty(Collection collection) {
        return collection != null && collection.size() > 0;
    }

    public static boolean isEmpty(Collection collection) {
        return !(collection != null && collection.size() > 0);
    }


    public static String setDefaultValueWhenStringIsNull(String str, String defaultValue) {
        return isNotBlank(str) ? str : defaultValue;
    }

    /**
     * 截取字符
     *
     * @param content
     * @param length
     * @return
     */
    public static String sub(String content, int length) {
        if (StringUtils.isNotBlank(content) && content.length() > length) {
            return content.substring(0, length);
        }
        return content;
    }

    /**
     * 删除首个prefix
     *
     * @param content
     * @param prefix
     * @return
     */
    public static String delFirstPrefix(String content, String prefix) {
        while (isNotBlank(content) && isNotBlank(prefix) && content.startsWith(prefix)) {
            content = content.substring(prefix.length());
        }
        return content;
    }

    public static boolean isNull(String arg) {
        return arg == null || arg.trim().length() <= 0 ? true : false;
    }

    public static boolean isNotNull(String arg) {
        return arg != null && arg.trim().length() > 0 ? true : false;
    }

    public static String format(String formatter, String... args) {
        if (isNull(formatter) || args == null || args.length <= 0 || !formatter.contains("{}")) {
            return formatter;
        }
        for (int count = 0; count < args.length; count++) {
            formatter = formatter.replaceFirst("\\{\\}", args[count]);
        }
        return formatter;
    }

    /**
     * 获取字符串的前n位, 可以从0位开始
     *
     * @param str
     * @param index 如前6位, index 为 6
     * @return
     */
    public static String prefix(String str, Integer index) {
        if (isNull(str) || str.length() <= index || index <= 0) {
            return "";
        }
        return str.substring(0, index);
    }

    /**
     * 获取字符串的后几位
     *
     * @param str
     * @param index
     * @return
     */
    public static String latter(String str, int index) {
        if (isNull(str) || str.length() <= index || index <= 0) {
            return "";
        }
        return str.substring(str.length() - index);
    }

    public static String getValue(Object object) {
        return object == null ? null : object.toString();
    }

    /**
     * 拼接URL数据
     *
     * @param strings
     * @return
     */
    public static String concatUrl(String... strings) {
        StringBuffer sbf = new StringBuffer();
        if (strings != null && strings.length > 0) {
            for (int i = 0; i < strings.length; i++) {
                if (isNotBlank(strings[i])) {
                    if (i == 0) {
                        if (strings[i].endsWith("/")) {
                            sbf.append(strings[i]);
                        } else {
                            sbf.append(strings[i]);
                            sbf.append("/");
                        }
                    } else if (i + 1 == strings.length) {
                        if (strings[i].startsWith("/")) {
                            sbf.append(strings[i].substring(1));
                        } else {
                            sbf.append(strings[i]);
                        }
                    } else {
                        if (strings[i].startsWith("/")) {
                            if (strings[i].endsWith("/")) {
                                sbf.append(strings[i].substring(1));
                            } else {
                                sbf.append(strings[i].substring(1));
                                sbf.append("/");
                            }
                        } else {
                            if (strings[i].endsWith("/")) {
                                sbf.append(strings[i]);
                            } else {
                                sbf.append(strings[i]);
                                sbf.append("/");
                            }
                        }
                    }
                }
            }
        }
        return sbf.toString();
    }

    /**
     * 转义超过3字符.
     *
     * @param input
     * @return
     */
    public static String escapeMoreThan3ByteString(String input) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            byte[] inputBytes = input.getBytes("UTF-8");
            for (int i = 0; i < inputBytes.length; i++) {
                byte tmp = inputBytes[i];
                if (CharUtils.isAscii((char) tmp)) {
                    byte[] ba = new byte[1];
                    ba[0] = tmp;
                    stringBuilder.append(new String(ba));
                }
                if ((tmp & 0xE0) == 0xC0) {
                    byte[] ba = new byte[2];
                    ba[0] = tmp;
                    ba[1] = inputBytes[i + 1];
                    i++;
                    stringBuilder.append(new String(ba));
                }
                if ((tmp & 0xF0) == 0xE0) {
                    byte[] ba = new byte[3];
                    ba[0] = tmp;
                    ba[1] = inputBytes[i + 1];
                    ba[2] = inputBytes[i + 2];
                    i++;
                    i++;
                    stringBuilder.append(new String(ba));
                }
                if ((tmp & 0xF8) == 0xF0) {
                    byte[] ba = new byte[4];
                    ba[0] = tmp;
                    ba[1] = inputBytes[i + 1];
                    ba[2] = inputBytes[i + 2];
                    ba[3] = inputBytes[i + 3];
                    i++;
                    i++;
                    i++;
                    log.info("剔除4字节文字, 原文: {}, 剔除字样: {}", input, new String(ba));
                }
            }
            return stringBuilder.toString();
        } catch (UnsupportedEncodingException e) {
            log.error("获取get byte, {}", input);
        }
        return input;
    }

}
