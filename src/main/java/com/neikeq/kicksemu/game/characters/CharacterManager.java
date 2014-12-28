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
        ServerMessage msg = MessageBuilder.playerInfo(session.getPlayerId(), (byte)0);
        session.send(msg);
    }

    private static void sendItemList(Session session) {
        Map<Integer, Item> items = PlayerInfo.getInventoryItems(session.getPlayerId());

        ServerMessage msg = MessageBuilder.itemList(items, (byte)0);
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
        String query = "SELECT experience FROM levels WHERE level=?";
        short curLevel = PlayerInfo.getLevel(playerId);

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, curLevel + 1);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int experience = rs.getInt("experience");

                    if (PlayerInfo.getExperience(playerId) >= experience) {
                        PlayerInfo.setLevel((short)(curLevel + 1), playerId);
                        onPlayerLevelUp(playerId);
                    }
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void onPlayerLevelUp(int playerId) {
        short position = PlayerInfo.getPosition(playerId);

        // Add a stat point
        PlayerInfo.setStatsPoints((short)1, playerId);

        // Add auto values
        PlayerStats autoStats = CharacterUpgrade.getInstance().getAutoStats().get(position);

        PlayerInfo.setStatsRunning(autoStats.getRunning(), playerId);
        PlayerInfo.setStatsEndurance(autoStats.getEndurance(), playerId);
        PlayerInfo.setStatsAgility(autoStats.getAgility(), playerId);
        PlayerInfo.setStatsBallControl(autoStats.getBallControl(), playerId);
        PlayerInfo.setStatsDribbling(autoStats.getDribbling(), playerId);
        PlayerInfo.setStatsStealing(autoStats.getStealing(), playerId);
        PlayerInfo.setStatsTackling(autoStats.getTackling(), playerId);
        PlayerInfo.setStatsHeading(autoStats.getHeading(), playerId);
        PlayerInfo.setStatsShortShots(autoStats.getShortShots(), playerId);
        PlayerInfo.setStatsLongShots(autoStats.getLongShots(), playerId);
        PlayerInfo.setStatsCrossing(autoStats.getCrossing(), playerId);
        PlayerInfo.setStatsShortPasses(autoStats.getShortPasses(), playerId);
        PlayerInfo.setStatsLongPasses(autoStats.getLongPasses(), playerId);
        PlayerInfo.setStatsMarking(autoStats.getMarking(), playerId);
        PlayerInfo.setStatsGoalkeeping(autoStats.getGoalkeeping(), playerId);
        PlayerInfo.setStatsPunching(autoStats.getPunching(), playerId);
        PlayerInfo.setStatsDefense(autoStats.getDefense(), playerId);
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
