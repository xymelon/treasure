package com.xycoding.treasure.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xuyang on 2016/7/25.
 */
public class DateTimeUtils {

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static String currentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date(currentTimeMillis()));
    }

    /**
     * 格式：yyyy年MM月dd日
     *
     * @return
     */
    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * 格式：yyyy.MM.dd
     *
     * @return
     */
    public static String formatDate1(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * 格式：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String formatDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static boolean isToday(long timestamp) {
        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.setTime(new Date(currentTimeMillis()));
        Calendar secondCalendar = Calendar.getInstance();
        secondCalendar.setTime(new Date(timestamp));
        return firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR)
                && firstCalendar.get(Calendar.DAY_OF_YEAR) == secondCalendar.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isYesterday(long timestamp) {
        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.setTime(new Date(currentTimeMillis()));
        firstCalendar.add(Calendar.DATE, -1);
        Calendar secondCalendar = Calendar.getInstance();
        secondCalendar.setTime(new Date(timestamp));
        return firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR)
                && firstCalendar.get(Calendar.DAY_OF_YEAR) == secondCalendar.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 比较两者是否同一日期
     *
     * @param timestamp1
     * @param timestamp2
     * @return
     */
    public static boolean dateEquals(long timestamp1, long timestamp2) {
        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.setTime(new Date(timestamp1));
        Calendar secondCalendar = Calendar.getInstance();
        secondCalendar.setTime(new Date(timestamp2));
        return firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR)
                && firstCalendar.get(Calendar.DAY_OF_YEAR) == secondCalendar.get(Calendar.DAY_OF_YEAR);
    }

}
