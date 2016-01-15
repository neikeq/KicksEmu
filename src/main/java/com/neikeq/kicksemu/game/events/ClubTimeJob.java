package com.neikeq.kicksemu.game.events;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ClubTimeJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        GameEvents.cancelCustomClubTime();
    }
}
