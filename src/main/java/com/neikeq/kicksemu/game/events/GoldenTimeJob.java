package com.neikeq.kicksemu.game.events;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

class GoldenTimeJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        GameEvents.cancelCustomGoldenTime();
    }
}
