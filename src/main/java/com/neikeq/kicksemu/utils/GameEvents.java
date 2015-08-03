package com.neikeq.kicksemu.utils;

import java.util.Calendar;
import java.util.TimeZone;

public class GameEvents {

    private static long customGoldenTimeStart = 0;
    private static float customGoldenTimeDuration = 0;

    public static void setCustomGoldenTime(float duration) {
        customGoldenTimeDuration = duration;

        if (duration > 0) {
            customGoldenTimeStart = DateUtils.currentTimeMillis();
        }
    }

    public static boolean isGoldenTime() {
        if (customGoldenTimeDuration > 0) {
            // Hours to Milliseconds
            long durationMillis = (long) (customGoldenTimeDuration * 3600000);

            // If the custom golden time is still active
            if (DateUtils.currentTimeMillis() - customGoldenTimeStart < durationMillis) {
                return true;
            } else {
                customGoldenTimeDuration = 0;
            }
        }

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        int day = c.get(Calendar.DAY_OF_WEEK);
        int hours = c.get(Calendar.HOUR_OF_DAY);

        switch (day) {
            case Calendar.SUNDAY:
                return (hours >= 16 && hours < 18) || (hours > 20 && hours < 23);
            case Calendar.MONDAY:
                return hours >= 21 && hours < 23;
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
