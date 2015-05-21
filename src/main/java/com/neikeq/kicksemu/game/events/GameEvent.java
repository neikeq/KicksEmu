package com.neikeq.kicksemu.game.events;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public interface GameEvent {

    String getName();
    JobDetail getJob();
    Trigger getTrigger();
    boolean isUsable();
}
