package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.rooms.Room;

public class RewardCalculator {

    public static int calculateReward(PlayerResult result, Room room, short countdown) {
        if (!room.isTraining()) {
            switch (room.getPlayers().size()) {
                case 6:
                    return (int)(12 * (float)(result.getVotePoints() / 10));
                case 8:
                    return (int)(18 * (float)(result.getVotePoints() / 10));
                case 10:
                    return (int)(24 * (float)(result.getVotePoints() / 10));
                default:
            }
        } else if (Configuration.getBoolean("game.rewards.practice") && countdown <= 0) {
            return (int)(12 * (float)(result.getVotePoints() / 10));
        }

        return 0;
    }
}
