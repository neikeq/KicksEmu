package com.neikeq.kicksemu.game.characters.upgrade;

import com.neikeq.kicksemu.game.characters.PlayerStats;
import com.neikeq.kicksemu.game.characters.PositionCodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterUpgrade {

    private final Map<Short, PlayerStats> stats;
    private final Map<Short, PlayerStats> autoStats;
    private final Map<Integer, Integer> levelStats;

    private static CharacterUpgrade instance;

    public void defineStats() {
        getStats().put(PositionCodes.ST,
                new PlayerStats(0, 0, 0, 7, 7, 0, -10, 7, 7, -10, 0, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.CF,
                new PlayerStats(0, 0, 7, 0, 7, 0, -10, 0, 7, -10, 0, 7, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.WF,
                new PlayerStats(0, 7, 0, 0, 7, 0, -10, 0, 7, -10, 7, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.AMF,
                new PlayerStats(0, 0, 7, 0, 7, 0, -10, 0, 0, 7, 0, 7, -10, 0, 0, 0, 0));
        getStats().put(PositionCodes.SMF,
                new PlayerStats(0, 7, 0, 0, 7, 0, -10, -10, 0, 7, 7, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.CMF,
                new PlayerStats(0, 0, 0, 7, 7, 0, -10, -10, 0, 7, 0, 7, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.DMF,
                new PlayerStats(0, 7, 0, 0, 0, 7, 0, 7, -10, -10, 0, 0, 0, 7, 0, 0, 0));
        getStats().put(PositionCodes.SB,
                new PlayerStats(0, 7, 7, 0, 0, 7, 0, -10, -10, 0, 7, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.CB,
                new PlayerStats(0, 7, 0, 0, 0, 7, 7, 7, -10, -10, 0, 0, 0, 0, 0, 0, 0));
        getStats().put(PositionCodes.SW,
                new PlayerStats(0, 7, 0, 0, 0, 0, 7, 0, -10, -10, 0, 0, 7, 7, 0, 0, 0));
    }

    public void defineAutoStats() {
        getAutoStats().put(PositionCodes.FW,
                new PlayerStats(1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(PositionCodes.ST,
                new PlayerStats(1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(PositionCodes.CF,
                new PlayerStats(1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(PositionCodes.WF,
                new PlayerStats(1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(PositionCodes.MF,
                new PlayerStats(1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(PositionCodes.AMF,
                new PlayerStats(1, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(PositionCodes.SMF,
                new PlayerStats(1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(PositionCodes.CMF,
                new PlayerStats(1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(PositionCodes.DMF,
                new PlayerStats(1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(PositionCodes.DF,
                new PlayerStats(1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(PositionCodes.SB,
                new PlayerStats(1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(PositionCodes.CB,
                new PlayerStats(1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(PositionCodes.SW,
                new PlayerStats(1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    }

    public void defineLevelStats() {
        getLevelStats().put(10, 2);
        getLevelStats().put(18, 2);
        getLevelStats().put(20, 2);
        getLevelStats().put(30, 2);
        getLevelStats().put(35, 2);
        getLevelStats().put(40, 2);
        getLevelStats().put(50, 2);
        getLevelStats().put(55, 2);
    }

    private CharacterUpgrade() {
        stats = new HashMap<>();
        autoStats = new HashMap<>();
        levelStats = new HashMap<>();

        defineStats();
        defineAutoStats();
        defineLevelStats();
    }

    public static CharacterUpgrade getInstance() {
        if (instance == null) {
            instance = new CharacterUpgrade();
        }

        return instance;
    }

    public Map<Short, PlayerStats> getStats() {
        return stats;
    }

    public Map<Short, PlayerStats> getAutoStats() {
        return autoStats;
    }

    public Map<Integer, Integer> getLevelStats() {
        return levelStats;
    }

    public short statsPointsForLevel(int level) {
        Integer result = getLevelStats().get(level);

        return result == null ? 1 : result.shortValue();
    }
}
