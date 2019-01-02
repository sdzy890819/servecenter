package com.fdz.common.utils;

import com.fdz.common.exception.BizException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
    public static final String DATE_FORMATTE_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMATTER_ALL = "yyyyMMddHHmmss";
    public static final String DATE_FORMATTER_DAY = "yyyy-MM-dd";
    public static final String DATE_FORMATTER_ALL_16 = "yyyyMMddHHmmssSSS";

    public static int getCurrentTimes() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static String getCurrentTimes(String formatter) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMATTE_DEFAULT);
        return simpleDateFormat.format(new Date());
    }

    public static String formatTime(Integer time, String formatter) {
        if (time == null) {
            return "";
        }
        return formatTime(time, formatter);
    }

    public static String formatTime(int time, String formatter) {
        if (time <= 0) {
            return null;
        }
        if (StringUtils.isNull(formatter)) {
            formatter = DATE_FORMATTE_DEFAULT;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatter);
        return simpleDateFormat.format(new Date((long) time * 1000));
    }

    public static String formatTime(Long time, String formatter) {
        if (time == null || time <= 0) {
            return null;
        }
        if (StringUtils.isNull(formatter)) {
            formatter = DATE_FORMATTE_DEFAULT;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatter);
        return simpleDateFormat.format(new Date(time));
    }

    public static String formatNowTime(String formatter) {
        return formatTime(new Date().getTime(), formatter);
    }

    public static long getTodayStartTimeMillis(long time) {
        return getAppointTimeMillis(time, 0, 0, 0);
    }

    public static long getAppointTimeMillis(long time, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        if (time <= 0) {
            calendar.setTimeInMillis(getCurrentTimeMillis());
        } else {
            calendar.setTimeInMillis(time);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (hour < 0 || hour > 23) {
            hour = 0;
        }
        if (minute < 0 || minute > 60) {
            minute = 0;
        }
        if (second < 0 || second > 0) {
            second = 0;
        }
        calendar.clear();
        calendar.set(year, month, day, hour, minute, second);
        return calendar.getTimeInMillis();
    }

    public static String birthDay(String idCard) {
        if (StringUtils.isNull(idCard)) {
            return null;
        }

        StringBuffer stringBuffer = new StringBuffer(idCard.substring(6, 10));
        stringBuffer.append("-").append(idCard.substring(10, 12)).append("-").append(idCard.substring(12, 14));
        return stringBuffer.toString();
    }

    public static long addMonth(long time, int month) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTimeInMillis(time);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + month, calendar.get(Calendar.DAY_OF_MONTH));
        return calendar.getTimeInMillis();
    }

    public static int getDayRestMinits(Long time) {
        if (time == null) {
            time = getCurrentTimeMillis();
        }
        long dayEndTime = dayEndTime(time);
        return (int) ((dayEndTime - time) / 60000);


    }

    public static long dayEndTime(Long time) {
        if (time == null) {
            time = getCurrentTimeMillis();
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTimeInMillis(time);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) + 1);
        return calendar.getTimeInMillis() - 1;
    }

    public static long parse(String date, String formatter) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatter);
            return simpleDateFormat.parse(date).getTime();
        } catch (ParseException e) {
            throw new BizException("日期转换异常");
        }
    }

    public static LocalDate formatLocalDate(Long date) {
        return Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 相隔天数
     *
     * @param date
     * @param otherDate
     * @return
     */
    public static long differentDaysBySeparated(Long date, Long otherDate) {

        String dateStr = formatTime(date, DATE_FORMATTER_DAY);
        String otherDateStr = formatTime(otherDate, DATE_FORMATTER_DAY);

        long beforeTime = parse(dateStr, DATE_FORMATTER_DAY);
        long afterTime = parse(otherDateStr, DATE_FORMATTER_DAY);

        return (beforeTime - afterTime) / (1000 * 60 * 60 * 24);

    }

}
