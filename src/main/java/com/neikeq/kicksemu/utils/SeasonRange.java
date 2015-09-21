package com.neikeq.kicksemu.utils;

import java.util.Date;

public class SeasonRange {

    private final int startMonth;
    private final int startDay;
    private final int endMonth;
    private final int endDay;

    public boolean isWithinRange(Date date) {
        int month = DateUtils.getDateMonth(date);
        int day = DateUtils.getDateDayOfMonth(date);

        if (month == startMonth) {
            if (startMonth == endMonth) {
                    return day >= startDay && day <= endDay;
            } else {
                return day >= startDay;
            }
        } else if (month == endMonth) {
            return day <= endDay;
        } else {
            return month > startMonth && month < endMonth;
        }
    }

    public SeasonRange(Date start, Date end) {
        startMonth = DateUtils.getDateMonth(start);
        startDay = DateUtils.getDateDayOfMonth(start);
        endMonth = DateUtils.getDateMonth(end);
        endDay = DateUtils.getDateDayOfMonth(end);
    }
}
