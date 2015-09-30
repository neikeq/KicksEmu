package com.neikeq.kicksemu.utils;

import java.util.Date;

public class DayTimeRange implements Comparable<DayTimeRange> {

    private final int startMinutes;
    private final int endMinutes;

    public boolean isWithinRange(Date date) {
        int minutes = DateUtils.getDateHourOfDay(date) * 60 + DateUtils.getDateMinute(date);
        return minutes >= startMinutes || minutes < endMinutes;
    }

    public boolean isAfterRange(Date date) {
        int minutes = DateUtils.getDateHourOfDay(date) * 60 + DateUtils.getDateMinute(date);
        return minutes < startMinutes;
    }

    public int minutesFrom(Date date) {
        int minutes = DateUtils.getDateHourOfDay(date) * 60 + DateUtils.getDateMinute(date);
        return startMinutes - minutes;
    }

    public static DayTimeRange build(int startHour, int endHour) {
        return build(startHour, 0, endHour, 0);
    }

    public static DayTimeRange build(int startHour, int startMinute, int endHour, int endMinute) {
        return new DayTimeRange(DateUtils.buildTimeOfDay(startHour, startMinute),
                DateUtils.buildTimeOfDay(endHour, endMinute));
    }

    public DayTimeRange(Date start, Date end) {
        startMinutes = DateUtils.getDateHourOfDay(start) * 60 + DateUtils.getDateMinute(start);
        endMinutes = DateUtils.getDateHourOfDay(end) * 60 + DateUtils.getDateMinute(end);
    }

    @Override
    public int compareTo(DayTimeRange o) {
        int result = 0;

        if (startMinutes < o.startMinutes) {
            result = -1;
        } else if (startMinutes > o.startMinutes) {
            result = 1;
        }

        return result;
    }
}
