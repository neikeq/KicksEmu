package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.rooms.Room;

import java.sql.Connection;

public class RewardCalculator {

    public static int calculateReward(PlayerResult result, Room room, short countdown) {
        short votePoints = result.getVotePoints() > 100 ? 100 : result.getVotePoints();

        switch (room.getTrainingFactor()) {
            case 0:
                if (Configuration.getBoolean("game.rewards.practice") && countdown <= 0) {
                    return (int)(12 * (float)(votePoints / 10));
                }

                return 0;
            case 6:
                return (int)(12 * (float)(votePoints / 10));
            case 8:
                return (int)(18 * (float)(votePoints / 10));
            case 10:
                return (int)(24 * (float)(votePoints / 10));
            default:
                return 0;
        }
    }

    public static void updatePlayerHistory(PlayerResult result, TeamResult teamResult,
                                           int mvp, Connection con) {
        int playerId = result.getPlayerId();

        PlayerInfo.setHistoryMatches(1, playerId, con);

        switch (teamResult.getResult()) {
            case 0:
                PlayerInfo.setHistoryDraws(1, playerId, con);
                break;
            case 1:
                PlayerInfo.setHistoryWins(1, playerId, con);
                break;
            default:
        }

        if (playerId == mvp) {
            PlayerInfo.setHistoryMom(1, playerId, con);
        }

        PlayerInfo.setHistoryValidGoals(result.getGoals(), playerId, con);
        PlayerInfo.setHistoryValidAssists(result.getAssists(), playerId, con);
        PlayerInfo.setHistoryValidInterception(result.getBlocks(), playerId, con);
        PlayerInfo.setHistoryValidShooting(result.getShots(), playerId, con);
        PlayerInfo.setHistoryValidStealing(result.getSteals(), playerId, con);
        PlayerInfo.setHistoryValidTackling(result.getTackles(), playerId, con);
        PlayerInfo.setHistoryTotalPoints(result.getVotePoints(), playerId, con);
    }
}
