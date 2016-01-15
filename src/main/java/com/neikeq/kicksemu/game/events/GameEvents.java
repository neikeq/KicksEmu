package com.neikeq.kicksemu.game.events;

import com.neikeq.kicksemu.config.Constants;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.utils.DayTimeRange;
import com.neikeq.kicksemu.utils.table.Row;
import com.neikeq.kicksemu.utils.table.TableReader;
import org.quartz.SchedulerException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

public class GameEvents {

    private static final int MAX_SCHEDULES_PER_DAY = 2;

    private static boolean customGoldenTime;
    private static boolean customClubTime;

    private static final Map<Integer, SortedSet<DayTimeRange>> goldenTimeSchedule = new HashMap<>();
    private static final Map<Integer, SortedSet<DayTimeRange>> clubTimeSchedule = new HashMap<>();

    public static void initialize() {
        initializeSchedule(goldenTimeSchedule,
                TableManager.getTablePath(Constants.PROPERTY_TABLE_GOLDEN_TIME));
        initializeSchedule(clubTimeSchedule,
                TableManager.getTablePath(Constants.PROPERTY_TABLE_CLUB_TIME));
    }

    private static void initializeSchedule(Map<Integer, SortedSet<DayTimeRange>> schedule,
                                           String path) {
        TableReader reader = new TableReader(path);

        Optional<Row> maybeRow;
        while ((maybeRow = reader.nextRow()).isPresent()) {
            Row row = maybeRow.get();

            row.nextColumn().ifPresent(strDay -> {
                try {
                    int dayOfWeek = DateUtils.stringToDayOfWeek(strDay);

                    if (!schedule.containsKey(dayOfWeek)) {
                        schedule.put(dayOfWeek, new TreeSet<>());
                    }

                    SortedSet<DayTimeRange> schedules = schedule.get(dayOfWeek);

                    for (int i = 0; (i < MAX_SCHEDULES_PER_DAY) && row.hasNext(); i++) {
                        // Format: HH:mm/HH:mm
                        row.nextColumn().filter(s -> !s.isEmpty()).ifPresent(strSchedule -> {
                            try {
                                String[] range = strSchedule.split("/");
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                                Date start = timeFormat.parse(range[0]);
                                Date end = timeFormat.parse(range[1]);
                                schedules.add(new DayTimeRange(start, end));
                            } catch (IndexOutOfBoundsException | ParseException e) {
                                Output.println("Invalid schedule format at " + path + ": " +
                                        row, Level.WARNING);
                            }
                        });
                    }
                } catch (IllegalArgumentException e) {
                    Output.println("Skipping schedule row: " + e.getMessage(), Level.WARNING);
                }
            });
        }
    }

    public static void setCustomGoldenTime(int durationMinutes) throws SchedulerException {
        GoldenTimeEvent customGoldenTimeEvent = new GoldenTimeEvent(durationMinutes);

        if (durationMinutes > 0) {
            EventsManager.scheduleEvent(customGoldenTimeEvent);
            customGoldenTime = true;
        } else {
            EventsManager.cancelEvent(customGoldenTimeEvent.getJob().getKey());
            cancelCustomGoldenTime();
        }
    }

    public static void cancelCustomGoldenTime() {
        customGoldenTime = false;
    }

    public static void setCustomClubTime(int durationMinutes) throws SchedulerException {
        ClubTimeEvent customClubTimeEvent = new ClubTimeEvent(durationMinutes);

        if (durationMinutes > 0) {
            EventsManager.scheduleEvent(customClubTimeEvent);
            customClubTime = true;
        } else {
            EventsManager.cancelEvent(customClubTimeEvent.getJob().getKey());
            cancelCustomClubTime();
        }
    }

    public static void cancelCustomClubTime() {
        customClubTime = false;
    }

    public static boolean isGoldenTime() {
        if (customGoldenTime) {
            return true;
        }

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        int day = c.get(Calendar.DAY_OF_WEEK);

        SortedSet<DayTimeRange> daySchedule = goldenTimeSchedule.get(day);

        if (daySchedule != null) {
            Date currentTime = c.getTime();
            return daySchedule.stream().anyMatch(dayTimeRange ->
                    dayTimeRange.isWithinRange(currentTime));
        }

        return false;
    }

    public static boolean isClubTime() {
        if (customClubTime) {
            return true;
        }

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        int day = c.get(Calendar.DAY_OF_WEEK);

        SortedSet<DayTimeRange> daySchedule = clubTimeSchedule.get(day);

        if (daySchedule != null) {
            Date currentTime = c.getTime();
            return daySchedule.stream().anyMatch(dayTimeRange ->
                    dayTimeRange.isWithinRange(currentTime));
        }

        return false;
    }

    public static int remainMinutesForNextGoldenTime() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        int day = c.get(Calendar.DAY_OF_WEEK);
        return remainMinutesForNextSchedule(goldenTimeSchedule.get(day), c);
    }

    public static int remainMinutesForNextClubTime() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        int day = c.get(Calendar.DAY_OF_WEEK);
        return remainMinutesForNextSchedule(clubTimeSchedule.get(day), c);
    }

    private static int remainMinutesForNextSchedule(SortedSet<DayTimeRange> schedule, Calendar c) {
        if (schedule != null) {
            Date currentTime = c.getTime();
            Optional<DayTimeRange> optional = schedule.stream()
                    .filter(dayTimeRange -> dayTimeRange.isAfterRange(currentTime))
                    .findFirst();

            if (optional.isPresent()) {
                return optional.get().minutesFrom(currentTime);
            }
        }

        return -1;
    }
}
