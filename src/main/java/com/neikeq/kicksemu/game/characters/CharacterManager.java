package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.characters.upgrade.CharacterUpgrade;
import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.sessions.Session;
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
        PlayerInfo.setStatsRunning(autoStats.getEndurance(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getAgility(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getBallControl(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getDribbling(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getStealing(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getTackling(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getHeading(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getShortShots(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getLongShots(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getCrossing(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getShortPasses(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getLongPasses(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getMarking(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getGoalkeeping(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getPunching(), playerId);
        PlayerInfo.setStatsRunning(autoStats.getDefense(), playerId);
    }
}
