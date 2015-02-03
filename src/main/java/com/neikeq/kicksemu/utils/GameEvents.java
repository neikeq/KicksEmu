package com.neikeq.kicksemu.utils;

import java.util.Calendar;
import java.util.TimeZone;

public class GameEvents {

    public static boolean isGoldenTime() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        int day = c.get(Calendar.DAY_OF_WEEK);
        int hours = c.get(Calendar.HOUR_OF_DAY);

        switch (day) {
            case Calendar.SUNDAY:
                return (hours >= 16 && hours < 18) || (hours > 20 && hours < 23);
            case Calendar.MONDAY:
                return hours >= 21 && hours < 23;
            case Calendar.TUESDAY:
                return hours >= 20 && hours < 21;
            case Calendar.WEDNESDAY:
                return hours >= 21 && hours < 23;
            case Calendar.FRIDAY:
                return hours >= 20 && hours < 24;
            case Calendar.SATURDAY:
                return hours >= 20 && hours < 24;
            default:
                return false;
        }
    }

    public static boolean isClubTime() {
        // Currently disabled
        return false;
    }
}
