package com.neikeq.kicksemu.game.events.tournaments;

import com.neikeq.kicksemu.game.chat.ChatUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TournamentJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ChatUtils.broadcastNotice("Tournament starting now...");
    }
}
