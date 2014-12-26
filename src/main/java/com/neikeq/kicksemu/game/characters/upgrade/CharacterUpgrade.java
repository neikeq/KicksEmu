package com.neikeq.kicksemu.game.characters.upgrade;

import com.neikeq.kicksemu.game.characters.PlayerStats;
import com.neikeq.kicksemu.game.characters.PositionCodes;

import java.util.HashMap;
import java.util.Map;

public class CharacterUpgrade {

    private final Map<Short, PlayerStats> stats;

    private static CharacterUpgrade instance;

    public static CharacterUpgrade getInstance() {
        if (instance == null) {
            instance = new CharacterUpgrade();
        }

        return instance;
    }

    private CharacterUpgrade() {
        stats = new HashMap<>();
        getStats().put(PositionCodes.ST,
                new PlayerStats(0, 0, 0, 7, 7, 0, -10, 7, 7, -10, 0, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.CF,
                new PlayerStats(0, 0, 0, 0, 7, 0, -10, 0, 7, -10, 0, 7, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.WF,
                new PlayerStats(0, 7, 7, 0, 7, 0, -10, 0, 7, -10, 7, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.AMF,
                new PlayerStats(0, 0, 7, 0, 7, 0, -10, 0, 0, 7, 0, 7, -10, 0, 0, 0, 0));
        getStats().put(PositionCodes.SMF,
                new PlayerStats(0, 0, 7, 0, 7, 0, -10, 0, 7, 7, 0, 7, -10, 0, 0, 0, 0));
        getStats().put(PositionCodes.CMF,
                new PlayerStats(0, 7, 0, 0, 7, 0, -10, -10, 7, 7, 7, 7, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.DMF,
                new PlayerStats(0, 7, 0, 0, 0, 7, 0, 7, -10, -10, 0, 0, 0, 7, 0, 0, 0));
        getStats().put(PositionCodes.SB,
                new PlayerStats(0, 7, 7, 0, 0, 7, 0, -10, -10, 0, 7, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.CB,
                new PlayerStats(0, 7, 0, 0, 0, 7, 7, 7, -10, -10, 0, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.SW,
                new PlayerStats(0, 7, 0, 0, 0, 0, 7, 0, -10, -10, 0, 0, 7, 7, 0, 0, 0));
    }

    public Map<Short, PlayerStats> getStats() {
        return stats;
    }
}
