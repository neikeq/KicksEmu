package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.characters.types.PlayerStats;
import com.neikeq.kicksemu.game.characters.types.Position;
import com.neikeq.kicksemu.game.characters.types.StatsInfo;
import com.neikeq.kicksemu.game.inventory.products.Celebration;
import com.neikeq.kicksemu.game.inventory.products.Item;
import com.neikeq.kicksemu.game.inventory.products.Skill;
import com.neikeq.kicksemu.game.inventory.products.Training;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.storage.ConnectionRef;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableShort;

import java.sql.SQLException;
import java.util.Map;

public class CharacterManager {

    public static void playerInfo(Session session) {
        sendItemList(session);
        sendTrainingList(session);
        sendSkillList(session);
        sendCelebrationList(session);
        sendPlayerInfo(session);
        sendItemsInUseForcedUpdate(session);
    }

    public static void playerDetails(Session session, ClientMessage msg) {
        int targetId = msg.readInt();
        session.send(MessageBuilder.playerDetails(ServerManager.getSession(targetId)));
    }

    private static void sendPlayerInfo(Session session) {
        try (ConnectionRef con = ConnectionRef.ref()) {
            session.send(MessageBuilder.playerInfo(session, (short) 0, con));
        } catch (SQLException e) {
            Output.println("Exception when writing player info: " + e.getMessage(), Level.DEBUG);
        }
    }

    public static void sendItemList(Session session) {
        Map<Integer, Item> items = session.getCache().getItems();

        session.send(MessageBuilder.itemList(items, (short) 0));
    }

    /** This is a trick to update client's inventory items in use. */
    public static void sendItemsInUseForcedUpdate(Session session) {
        Map<Integer, Item> items = session.getCache().getItems();

        if (!items.isEmpty()) {
            Item item = items.values().iterator().next();

            session.send(item.isSelected() ?
                    MessageBuilder.activateItem(item.getInventoryId(), session, (short) 0) :
                    MessageBuilder.deactivateItem(item.getInventoryId(), session, (short) 0));
        }
    }

    private static void sendTrainingList(Session session) {
        Map<Integer, Training> trainings = session.getCache().getLearns();
        session.send(MessageBuilder.trainingList(trainings, (short) 0));
    }

    public static void sendSkillList(Session session) {
        Map<Integer, Skill> items = session.getCache().getSkills();
        byte slots = PlayerInfo.getSkillSlots(session.getCache().getItems());
        session.send(MessageBuilder.skillList(items, slots, (short) 0));
    }

    private static void sendCelebrationList(Session session) {
        Map<Integer, Celebration> items = session.getCache().getCelebrations();
        session.send(MessageBuilder.celebrationList(items, (short) 0));
    }

    public static void resetStats(int playerId) {
        short position = PlayerInfo.getPosition(playerId);
        short level = PlayerInfo.getLevel(playerId);
        short branchPosition = Position.basePosition(position);

        PlayerStats newStats = new PlayerStats(StatsInfo.getInstance()
                .getCreationStats().get(branchPosition));

        MutableInt statsPoints = new MutableInt(10);

        if (level > 18) {
            onPlayerLevelUp(18, 17, branchPosition, newStats, statsPoints);
            onPlayerLevelUp(level, level - 18, position, newStats, statsPoints);
        } else {
            onPlayerLevelUp(level, level - 1, branchPosition, newStats, statsPoints);
        }

        if ((level >= 18) && Position.isAdvancedPosition(position)) {
            // Apply upgrade stats
            PlayerStats.sumStats(StatsInfo.getInstance().getUpgradeStats().get(position),
                    1, newStats, statsPoints);
        }

        try (ConnectionRef con = ConnectionRef.ref()) {
            // Set new stats
            PlayerInfo.setStats(newStats, playerId, con);

            // Set stats point
            PlayerInfo.setStatsPoints(statsPoints.shortValue(), playerId, con);
        } catch (SQLException e) {
            Output.println("Exception when resetting stats: " + e.getMessage(), Level.DEBUG);
        }
    }

    public static short checkIfLevelUp(Session s, short level, int exp, ConnectionRef ... con) {
        MutableShort levels = new MutableShort();

        TableManager.getLevelInfo(li -> (li.getLevel() > level) && (li.getExperience() <= exp))
                .ifPresent(newLevelInfo -> {
            if (newLevelInfo != null) {
                short newLevel = newLevelInfo.getLevel();

                if (newLevel > level) {
                    levels.setValue((short) (newLevel - level));
                }

                if (levels.shortValue() > 0) {
                    int playerId = s.getPlayerId();
                    PlayerInfo.setLevel(newLevel, playerId, con);
                    short position = s.getCache().getPosition(con);
                    onPlayerLevelUp(playerId, newLevel, levels.shortValue(), position, con);
                }
            }
        });

        return levels.shortValue();
    }

    private static void onPlayerLevelUp(int id, short level, short levels,
                                       short position, ConnectionRef ... con) {
        int from = level - levels;

        // Calculate stats points to add
        MutableInt statsPoints = new MutableInt(0);

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
        PlayerInfo.sumStatsPoints(statsPoints.shortValue(), id, con);
    }

    private static void onPlayerLevelUp(int level, int levels, short position,
                                        PlayerStats stats, MutableInt statPoints) {
        int from = level - levels;

        // Calculate stats points to add
        MutableInt statsPoints = new MutableInt(0);

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

        short result = 0;

        short[] values = new short[17];
        short total = 0;

        for (int i = 0; i < values.length; i++) {
            values[i] = msg.readShort();
            total += values[i];

            if (values[i] < 0) {
                result = -2; // Invalid value
                break;
            }
        }

        try (ConnectionRef con = ConnectionRef.ref()) {
            int playerId = session.getPlayerId();

            // If all values are valid
            if (result == 0) {
                short statsPoints = PlayerInfo.getStatsPoints(playerId, con);

                MutableInt remain = new MutableInt(0);
                PlayerStats stats = PlayerInfo.getStats(playerId, con);

                // If player have enough points
                if (total <= statsPoints) {
                    PlayerStats.sumStats(PlayerStats.fromArray(values), 1, stats, remain);

                    PlayerInfo.setStats(stats, playerId, con);
                    PlayerInfo.sumStatsPoints((short) -(total - remain.getValue()), playerId, con);
                } else {
                    result = -3; // Not enough stats points
                }
            }

            session.send(MessageBuilder.addStatsPoints(playerId, result, con));

            if (session.getRoomId() > 0) {
                RoomManager.getRoomById(session.getRoomId()).ifPresent(room ->
                        room.broadcast(MessageBuilder.updateRoomPlayer(playerId, con)));
            }
        } catch (SQLException e) {
            Output.println("Exception when adding stat points: " + e.getMessage(), Level.DEBUG);
        }
    }
}
