package com.neikeq.kicksemu.game.events;

import com.neikeq.kicksemu.game.events.tournaments.TournamentEvent;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class EventsManager {

    private static Scheduler scheduler;

    public static void initialize() throws SchedulerException {
        scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();

        scheduleEvent(new TournamentEvent());
    }

    public static void scheduleEvent(GameEvent event) throws SchedulerException {
        cancelEvent(event.getJob().getKey());

        scheduler.scheduleJob(event.getJob(), event.getTrigger());
    }

    public static void cancelEvent(JobKey jobKey) throws SchedulerException {
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
    }
}
