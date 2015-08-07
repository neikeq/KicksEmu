package com.neikeq.kicksemu.game.events;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import static org.quartz.DateBuilder.*;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class GoldenTimeEvent implements GameEvent {

    private final JobDetail job = newJob(GoldenTimeJob.class)
            .withIdentity("goldenTimeTimeout", "goldenTime")
            .build();

    private final Trigger trigger;

    @Override
    public String getName() {
        return "GOLDEN_TIME_TIMEOUT";
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

    public GoldenTimeEvent(int interval) {
        trigger = newTrigger()
                .withIdentity("goldenTimeTimeout", "goldenTime")
                .startAt(futureDate(interval, IntervalUnit.MINUTE))
                .build();
    }
}
