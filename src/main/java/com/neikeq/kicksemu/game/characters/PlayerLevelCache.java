package com.neikeq.kicksemu.game.characters;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class PlayerLevelCache {

    private final Map<Integer, Short> levels;

    public short getPlayerLevel(int playerId, Connection ... con) {
        Short level = levels.get(playerId);

        if (level == null) {
            level = PlayerInfo.getLevel(playerId, con);
            levels.put(playerId, level);
        }

        return level;
    }

    public PlayerLevelCache() {
        levels = new HashMap<>();
    }
}
