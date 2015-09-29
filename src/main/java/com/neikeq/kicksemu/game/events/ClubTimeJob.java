package com.neikeq.kicksemu.game.events;

import com.neikeq.kicksemu.utils.GameEvents;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

class ClubTimeJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        GameEvents.cancelCustomClubTime();
    }
}
