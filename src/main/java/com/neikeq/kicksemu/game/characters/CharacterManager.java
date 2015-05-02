package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.characters.types.PlayerStats;
import com.neikeq.kicksemu.game.characters.types.Position;
import com.neikeq.kicksemu.game.characters.types.StatsInfo;
import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.table.LevelInfo;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
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
            session.send(MessageBuilder.playerInfo(session.getPlayerId(), (byte) 0, con));
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

        session.send(MessageBuilder.trainingList(trainings, (byte) 0));
    }

    private static void sendSkillList(Session session) {
        int playerId = session.getPlayerId();

        Map<Integer, Skill> items = PlayerInfo.getInventorySkills(session.getPlayerId());

        byte slots = PlayerInfo.getSkillSlots(playerId);

        session.send(MessageBuilder.skillList(items, slots, (byte) 0));
    }

    private static void sendCelebrationList(Session session) {
        Map<Integer, Celebration> items =
                PlayerInfo.getInventoryCelebration(session.getPlayerId());

        session.send(MessageBuilder.celebrationList(items, (byte) 0));
    }

    public static void resetStats(int playerId) {
        short position = PlayerInfo.getPosition(playerId);
        short level = PlayerInfo.getLevel(playerId);
        short branchPosition = Position.trunk(position);

        PlayerStats newStats = new PlayerStats(StatsInfo.getInstance()
                .getCreationStats().get(branchPosition));

        MutableInteger statsPoints = new MutableInteger(10);

        if (level > 18) {
            onPlayerLevelUp(18, 17, branchPosition, newStats, statsPoints);
            onPlayerLevelUp(level, level - 18, position, newStats, statsPoints);
        } else {
            onPlayerLevelUp(level, level - 1, branchPosition, newStats, statsPoints);
        }

        if (level >= 18 && Position.isAdvancedPosition(position)) {
            // Apply upgrade stats
            PlayerStats.sumStats(StatsInfo.getInstance().getUpgradeStats().get(position),
                    1, newStats, statsPoints);
        }

        try (Connection con = MySqlManager.getConnection()) {
            // Set new stats
            PlayerInfo.setStats(newStats, playerId, con);

            // Set stats point
            PlayerInfo.setStatsPoints((short)statsPoints.get(), playerId, con);
        } catch (SQLException ignored) {}
    }

    public static short checkExperience(int playerId, short level,
                                        int experience, Connection ... con) {
        short levels = 0;

        LevelInfo newLevelInfo = TableManager.getLevelInfo(li ->
                li.getLevel() > level && li.getExperience() <= experience);

        if (newLevelInfo != null) {
            short newLevel = newLevelInfo.getLevel();

            if (newLevel > level) {
                levels = (short)(newLevel - level);
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
        MutableInteger statsPoints = new MutableInteger(0);

        for (int i = from; i < level; i++) {
            statsPoints.add(StatsInfo.getInstance().statsPointsForLevel(i + 1));
        }

        PlayerStats stats = PlayerInfo.getStats(id, con);

        // Add auto stats
        PlayerStats.sumStats(StatsInfo.getInstance().getAutoStats().get(position),
                levels, stats, statsPoints);

        // Set new stats
        PlayerInfo.setStats(stats, id, con);

        // Add stats point
        PlayerInfo.sumStatsPoints((short) statsPoints.get(), id, con);
    }

    private static void onPlayerLevelUp(int level, int levels, short position,
                                        PlayerStats stats, MutableInteger statPoints) {
        int from = level - levels;

        // Calculate stats points to add
        MutableInteger statsPoints = new MutableInteger(0);

        for (int i = from; i < level; i++) {
            statsPoints.add(StatsInfo.getInstance().statsPointsForLevel(i + 1));
        }

        // Add auto stats
        PlayerStats.sumStats(StatsInfo.getInstance().getAutoStats().get(position),
                levels, stats, statsPoints);

        statPoints.add(statsPoints);
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
                result = (byte) 254; // Invalid value
                break;
            }
        }

        try (Connection con = MySqlManager.getConnection()) {
            int playerId = session.getPlayerId();

            // If all values are valid
            if (result == 0) {
                short statsPoints = PlayerInfo.getStatsPoints(playerId, con);

                MutableInteger remain = new MutableInteger(0);
                PlayerStats stats = PlayerInfo.getStats(playerId, con);

                // If player have enough points
                if (total <= statsPoints) {
                    PlayerStats.sumStats(PlayerStats.fromArray(values), 1, stats, remain);

                    PlayerInfo.setStats(stats, playerId, con);
                    PlayerInfo.sumStatsPoints((short) -(total - remain.get()), playerId, con);
                } else {
                    result = (byte) 253; // Not enough stats points
                }
            }

            session.sendAndFlush(MessageBuilder.addStatsPoints(playerId, result, con));

            if (session.getRoomId() > 0) {
                Room room = RoomManager.getRoomById(session.getRoomId());

                if (room != null) {
                    room.sendBroadcast(MessageBuilder.updateRoomPlayer(session.getPlayerId(), con));
                }
            }
        } catch (SQLException ignored) {}
    }
}
