package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.table.LevelInfo;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.storage.MySqlManager;
import com.neikeq.kicksemu.utils.mutable.MutableInteger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class CharacterManager {

    public static void playerInfo(Session session) {
        sendItemList(session);
        sendTrainingList(session);
        sendSkillList(session);
        sendCelebrationList(session);
        sendPlayerInfo(session);
        sendItemsInUse(session);
    }

    public static void playerDetails(Session session, ClientMessage msg) {
        int playerId = msg.readInt();

        if (ServerManager.isPlayerConnected(playerId)) {
            session.send(MessageBuilder.playerDetails(playerId, (byte) 0));
        }
    }

    private static void sendPlayerInfo(Session session) {
        try (Connection con = MySqlManager.getConnection()) {
            ServerMessage msg = MessageBuilder.playerInfo(session.getPlayerId(), (byte) 0, con);
            session.send(msg);
        } catch (SQLException ignored) {}
    }

    public static void sendItemList(Session session) {
        Map<Integer, Item> items = PlayerInfo.getInventoryItems(session.getPlayerId());

        session.send(MessageBuilder.itemList(items, (byte) 0));
    }

    /** This is a trick to update client's inventory items in use. */
    public static void sendItemsInUse(Session session) {
        int playerId = session.getPlayerId();

        Map<Integer, Item> items = PlayerInfo.getInventoryItems(session.getPlayerId());

        if (items.size() > 0) {
            Item item = items.values().iterator().next();

            session.send(item.isSelected() ?
                    MessageBuilder.activateItem(item.getInventoryId(), playerId, (byte) 0) :
                    MessageBuilder.deactivateItem(item.getInventoryId(), playerId, (byte) 0));
        }
    }

    private static void sendTrainingList(Session session) {
        Map<Integer, Training> trainings = PlayerInfo.getInventoryTraining(session.getPlayerId());

        ServerMessage msg = MessageBuilder.trainingList(trainings, (byte) 0);
        session.send(msg);
    }

    private static void sendSkillList(Session session) {
        int playerId = session.getPlayerId();

        Map<Integer, Skill> items = PlayerInfo.getInventorySkills(session.getPlayerId());

        byte slots = PlayerInfo.getSkillSlots(playerId);

        ServerMessage msg = MessageBuilder.skillList(items, slots, (byte) 0);
        session.send(msg);
    }

    private static void sendCelebrationList(Session session) {
        Map<Integer, Celebration> items =
                PlayerInfo.getInventoryCelebration(session.getPlayerId());

        ServerMessage msg = MessageBuilder.celebrationList(items, (byte) 0);
        session.send(msg);
    }

    public static void gameExit(Session session) {
        int playerId = session.getPlayerId();

        ServerMessage response = MessageBuilder.gameExit(session.getRemoteAddress(), playerId);
        session.send(response);

        session.close();
    }

    public static void resetStats(int playerId) {
        short position = PlayerInfo.getPosition(playerId);
        short level = PlayerInfo.getLevel(playerId);
        short branchPosition = Position.trunk(position);

        PlayerStats newStats = new PlayerStats(StatsInfo.getInstance()
                .getCreationStats().get(branchPosition));

        MutableInteger statsPoints = new MutableInteger(10);

        try (Connection con = MySqlManager.getConnection()) {
            if (level > 18) {
                onPlayerLevelUp(18, 17, branchPosition, newStats, statsPoints);
                onPlayerLevelUp(level, level - 18, position, newStats, statsPoints);
            } else {
                onPlayerLevelUp(level, level - 1, branchPosition, newStats, statsPoints);
            }

            if (level >= 18) {
                // Apply upgrade stats
                PlayerStats upgradeStats = StatsInfo.getInstance()
                        .getUpgradeStats().get(position);

                newStats.sumRunning(sumStatsUpToHundred(upgradeStats.getRunning(),
                        newStats.getRunning(), statsPoints));
                newStats.sumEndurance(sumStatsUpToHundred(upgradeStats.getEndurance(),
                        newStats.getEndurance(), statsPoints));
                newStats.sumAgility(sumStatsUpToHundred(upgradeStats.getAgility(),
                        newStats.getAgility(), statsPoints));
                newStats.sumBallControl(sumStatsUpToHundred(upgradeStats.getBallControl(),
                        newStats.getBallControl(), statsPoints));
                newStats.sumDribbling(sumStatsUpToHundred(upgradeStats.getDribbling(),
                        newStats.getDribbling(), statsPoints));
                newStats.sumStealing(sumStatsUpToHundred(upgradeStats.getStealing(),
                        newStats.getStealing(), statsPoints));
                newStats.sumTackling(sumStatsUpToHundred(upgradeStats.getTackling(),
                        newStats.getTackling(), statsPoints));
                newStats.sumHeading(sumStatsUpToHundred(upgradeStats.getHeading(),
                        newStats.getHeading(), statsPoints));
                newStats.sumShortShots(sumStatsUpToHundred(upgradeStats.getShortShots(),
                        newStats.getShortShots(), statsPoints));
                newStats.sumLongShots(sumStatsUpToHundred(upgradeStats.getLongShots(),
                        newStats.getLongShots(), statsPoints));
                newStats.sumCrossing(sumStatsUpToHundred(upgradeStats.getCrossing(),
                        newStats.getCrossing(), statsPoints));
                newStats.sumShortPasses(sumStatsUpToHundred(upgradeStats.getShortPasses(),
                        newStats.getShortPasses(), statsPoints));
                newStats.sumLongShots(sumStatsUpToHundred(upgradeStats.getLongPasses(),
                        newStats.getLongPasses(), statsPoints));
                newStats.sumMarking(sumStatsUpToHundred(upgradeStats.getMarking(),
                        newStats.getMarking(), statsPoints));
                newStats.sumGoalkeeping(sumStatsUpToHundred(upgradeStats.getGoalkeeping(),
                        newStats.getGoalkeeping(), statsPoints));
                newStats.sumPunching(sumStatsUpToHundred(upgradeStats.getPunching(),
                        newStats.getPunching(), statsPoints));
                newStats.sumDefense(sumStatsUpToHundred(upgradeStats.getDefense(),
                        newStats.getDefense(), statsPoints));
            }

            // Set new stats
            PlayerInfo.setStats(newStats, playerId, con);

            // Set stats point
            PlayerInfo.setStatsPoints((short)statsPoints.get(), playerId, con);
        } catch (SQLException ignored) {}
    }

    public static short checkExperience(int playerId, Connection ... con) {
        short levels = 0;
        final short level = PlayerInfo.getLevel(playerId, con);
        final int experience = PlayerInfo.getExperience(playerId, con);

        LevelInfo newLevelInfo = TableManager.getLevelInfo(li ->
                li.getLevel() > level && li.getExperience() <= experience);

        if (newLevelInfo != null) {
            short newLevel = newLevelInfo.getLevel();

            if (newLevel > level) {
                levels += newLevel - level;
            }

            if (levels > 0) {
                PlayerInfo.setLevel(newLevel, playerId, con);

                short position = PlayerInfo.getPosition(playerId, con);
                onPlayerLevelUp(playerId, newLevel, levels, position, con);
            }
        }

        return levels;
    }

    private static void onPlayerLevelUp(int id, short level, short levels,
                                       short position, Connection ... con) {
        int from = level - levels;

        // Calculate stats points to add
        short statsPoints = 0;

        for (int i = from; i < level; i++) {
            statsPoints += StatsInfo.getInstance().statsPointsForLevel(i+1);
        }

        // Add auto stats
        PlayerStats autoStats = StatsInfo.getInstance().getAutoStats().get(position);

        statsPoints += PlayerInfo.sumStatsRunning(autoStats.getRunning() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsEndurance(autoStats.getEndurance() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsAgility(autoStats.getAgility() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsBallControl(autoStats.getBallControl() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsDribbling(autoStats.getDribbling() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsStealing(autoStats.getStealing() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsTackling(autoStats.getTackling() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsHeading(autoStats.getHeading() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsShortShots(autoStats.getShortShots() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsLongShots(autoStats.getLongShots() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsCrossing(autoStats.getCrossing() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsShortPasses(autoStats.getShortPasses() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsLongPasses(autoStats.getLongPasses() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsMarking(autoStats.getMarking() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsGoalkeeping(autoStats.getGoalkeeping() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsPunching(autoStats.getPunching() * levels, id, con);
        statsPoints += PlayerInfo.sumStatsDefense(autoStats.getDefense() * levels, id, con);

        // Add stats point
        PlayerInfo.sumStatsPoints(statsPoints, id, con);
    }

    private static void onPlayerLevelUp(int level, int levels, short position,
                                        PlayerStats stats, MutableInteger statPoints) {
        int from = level - levels;

        // Calculate stats points to add
        MutableInteger statsPoints = new MutableInteger(0);

        for (int i = from; i < level; i++) {
            statsPoints.sum(StatsInfo.getInstance().statsPointsForLevel(i + 1));
        }

        // Add auto stats
        PlayerStats autoStats = StatsInfo.getInstance().getAutoStats().get(position);

        stats.sumRunning(sumStatsUpToHundred(autoStats.getRunning() * levels,
                stats.getRunning(), statsPoints));
        stats.sumEndurance(sumStatsUpToHundred(autoStats.getEndurance() * levels,
                stats.getEndurance(), statsPoints));
        stats.sumAgility(sumStatsUpToHundred(autoStats.getAgility() * levels,
                stats.getAgility(), statsPoints));
        stats.sumBallControl(sumStatsUpToHundred(autoStats.getBallControl() * levels,
                stats.getBallControl(), statsPoints));
        stats.sumDribbling(sumStatsUpToHundred(autoStats.getDribbling() * levels,
                stats.getDribbling(), statsPoints));
        stats.sumStealing(sumStatsUpToHundred(autoStats.getStealing() * levels,
                stats.getStealing(), statsPoints));
        stats.sumTackling(sumStatsUpToHundred(autoStats.getTackling() * levels,
                stats.getTackling(), statsPoints));
        stats.sumHeading(sumStatsUpToHundred(autoStats.getHeading() * levels,
                stats.getHeading(), statsPoints));
        stats.sumShortShots(sumStatsUpToHundred(autoStats.getShortShots() * levels,
                stats.getShortShots(), statsPoints));
        stats.sumLongShots(sumStatsUpToHundred(autoStats.getLongShots() * levels,
                stats.getLongShots(), statsPoints));
        stats.sumCrossing(sumStatsUpToHundred(autoStats.getCrossing() * levels,
                stats.getCrossing(), statsPoints));
        stats.sumShortPasses(sumStatsUpToHundred(autoStats.getShortPasses() * levels,
                stats.getShortPasses(), statsPoints));
        stats.sumLongShots(sumStatsUpToHundred(autoStats.getLongPasses() * levels,
                stats.getLongPasses(), statsPoints));
        stats.sumMarking(sumStatsUpToHundred(autoStats.getMarking() * levels,
                stats.getMarking(), statsPoints));
        stats.sumGoalkeeping(sumStatsUpToHundred(autoStats.getGoalkeeping() * levels,
                stats.getGoalkeeping(), statsPoints));
        stats.sumPunching(sumStatsUpToHundred(autoStats.getPunching() * levels,
                stats.getPunching(), statsPoints));
        stats.sumDefense(sumStatsUpToHundred(autoStats.getDefense() * levels,
                stats.getDefense(), statsPoints));

        statPoints.sum(statsPoints);
    }

    private static short sumStatsUpToHundred(int value, short current, MutableInteger statsPoints) {
        short add = CharacterUtils.statsUpToHundred(current, value);
        statsPoints.sum(value - add);

        return add;
    }

    public static void addStatsPoints(Session session, ClientMessage msg) {
        msg.readInt();

        byte result = 0;

        short[] values = new short[17];
        short total = 0;

        for (int i = 0; i < values.length; i++) {
            values[i] = msg.readShort();
            total += values[i];

            if (values[i] < 0) {
                result = (byte)254; // Invalid value
                break;
            }
        }

        try (Connection con = MySqlManager.getConnection()) {
            int playerId = session.getPlayerId();

            // If all values are valid
            if (result == 0) {
                short statsPoints = PlayerInfo.getStatsPoints(playerId, con);

                // If player have enough points
                if (total <= statsPoints) {
                    total -= PlayerInfo.sumStatsRunning(values[0], playerId, con);
                    total -= PlayerInfo.sumStatsEndurance(values[1], playerId, con);
                    total -= PlayerInfo.sumStatsAgility(values[2], playerId, con);
                    total -= PlayerInfo.sumStatsBallControl(values[3], playerId, con);
                    total -= PlayerInfo.sumStatsDribbling(values[4], playerId, con);
                    total -= PlayerInfo.sumStatsStealing(values[5], playerId, con);
                    total -= PlayerInfo.sumStatsTackling(values[6], playerId, con);
                    total -= PlayerInfo.sumStatsHeading(values[7], playerId, con);
                    total -= PlayerInfo.sumStatsShortShots(values[8], playerId, con);
                    total -= PlayerInfo.sumStatsLongShots(values[9], playerId, con);
                    total -= PlayerInfo.sumStatsCrossing(values[10], playerId, con);
                    total -= PlayerInfo.sumStatsShortPasses(values[11], playerId, con);
                    total -= PlayerInfo.sumStatsLongPasses(values[12], playerId, con);
                    total -= PlayerInfo.sumStatsMarking(values[13], playerId, con);
                    total -= PlayerInfo.sumStatsGoalkeeping(values[14], playerId, con);
                    total -= PlayerInfo.sumStatsPunching(values[15], playerId, con);
                    total -= PlayerInfo.sumStatsDefense(values[16], playerId, con);

                    PlayerInfo.sumStatsPoints((short) -total, playerId, con);
                } else {
                    result = (byte) 253; // Not enough stats points
                }
            }

            session.sendAndFlush(MessageBuilder.addStatsPoints(playerId, result, con));
        } catch (SQLException ignored) {}
    }
}
