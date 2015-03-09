package com.neikeq.kicksemu.game.characters.upgrade;

import com.neikeq.kicksemu.game.characters.PlayerStats;
import com.neikeq.kicksemu.game.characters.Position;

import java.util.HashMap;
import java.util.Map;

public class CharacterUpgrade {

    private final Map<Short, PlayerStats> creationStats;
    private final Map<Short, PlayerStats> upgradeStats;
    private final Map<Short, PlayerStats> autoStats;
    private final Map<Integer, Integer> levelStats;

    private static CharacterUpgrade instance;

    public void defineCreationStats() {
        getCreationStats().put(Position.FW,
                new PlayerStats(40, 30, 30, 30, 30, 20, 15, 35, 30, 20, 55, 60, 55, 30, 0, 0, 0));
        getCreationStats().put(Position.MF,
                new PlayerStats(40, 35, 30, 25, 30, 25, 20, 20, 15, 30, 60, 65, 55, 30, 0, 0, 0));
        getCreationStats().put(Position.DF,
                new PlayerStats(40, 30, 30, 25, 25, 30, 30, 35, 15, 15, 50, 60, 65, 30, 0, 0, 0));
    }

    public void defineUpgradeStats() {
        getUpgradeStats().put(Position.ST,
                new PlayerStats(0, 0, 0, 7, 7, 0, -10, 7, 7, -10, 0, 0, 0, 0, 0, 0, 0));
        getUpgradeStats().put(Position.CF,
                new PlayerStats(0, 0, 7, 0, 7, 0, -10, 0, 7, -10, 0, 7, 0, 0, 0, 0, 0));
        getUpgradeStats().put(Position.WF,
                new PlayerStats(0, 7, 0, 0, 7, 0, -10, 0, 7, -10, 7, 0, 0, 0, 0, 0, 0));
        getUpgradeStats().put(Position.AMF,
                new PlayerStats(0, 0, 7, 0, 7, 0, -10, 0, 0, 7, 0, 7, -10, 0, 0, 0, 0));
        getUpgradeStats().put(Position.SMF,
                new PlayerStats(0, 7, 0, 0, 7, 0, -10, -10, 0, 7, 7, 0, 0, 0, 0, 0, 0));
        getUpgradeStats().put(Position.CMF,
                new PlayerStats(0, 0, 0, 7, 7, 0, -10, -10, 0, 7, 0, 7, 0, 0, 0, 0, 0));
        getUpgradeStats().put(Position.DMF,
                new PlayerStats(0, 7, 0, 0, 0, 7, 0, 7, -10, -10, 0, 0, 0, 7, 0, 0, 0));
        getUpgradeStats().put(Position.SB,
                new PlayerStats(0, 7, 7, 0, 0, 7, 0, -10, -10, 0, 7, 0, 0, 0, 0, 0, 0));
        getUpgradeStats().put(Position.CB,
                new PlayerStats(0, 7, 0, 0, 0, 7, 7, 7, -10, -10, 0, 0, 0, 0, 0, 0, 0));
        getUpgradeStats().put(Position.SW,
                new PlayerStats(0, 7, 0, 0, 0, 0, 7, 0, -10, -10, 0, 0, 7, 7, 0, 0, 0));
    }

    public void defineAutoStats() {
        getAutoStats().put(Position.FW,
                new PlayerStats(1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(Position.ST,
                new PlayerStats(1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(Position.CF,
                new PlayerStats(1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(Position.WF,
                new PlayerStats(1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(Position.MF,
                new PlayerStats(1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(Position.AMF,
                new PlayerStats(1, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(Position.SMF,
                new PlayerStats(1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(Position.CMF,
                new PlayerStats(1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(Position.DMF,
                new PlayerStats(1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(Position.DF,
                new PlayerStats(1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(Position.SB,
                new PlayerStats(1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(Position.CB,
                new PlayerStats(1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        getAutoStats().put(Position.SW,
                new PlayerStats(1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    }

    public void defineLevelStats() {
        getLevelStats().put(10, 2);
        getLevelStats().put(20, 2);
        getLevelStats().put(30, 2);
        getLevelStats().put(35, 2);
        getLevelStats().put(40, 2);
        getLevelStats().put(50, 2);
        getLevelStats().put(55, 2);
    }

    public short statsPointsForLevel(int level) {
        Integer result = getLevelStats().get(level);

        return result == null ? 1 : result.shortValue();
    }

    private CharacterUpgrade() {
        creationStats = new HashMap<>();
        upgradeStats = new HashMap<>();
        autoStats = new HashMap<>();
        levelStats = new HashMap<>();

        defineCreationStats();
        defineUpgradeStats();
        defineAutoStats();
        defineLevelStats();
    }

    public static CharacterUpgrade getInstance() {
        if (instance == null) {
            instance = new CharacterUpgrade();
        }

        return instance;
    }

    public Map<Short, PlayerStats> getUpgradeStats() {
        return upgradeStats;
    }

    public Map<Short, PlayerStats> getAutoStats() {
        return autoStats;
    }

    public Map<Integer, Integer> getLevelStats() {
        return levelStats;
    }

    public Map<Short, PlayerStats> getCreationStats() {
        return creationStats;
    }
}
