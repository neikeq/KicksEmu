package com.neikeq.kicksemu.game.characters;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class LevelCache {

    private final Map<Integer, Short> levels = new HashMap<>();

    public short getPlayerLevel(int playerId, Connection ... con) {
        Short level = levels.get(playerId);

        if (level == null) {
            level = PlayerInfo.getLevel(playerId, con);
            levels.put(playerId, level);
        }

        return level;
    }
}
