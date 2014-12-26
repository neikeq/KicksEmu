package com.neikeq.kicksemu.game.characters.upgrade;

import com.neikeq.kicksemu.game.characters.PositionCodes;

import java.util.HashMap;
import java.util.Map;

public class CharacterUpgrade {

    private final Map<Short, StatsFactor> stats;

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
                new StatsFactor(0, 0, 0, 7, 7, 0, -10, 7, 7, -10, 0, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.CF,
                new StatsFactor(0, 0, 0, 0, 7, 0, -10, 0, 7, -10, 0, 7, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.WF,
                new StatsFactor(0, 7, 7, 0, 7, 0, -10, 0, 7, -10, 7, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.AMF,
                new StatsFactor(0, 0, 7, 0, 7, 0, -10, 0, 0, 7, 0, 7, -10, 0, 0, 0, 0));
        getStats().put(PositionCodes.SMF,
                new StatsFactor(0, 0, 7, 0, 7, 0, -10, 0, 7, 7, 0, 7, -10, 0, 0, 0, 0));
        getStats().put(PositionCodes.CMF,
                new StatsFactor(0, 7, 0, 0, 7, 0, -10, -10, 7, 7, 7, 7, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.DMF,
                new StatsFactor(0, 7, 0, 0, 0, 7, 0, 7, -10, -10, 0, 0, 0, 7, 0, 0, 0));
        getStats().put(PositionCodes.SB,
                new StatsFactor(0, 7, 7, 0, 0, 7, 0, -10, -10, 0, 7, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.CB,
                new StatsFactor(0, 7, 0, 0, 0, 7, 7, 7, -10, -10, 0, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.SW,
                new StatsFactor(0, 7, 0, 0, 0, 0, 7, 0, -10, -10, 0, 0, 7, 7, 0, 0, 0));
    }

    public Map<Short, StatsFactor> getStats() {
        return stats;
    }
}
