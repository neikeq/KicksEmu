package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.game.rooms.ChallengeRoom;
import com.neikeq.kicksemu.game.rooms.ClubRoom;
import com.neikeq.kicksemu.game.rooms.challenges.Challenge;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;

public class ClubPlayerRewards extends PlayerRewards {

    private final ClubRoom clubRoom;

    public ClubPlayerRewards(MatchResultHandler resultHandler, PlayerResult playerResult) {
        super(resultHandler, playerResult);

        Challenge challenge = ((ChallengeRoom) room()).getChallenge();
        clubRoom = room().getPlayerTeam(playerResult.getPlayerId()) == RoomTeam.RED ?
                challenge.getRedTeam() : challenge.getBlueTeam();
    }

    @Override
    protected void applyRewardExtras() {
        super.applyRewardExtras();
        calculateWinStreakBonuses();
    }

    private void calculateWinStreakBonuses() {
        int percentage = 0;

        if (clubRoom.getWinStreak() > 0) {
            if (clubRoom.getWinStreak() >= 15) {
                percentage = 60;
            } else if (clubRoom.getWinStreak() >= 10) {
                percentage = 50;
            } else if (clubRoom.getWinStreak() >= 5) {
                percentage = 40;
            } else if (clubRoom.getWinStreak() == 4) {
                percentage = 30;
            } else if (clubRoom.getWinStreak() == 3) {
                percentage = 20;
            } else if (clubRoom.getWinStreak() == 2) {
                percentage = 10;
            }
        }

        if (percentage > 0) {
            experience.sum(experience.get() + onePercentOfBaseReward * percentage);
        }
    }
}
