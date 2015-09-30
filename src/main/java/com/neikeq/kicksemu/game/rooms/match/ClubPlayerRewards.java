package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.game.rooms.ChallengeRoom;
import com.neikeq.kicksemu.game.rooms.ClubRoom;
import com.neikeq.kicksemu.game.rooms.challenges.Challenge;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;

public class ClubPlayerRewards extends PlayerRewards {

    private static final int BASE_LEVEL_GAP_POINTS = 4;

    private final ClubRoom clubRoom;
    private final ClubRoom rivalRoom;

    public ClubPlayerRewards(MatchResultHandler resultHandler, PlayerResult playerResult) {
        super(resultHandler, playerResult);

        int playerId = playerResult.getPlayerId();

        Challenge challenge = ((ChallengeRoom) room()).getChallenge();
        clubRoom = room().getPlayerTeam(playerId) == RoomTeam.RED ?
                challenge.getRedTeam() : challenge.getBlueTeam();
        rivalRoom = room().getPlayerTeam(playerId) == RoomTeam.BLUE ?
                challenge.getRedTeam() : challenge.getBlueTeam();
    }

    @Override
    protected void applyRewardExtras() {
        applyLevelGapPoints();
        super.applyRewardExtras();
        calculateWinStreakBonuses();
    }

    private void applyLevelGapPoints() {
        byte levelGap = clubRoom.getLevelGapFactorTo(rivalRoom);
        int levelGapPoints = BASE_LEVEL_GAP_POINTS + levelGap;

        TeamResult teamResult = playerTeam == RoomTeam.RED ?
                matchResult().getRedTeam() : matchResult().getBlueTeam();
        switch (teamResult.getResult()) {
            case WIN:
                levelGapPoints += 6;
                break;
            case DRAW:
                levelGapPoints += 3;
                break;
            default:
        }

        points.sum(levelGapPoints);
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
