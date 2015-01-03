package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.characters.upgrade.CharacterUpgrade;
import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class CharacterManager {

    public static void playerInfo(Session session) {
        sendItemList(session);
        sendTrainingList(session);
        sendSkillList(session);
        sendCelebrationList(session);
        sendPlayerInfo(session);
    }

    private static void sendPlayerInfo(Session session) {
        ServerMessage msg = MessageBuilder.playerInfo(session.getPlayerId(), (byte) 0);
        session.send(msg);
    }

    private static void sendItemList(Session session) {
        Map<Integer, Item> items = PlayerInfo.getInventoryItems(session.getPlayerId());

        ServerMessage msg = MessageBuilder.itemList(items, (byte) 0);
        session.send(msg);
    }

    private static void sendTrainingList(Session session) {
        Map<Integer, Training> trainings = PlayerInfo.getInventoryTraining(session.getPlayerId());

        ServerMessage msg = MessageBuilder.trainingList(trainings, (byte) 0);
        session.send(msg);
    }

    private static void sendSkillList(Session session) {
        Map<Integer, Skill> items = PlayerInfo.getInventorySkills(session.getPlayerId());

        ServerMessage msg = MessageBuilder.skillList(items, (byte) 0);
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

    public static void checkExperience(int playerId) {
        short level = PlayerInfo.getLevel(playerId);
        short levels = 0;

        String query = "SELECT level FROM levels WHERE experience <= ? AND level > ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, PlayerInfo.getExperience(playerId));
            stmt.setShort(2, level);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    short newLevel = rs.getShort("level");

                    if (newLevel > level) {
                        levels += newLevel - level;
                        level = newLevel;
                    }
                }
            }
        } catch (SQLException ignored) {}

        if (levels > 0) {
            PlayerInfo.setLevel(level, playerId);
            onPlayerLevelUp(playerId, level, levels);
        }
    }

    public static void onPlayerLevelUp(int id, short level, short levels) {
        short position = PlayerInfo.getPosition(id);
        int from = level - levels;

        // Calculate stats points to add
        short statsPoints = 0;

        for (int i = from; i < level; i++) {
            statsPoints += CharacterUpgrade.getInstance().statsPointsForLevel(++i);
        }

        // Add auto stats
        PlayerStats autoStats = CharacterUpgrade.getInstance().getAutoStats().get(position);

        statsPoints += PlayerInfo.setStatsRunning(autoStats.getRunning() * levels, id);
        statsPoints += PlayerInfo.setStatsEndurance(autoStats.getEndurance() * levels, id);
        statsPoints += PlayerInfo.setStatsAgility(autoStats.getAgility() * levels, id);
        statsPoints += PlayerInfo.setStatsBallControl(autoStats.getBallControl() * levels, id);
        statsPoints += PlayerInfo.setStatsDribbling(autoStats.getDribbling() * levels, id);
        statsPoints += PlayerInfo.setStatsStealing(autoStats.getStealing() * levels, id);
        statsPoints += PlayerInfo.setStatsTackling(autoStats.getTackling() * levels, id);
        statsPoints += PlayerInfo.setStatsHeading(autoStats.getHeading() * levels, id);
        statsPoints += PlayerInfo.setStatsShortShots(autoStats.getShortShots() * levels, id);
        statsPoints += PlayerInfo.setStatsLongShots(autoStats.getLongShots() * levels, id);
        statsPoints += PlayerInfo.setStatsCrossing(autoStats.getCrossing() * levels, id);
        statsPoints += PlayerInfo.setStatsShortPasses(autoStats.getShortPasses() * levels, id);
        statsPoints += PlayerInfo.setStatsLongPasses(autoStats.getLongPasses() * levels, id);
        statsPoints += PlayerInfo.setStatsMarking(autoStats.getMarking() * levels, id);
        statsPoints += PlayerInfo.setStatsGoalkeeping(autoStats.getGoalkeeping() * levels, id);
        statsPoints += PlayerInfo.setStatsPunching(autoStats.getPunching() * levels, id);
        statsPoints += PlayerInfo.setStatsDefense(autoStats.getDefense() * levels, id);

        // Add stats point
        PlayerInfo.setStatsPoints(statsPoints, id);
    }

    public static void addStatsPoints(Session session, ClientMessage msg) {
        int playerId = msg.readInt();

        if (session.getPlayerId() == playerId) {
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

            // If all values are valid
            if (result == 0) {
                short statsPoints = PlayerInfo.getStatsPoints(playerId);

                // If player have enough points
                if (total <= statsPoints) {
                    total -= PlayerInfo.setStatsRunning(values[0], playerId);
                    total -= PlayerInfo.setStatsEndurance(values[1], playerId);
                    total -= PlayerInfo.setStatsAgility(values[2], playerId);
                    total -= PlayerInfo.setStatsBallControl(values[3], playerId);
                    total -= PlayerInfo.setStatsDribbling(values[4], playerId);
                    total -= PlayerInfo.setStatsStealing(values[5], playerId);
                    total -= PlayerInfo.setStatsTackling(values[6], playerId);
                    total -= PlayerInfo.setStatsHeading(values[7], playerId);
                    total -= PlayerInfo.setStatsShortShots(values[8], playerId);
                    total -= PlayerInfo.setStatsLongShots(values[9], playerId);
                    total -= PlayerInfo.setStatsCrossing(values[10], playerId);
                    total -= PlayerInfo.setStatsShortPasses(values[11], playerId);
                    total -= PlayerInfo.setStatsLongPasses(values[12], playerId);
                    total -= PlayerInfo.setStatsMarking(values[13], playerId);
                    total -= PlayerInfo.setStatsGoalkeeping(values[14], playerId);
                    total -= PlayerInfo.setStatsPunching(values[15], playerId);
                    total -= PlayerInfo.setStatsDefense(values[16], playerId);

                    PlayerInfo.setStatsPoints((short) -total, playerId);
                } else {
                    result = (byte) 253; // Not enough stats points
                }
            }

            session.sendAndFlush(MessageBuilder.addStatsPoints(playerId, result));
        }
    }
}
