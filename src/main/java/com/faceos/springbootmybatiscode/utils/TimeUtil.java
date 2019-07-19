package com.faceos.springbootmybatiscode.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TimeUtil
 * 时间工具类
 *
 * @author lang
 * @date 2019-07-08
 */
public class TimeUtil {
    private static final long MILLIS_IN_A_SECOND = 1000;
    private static final long SECONDS_IN_A_MINUTE = 60;
    private static final long MINUTES_IN_AN_HOUR = 60;
    private static final long HOURS_IN_A_DAY = 24;

    private static final int DAYS_IN_A_WEEK = 7;
    private static final int MONTHS_IN_A_YEAR = 12;

    /**
     * String 转 Date
     *
     * @param dateString
     * @return
     */
    public static Date getDateWithDateString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Date 转 String
     *
     * @return
     */
    public String GetNowDate(){
        String temp_str;
        Date dt = new Date();
        //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
        temp_str=sdf.format(dt);
        return temp_str;
    }

    /**
     * 获得两个时间的分钟差值
     *
     * @param time1
     * @param time2
     * @return
     */
    public static int getMinuteDiffByTime(Date time1, Date time2) {
        long startMil = 0;
        long endMil = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time1);
        startMil = calendar.getTimeInMillis();
        calendar.setTime(time2);
        endMil = calendar.getTimeInMillis();
        return (int) ((endMil - startMil) / MILLIS_IN_A_SECOND / SECONDS_IN_A_MINUTE);
    }
}
