package com.neikeq.kicksemu.game.events;

import com.neikeq.kicksemu.game.events.tournaments.TournamentEvent;
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

    private static void scheduleEvent(GameEvent event) throws SchedulerException {
        scheduler.scheduleJob(event.getJob(), event.getTrigger());
    }
}
