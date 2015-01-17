package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.rooms.Room;

public class RewardCalculator {

    public static int calculateReward(PlayerResult result, Room room, short countdown) {
        switch (room.getTrainingFactor()) {
            case 0:
                if (Configuration.getBoolean("game.rewards.practice") && countdown <= 0) {
                    return (int)(12 * (float)(result.getVotePoints() / 10));
                }
                break;
            case 6:
                return (int)(12 * (float)(result.getVotePoints() / 10));
            case 8:
                return (int)(18 * (float)(result.getVotePoints() / 10));
            case 10:
                return (int)(24 * (float)(result.getVotePoints() / 10));
            default:
        }

        return 0;
    }
}
