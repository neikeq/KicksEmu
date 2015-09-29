package com.neikeq.kicksemu.game.events;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import static org.quartz.DateBuilder.IntervalUnit;
import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class ClubTimeEvent implements GameEvent {

    private final JobDetail job = newJob(ClubTimeJob.class)
            .withIdentity("clubTimeTimeout", "clubTime")
            .build();

    private final Trigger trigger;

    @Override
    public String getName() {
        return "CLUB_TIME_TIMEOUT";
    }

    @Override
    public JobDetail getJob() {
        return job;
    }

    @Override
    public Trigger getTrigger() {
        return trigger;
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    public ClubTimeEvent(int interval) {
        trigger = newTrigger()
                .withIdentity("clubTimeTimeout", "clubTime")
                .startAt(futureDate(interval, IntervalUnit.MINUTE))
                .build();
    }
}
