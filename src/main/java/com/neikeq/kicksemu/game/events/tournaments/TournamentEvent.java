package com.neikeq.kicksemu.game.events.tournaments;

import com.neikeq.kicksemu.KicksEmu;
import com.neikeq.kicksemu.game.events.GameEvent;

import com.neikeq.kicksemu.game.servers.ServerType;
import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;

public class TournamentEvent implements GameEvent {

    private final JobDetail job = newJob(TournamentJob.class)
            .withIdentity("jobTournament", "groupTournament")
            .build();

    private final Trigger trigger = newTrigger()
            .withIdentity("trigger1", "group1")
            .withSchedule(weeklyOnDayAndHourAndMinute(DateBuilder.SATURDAY, 15, 0))
            .build();

    @Override
    public String getName() {
        return "TOURNAMENT";
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
        return KicksEmu.getServerManager().getServerType() == ServerType.TOURNAMENT;
    }
}
