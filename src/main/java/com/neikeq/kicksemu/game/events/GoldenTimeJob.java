package com.neikeq.kicksemu.game.events;

import com.neikeq.kicksemu.utils.GameEvents;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class GoldenTimeJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        GameEvents.cancelCustomGoldenTime();
    }
}
