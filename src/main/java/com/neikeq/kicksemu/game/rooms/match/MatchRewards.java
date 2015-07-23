package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.characters.types.PlayerHistory;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.rooms.Room;

import java.sql.Connection;

public class MatchRewards {

    public static int calculateReward(PlayerResult result, Room room, short countdown) {
        short votePoints = result.getVotePoints() > 100 ? 100 : result.getVotePoints();

        switch (room.getTrainingFactor()) {
            case -1:
                if (Configuration.getBoolean("game.rewards.practice") &&
                        countdown <= 0 && result.getGoals() >= 3) {
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

        PlayerHistory matchHistory = new PlayerHistory();
        matchHistory.sumMatches(1);

        switch (teamResult.getResult()) {
            case DRAW:
                matchHistory.sumDraws(1);
                break;
            case WIN:
                matchHistory.sumWins(1);
                break;
            default:
        }

        if (playerId == mvp) {
            matchHistory.sumMom(1);
        }

        matchHistory.sumValidGoals(result.getGoals());
        matchHistory.sumValidAssists(result.getAssists());
        matchHistory.sumValidInterception(result.getBlocks());
        matchHistory.sumValidShooting(result.getShots());
        matchHistory.sumValidStealing(result.getSteals());
        matchHistory.sumValidTackling(result.getTackles());
        matchHistory.sumTotalPoints(result.getVotePoints());

        PlayerInfo.sumHistory(matchHistory, playerId, con);
    }
}
