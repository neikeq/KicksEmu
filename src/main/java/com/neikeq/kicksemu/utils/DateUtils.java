package com.neikeq.kicksemu.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static String getTimeString() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("[HH:mm:ss]");
        return timeFormat.format(Calendar.getInstance().getTime());
    }

    public static String dateToString(Date date) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(date);
        } else {
            return "0000-00-00 00:00:00";
        }
    }

    /** Returns a formatted DateTime String that can be used for file naming. */
    public static String getFileDateTimeString() {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        return dateTimeFormat.format(Calendar.getInstance().getTime());
    }

    public static Date getDate() {
        return Calendar.getInstance().getTime();
    }

    public static java.sql.Date getSqlDate() {
        return new java.sql.Date(getDate().getTime());
    }

    public static Timestamp getTimestamp() {
        return toTimestamp(getSqlDate());
    }

    public static Timestamp toTimestamp(java.sql.Date date) {
        if (date == null) {
            return null;
        }

        return new Timestamp(date.getTime());
    }

    public static java.sql.Date addDays(java.sql.Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, amount);

        return new java.sql.Date(calendar.getTime().getTime());
    }
}
