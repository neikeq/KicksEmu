package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.storage.ConnectionRef;

import java.util.HashMap;
import java.util.Map;

public class LevelCache {

    private final Map<Integer, Short> levels = new HashMap<>();

    public short getPlayerLevel(int playerId, ConnectionRef ... con) {
        Short level = levels.get(playerId);

        if (level == null) {
            level = PlayerInfo.getLevel(playerId, con);
            levels.put(playerId, level);
        }

        return level;
    }
}
