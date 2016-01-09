package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.game.rooms.ChallengeRoom;
import com.neikeq.kicksemu.game.rooms.ClubRoom;
import com.neikeq.kicksemu.game.rooms.challenges.Challenge;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;

import java.util.Optional;

public class ClubPlayerRewards extends PlayerRewards {

    private static final int BASE_LEVEL_GAP_POINTS = 4;

    private final Optional<ClubRoom> maybeClubRoom;
    private final Optional<ClubRoom> rivalRoom;

    public ClubPlayerRewards(MatchResultHandler resultHandler, PlayerResult playerResult) {
        super(resultHandler, playerResult);

        int playerId = playerResult.getPlayerId();

        Challenge challenge = ((ChallengeRoom) room()).getChallenge();
        maybeClubRoom = challenge.getClubRoomForTeam(room().getPlayerTeam(playerId));
        rivalRoom = challenge.getClubRoomForTeam(room().getPlayerRivalTeam(playerId));
    }

    @Override
    protected void applyRewardExtras() {
        applyLevelGapPoints();
        super.applyRewardExtras();
        calculateWinStreakBonuses();
    }

    private void applyLevelGapPoints() {
        byte levelGap = maybeClubRoom.map(clubRoom ->
                clubRoom.getLevelGapDifferenceTo(rivalRoom)).orElse((byte) 0);
        int levelGapPoints = BASE_LEVEL_GAP_POINTS + levelGap;

        TeamResult teamResult = (playerTeam == RoomTeam.RED) ?
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

        points.add(levelGapPoints);
    }

    private void calculateWinStreakBonuses() {
        int percentage = maybeClubRoom.map(clubRoom -> {

            if (clubRoom.getWinStreak() > 0) {
                if (clubRoom.getWinStreak() >= 15) {
                    return 60;
                } else if (clubRoom.getWinStreak() >= 10) {
                    return 50;
                } else if (clubRoom.getWinStreak() >= 5) {
                    return 40;
                } else if (clubRoom.getWinStreak() == 4) {
                    return 30;
                } else if (clubRoom.getWinStreak() == 3) {
                    return 20;
                } else if (clubRoom.getWinStreak() == 2) {
                    return 10;
                }
            }

            return 0;
        }).orElse(0);

        if (percentage > 0) {
            experience.add(experience.getValue() + (onePercentOfBaseReward * percentage));
        }
    }
}
