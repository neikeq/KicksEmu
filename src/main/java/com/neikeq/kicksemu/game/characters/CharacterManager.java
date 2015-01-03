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

    public static void onPlayerLevelUp(int playerId, short level, short levels) {
        short position = PlayerInfo.getPosition(playerId);
        int from = level - levels;

        // Calculate stats points to add
        short statsPoints = 0;

        for (int i = from; i < level; i++) {
            statsPoints += CharacterUpgrade.getInstance().statsPointsForLevel(++i);
        }

        // Add stats point
        PlayerInfo.setStatsPoints(statsPoints, playerId);

        // Add auto stats
        PlayerStats autoStats = CharacterUpgrade.getInstance().getAutoStats().get(position);

        PlayerInfo.setStatsRunning((short)(autoStats.getRunning() * levels), playerId);
        PlayerInfo.setStatsEndurance((short)(autoStats.getEndurance() * levels), playerId);
        PlayerInfo.setStatsAgility((short)(autoStats.getAgility() * levels), playerId);
        PlayerInfo.setStatsBallControl((short)(autoStats.getBallControl() * levels), playerId);
        PlayerInfo.setStatsDribbling((short)(autoStats.getDribbling() * levels), playerId);
        PlayerInfo.setStatsStealing((short)(autoStats.getStealing() * levels), playerId);
        PlayerInfo.setStatsTackling((short)(autoStats.getTackling() * levels), playerId);
        PlayerInfo.setStatsHeading((short)(autoStats.getHeading() * levels), playerId);
        PlayerInfo.setStatsShortShots((short)(autoStats.getShortShots() * levels), playerId);
        PlayerInfo.setStatsLongShots((short)(autoStats.getLongShots() * levels), playerId);
        PlayerInfo.setStatsCrossing((short)(autoStats.getCrossing() * levels), playerId);
        PlayerInfo.setStatsShortPasses((short)(autoStats.getShortPasses() * levels), playerId);
        PlayerInfo.setStatsLongPasses((short)(autoStats.getLongPasses() * levels), playerId);
        PlayerInfo.setStatsMarking((short)(autoStats.getMarking() * levels), playerId);
        PlayerInfo.setStatsGoalkeeping((short)(autoStats.getGoalkeeping() * levels), playerId);
        PlayerInfo.setStatsPunching((short)(autoStats.getPunching() * levels), playerId);
        PlayerInfo.setStatsDefense((short)(autoStats.getDefense() * levels), playerId);
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

                // TODO Avoid adding stats on stats with 100 points or more

                // If player have enough points
                if (total <= statsPoints) {
                    PlayerInfo.setStatsPoints((short) -total, playerId);

                    PlayerInfo.setStatsRunning(values[0], playerId);
                    PlayerInfo.setStatsEndurance(values[1], playerId);
                    PlayerInfo.setStatsAgility(values[2], playerId);
                    PlayerInfo.setStatsBallControl(values[3], playerId);
                    PlayerInfo.setStatsDribbling(values[4], playerId);
                    PlayerInfo.setStatsStealing(values[5], playerId);
                    PlayerInfo.setStatsTackling(values[6], playerId);
                    PlayerInfo.setStatsHeading(values[7], playerId);
                    PlayerInfo.setStatsShortShots(values[8], playerId);
                    PlayerInfo.setStatsLongShots(values[9], playerId);
                    PlayerInfo.setStatsCrossing(values[10], playerId);
                    PlayerInfo.setStatsShortPasses(values[11], playerId);
                    PlayerInfo.setStatsLongPasses(values[12], playerId);
                    PlayerInfo.setStatsMarking(values[13], playerId);
                    PlayerInfo.setStatsGoalkeeping(values[14], playerId);
                    PlayerInfo.setStatsPunching(values[15], playerId);
                    PlayerInfo.setStatsDefense(values[16], playerId);
                } else {
                    result = (byte) 253; // Not enough stats points
                }
            }

            session.sendAndFlush(MessageBuilder.addStatsPoints(playerId, result));
        }
    }
}
