package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.Position;
import com.neikeq.kicksemu.game.inventory.Soda;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.rooms.enums.*;
import com.neikeq.kicksemu.game.rooms.match.MatchResult;
import com.neikeq.kicksemu.game.rooms.match.PlayerResult;
import com.neikeq.kicksemu.game.rooms.match.RewardCalculator;
import com.neikeq.kicksemu.game.rooms.match.TeamResult;
import com.neikeq.kicksemu.game.servers.GameServerType;
import com.neikeq.kicksemu.game.servers.ServerInfo;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.network.server.udp.UdpPing;
import com.neikeq.kicksemu.storage.MySqlManager;
import com.neikeq.kicksemu.utils.GameEvents;
import com.neikeq.kicksemu.utils.mutable.MutableBoolean;
import com.neikeq.kicksemu.utils.mutable.MutableInteger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RoomManager {

    private static final Map<Integer, Room> rooms = new HashMap<>();

    private static final Object roomsLocker = new Object();

    private static final int MAX_ROOM_NAME_LENGTH = 30;
    private static final int MAX_ROOM_LEVEL = 60;
    private static final int MIN_ROOM_LEVEL = 1;
    private static final int ROOMS_PER_PAGE = 5;

    public static Room getRoomById(int id) {
        synchronized (roomsLocker) {
            return rooms.get(id);
        }
    }

    private static void addRoom(Room room) {
        synchronized (roomsLocker) {
            if (!rooms.containsKey(room.getId())) {
                rooms.put(room.getId(), room);
            }
        }
    }

    public static void removeRoom(int id) {
        synchronized (roomsLocker) {
            rooms.remove(id);
        }
    }

    public static int openRooms() {
        return rooms.size();
    }

    /**
     * Returns the smallest missing key in rooms map.<br>
     * Required to get an id for new rooms.
     */
    private static int getSmallestMissingIndex() {
        synchronized (roomsLocker) {
            int i;

            for (i = 1; i <= rooms.size(); i++) {
                if (!rooms.containsKey(i)) {
                    return i;
                }
            }

            return i;
        }
    }

    /**
     * Returns a map containing the rooms from the specified page.
     * @param page the page to get the rooms from
     * @return a map with a maximum length of {@value #ROOMS_PER_PAGE}
     * containing the rooms from the specified page
     */
    private static Map<Integer, Room> getRoomsFromPage(int page) {
        Set<Integer> indexes = rooms.keySet();
        Map<Integer, Room> pageRooms = new HashMap<>();

        int startIndex = page * ROOMS_PER_PAGE;
        int i = 0;

        for (int index : indexes) {
            if (i >= startIndex) {
                pageRooms.put(index, rooms.get(index));

                if (pageRooms.size() > ROOMS_PER_PAGE) {
                    break;
                }
            }

            i++;
        }

        return pageRooms;
    }

    public static void createRoom(Session session, ClientMessage msg) {
        if (session.getRoomId() <= 0) {
            RoomType type = RoomType.fromInt(msg.readShort());
            String name = msg.readString(45);
            String password = msg.readString(4);

            msg.ignoreBytes(1);

            RoomMode roomMode = RoomMode.fromInt(msg.readByte());

            byte minLevel = msg.readByte();
            byte maxLevel = msg.readByte();

            RoomMap map = RoomMap.fromInt(msg.readShort());
            RoomBall ball = RoomBall.fromInt(msg.readShort());
            RoomSize maxSize = RoomSize.fromInt(msg.readByte());

            // Check that everything is correct
            byte result = 0;

            GameServerType serverType = ServerInfo.getType(ServerManager.getServerId());

            if (minLevel < MIN_ROOM_LEVEL || maxLevel > MAX_ROOM_LEVEL) {
                result = (byte) 253; // Wrong level settings
            } else if (maxSize == null || type == null || map == null || ball == null ||
                    roomMode == null || !roomMode.isValidForServer(serverType)) {
                result = (byte) 255; // System problem
            } else {
                short playerLevel = PlayerInfo.getLevel(session.getPlayerId());

                if (playerLevel < minLevel || playerLevel > maxLevel) {
                    result = (byte) 252; // Invalid level
                }
            }

            // Send the validation result to the client
            ServerMessage response = MessageBuilder.createRoom((short)0, result);
            session.send(response);

            // If everything is correct, create the room
            if (result == 0) {
                Room room = new Room();

                // Limit the length of the name
                if (name.length() > MAX_ROOM_NAME_LENGTH) {
                    name = name.substring(0, MAX_ROOM_NAME_LENGTH);
                }

                // If password is blank, disable password usage
                if (type == RoomType.PASSWORD && password.isEmpty()) {
                    type = RoomType.FREE;
                }

                // Set room information from received data
                room.setName(name);
                room.setPassword(password);
                room.setType(type);
                room.setRoomMode(roomMode);
                room.setMinLevel(minLevel);
                room.setMaxLevel(maxLevel);
                room.setMap(map);
                room.setBall(ball);
                room.setMaxSize(maxSize);

                synchronized (roomsLocker) {
                    // Get the room id
                    room.setId(getSmallestMissingIndex());
                    // Add it to the rooms list
                    addRoom(room);
                    // Add the player to the room
                    room.addPlayer(session);
                }

                // Notify the client to join the room
                session.send(MessageBuilder.joinRoom(room, session.getPlayerId(), result));
            }
        }
    }

    public static void joinRoom(Session session, ClientMessage msg) {
        if (session.getRoomId() > 0) return;

        int roomId = msg.readShort();
        String password = msg.readString(4);

        Room room = getRoomById(roomId);

        // Try to join the room.
        // Result -3 means that the room does not exists.
        byte result = room != null ? room.tryJoinRoom(session, password) : -3;

        // Send the notification to the client
        session.send(MessageBuilder.joinRoom(room, session.getPlayerId(), result));
    }

    public static void quickJoinRoom(Session session) {
        if (session.getRoomId() > 0) return;

        short level = PlayerInfo.getLevel(session.getPlayerId());

        List<Room> freeRooms = rooms.values().stream()
                .filter(r -> !r.isPlaying() && r.getType() != RoomType.PASSWORD &&
                        !r.isFull() && !r.playerHasInvalidLevel(level))
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.sort(freeRooms, (r1, r2) ->
                Byte.compare(r2.getCurrentSize(), r1.getCurrentSize()));

        // If a valid room was found
        if (freeRooms.size() > 0) {
            Room room = freeRooms.get(0);

            byte result = room.tryJoinRoom(session, "");

            // Send the notification to the client
            session.send(MessageBuilder.joinRoom(room, session.getPlayerId(), result));
        } else {
            // Notify the player that no room were found
            session.send(MessageBuilder.quickJoinRoom((byte) -2));
        }
    }

    public static void leaveRoom(Session session, ClientMessage msg) {
        short roomId = msg.readShort();
        int playerId = session.getPlayerId();

        Room room = getRoomById(roomId);

        if (room != null && room.isPlayerIn(playerId) &&
                (room.state() == RoomState.WAITING || room.state() == RoomState.COUNT_DOWN)) {
            RoomLeaveReason reason = RoomLeaveReason.LEAVED;

            session.leaveRoom(reason);
        }
    }

    public static void roomList(Session session, ClientMessage msg) {
        short page = msg.readShort();

        Map<Integer, Room> pageRooms = getRoomsFromPage(page);

        ServerMessage response = MessageBuilder.roomList(pageRooms, page, (byte) 0);
        session.send(response);
    }

    public static void roomMap(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short mapId = msg.readShort();

        Room room = getRoomById(roomId);

        if (room != null && room.isPlayerIn(session.getPlayerId())) {
            RoomMap map = RoomMap.fromInt(mapId);

            if (map != null) {
                room.setMap(map);

                // Notify players in room that map changed
                ServerMessage msgRoomMap = MessageBuilder.roomMap(mapId);
                room.sendBroadcast(msgRoomMap);
            }
        }
    }

    public static void roomBall(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short ballId = msg.readShort();

        Room room = getRoomById(roomId);

        if (room != null && room.isPlayerIn(session.getPlayerId())) {
            RoomBall ball = RoomBall.fromInt(ballId);

            if (ball != null) {
                room.setBall(ball);

                // Notify players in room that ball changed
                ServerMessage msgRoomBall = MessageBuilder.roomBall(ballId);
                room.sendBroadcast(msgRoomBall);
            }
        }
    }

    public static void roomSettings(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        RoomType type = RoomType.fromInt(msg.readShort());
        String name = msg.readString(45);
        String password = msg.readString(4);
        msg.ignoreBytes(1);
        RoomMode roomMode = RoomMode.fromInt(msg.readByte());
        byte minLevel = msg.readByte();
        byte maxLevel = msg.readByte();
        RoomSize maxSize = RoomSize.fromInt(msg.readByte());

        if (minLevel < MIN_ROOM_LEVEL) {
            minLevel = MIN_ROOM_LEVEL;
        }

        if (maxLevel > MAX_ROOM_LEVEL) {
            maxLevel = MAX_ROOM_LEVEL;
        }

        // Check that settings are valid

        byte result = 0;

        Room room = rooms.get(roomId);

        GameServerType serverType = ServerInfo.getType(ServerManager.getServerId());

        if (maxSize == null || type == null || roomMode == null ||
                !roomMode.isValidForServer(serverType)) {
            result = (byte) 255; // System problem
        } else if (room == null) {
            result = (byte) 254; // Room does not exist
        } else if (room.getMaster() != session.getPlayerId()) {
            result = (byte) 253; // Player is not room's master
        } else if (maxSize.toInt() < room.getPlayers().size()) {
            result = (byte) 252; // Size is lower than players in room
        } else if (minLevel > maxLevel) {
            result = (byte) 251; // Wrong level settings
        } else if (!room.isValidMaxLevel(maxLevel)) {
            result = (byte) 249; // Invalid maximum level
        } else if (!room.isValidMinLevel(minLevel)) {
            result = (byte) 248; // Invalid minimum level
        } else {
            short playerLevel = PlayerInfo.getLevel(session.getPlayerId());

            if (playerLevel < minLevel || playerLevel > maxLevel) {
                result = (byte) 250; // Invalid level
            } else {
                // Limit the length of the name
                if (name.length() > MAX_ROOM_NAME_LENGTH) {
                    name = name.substring(0, MAX_ROOM_NAME_LENGTH);
                }

                // Update room settings
                room.setType(type);
                room.setName(name);
                room.setPassword(password);
                room.setRoomMode(roomMode);
                room.setMinLevel(minLevel);
                room.setMaxLevel(maxLevel);
                room.setMaxSize(maxSize);

                ServerMessage msgRoomSettings = MessageBuilder.roomSettings(room, result);
                room.sendBroadcast(msgRoomSettings);
            }
        }

        if (result != 0) {
            ServerMessage msgRoomSettings = MessageBuilder.roomSettings(room, result);
            session.send(msgRoomSettings);
        }
    }

    public static void swapTeam(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        Room room = getRoomById(roomId);

        // If the room is valid, the player is inside it and it is in waiting state
        if (room != null && room.isPlayerIn(playerId) && room.state() == RoomState.WAITING) {
            if (!room.getSwapLocker().isPlayerLocked(playerId)) {
                RoomTeam currentTeam = room.getPlayerTeam(playerId);
                RoomTeam newTeam = room.swapPlayerTeam(playerId, currentTeam);

                if (newTeam != currentTeam) {
                    room.getSwapLocker().lockPlayer(playerId);
                    room.sendBroadcast(MessageBuilder.swapTeam(playerId, newTeam));
                }
            }
        }
    }

    public static void kickPlayer(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerToKick = msg.readInt();

        byte result = 0;

        Room room = getRoomById(session.getRoomId());

        // If the room exist and the player is inside it
        if (room != null && room.getId() == roomId) {
            // If the player is the room master
            if (room.getMaster() == session.getPlayerId() && room.isLobbyScreen()) {
                // If the player is in the room
                if (room.isPlayerIn(playerToKick)) {
                    room.getPlayers().get(playerToKick).leaveRoom(RoomLeaveReason.KICKED);
                } else {
                    result = (byte)252; // Player not found
                }
            } else {
                result = (byte)253; // Not the room master
            }
        } else {
            result = (byte)254; // Invalid room
        }

        // If there is something wrong, notify the client
        if (result != 0) {
            ServerMessage response = MessageBuilder.kickPlayer(result);
            session.send(response);
        }
    }

    public static void invitePlayer(Session session, ClientMessage msg) {
        Room room = getRoomById(session.getRoomId());

        // If the player is in a room
        if (room != null) {
            int playerToInvite = msg.readInt();

            byte result = 0;

            // If the player to invite is in the main lobby
            if (LobbyManager.getMainLobby().getPlayers().contains(playerToInvite)) {
                Session sessionToInvite = ServerManager.getPlayers().get(playerToInvite);

                if (UserInfo.getSettings(sessionToInvite.getUserId()).getInvites()) {
                    byte level = (byte)PlayerInfo.getLevel(sessionToInvite.getPlayerId());

                    // If player level meets the level requirement of the room
                    if (room.getMinLevel() <= level && room.getMaxLevel() >= level) {
                        ServerMessage invitation = MessageBuilder.invitePlayer(result,
                                room, PlayerInfo.getName(session.getPlayerId()));
                        sessionToInvite.sendAndFlush(invitation);
                    } else {
                        result = (byte)251; // Player does not meet the level requirements
                    }
                } else {
                    result = (byte)253; // Player does not accept invitations
                }
            } else {
                result = (byte)254; // Player not found
            }

            // If there is something wrong, notify the client
            if (result != 0) {
                ServerMessage response = MessageBuilder.invitePlayer(result, null, "");
                session.sendAndFlush(response);
            }
        }
    }

    public static void startCountDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        byte type = msg.readByte();

        if (session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);
            int playerId = session.getPlayerId();

            switch (type) {
                case 1:
                    if (!room.getConfirmedPlayers().contains(playerId)) {
                        room.getConfirmedPlayers().add(playerId);
                    }

                    if (room.getConfirmedPlayers().size() >= room.getPlayers().size()) {
                        room.getConfirmedPlayers().clear();
                        room.sendBroadcast(MessageBuilder.startCountDown((byte)1));
                    }
                    break;
                case -1:
                    if (!room.isPlaying() && room.getMaster() == playerId) {
                        room.startCountDown();
                    }
                    break;
                default:
            }
        }
    }

    public static void hostInfo(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);

            if (room.getHost() == session.getPlayerId()) {
                room.sendBroadcast(MessageBuilder.hostInfo(room));
            }
        }
    }

    public static void countDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short count = msg.readShort();

        Room room = getRoomById(roomId);

        if (room != null && room.getMaster() == session.getPlayerId() && room.isLobbyScreen()) {
            if (count == 0) {
                room.setState(RoomState.LOADING);
                room.updateTrainingFactor();
            }

            ServerMessage msgCountDown = MessageBuilder.countDown(count);
            room.sendBroadcast(msgCountDown);
        }
    }

    public static void cancelCountDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);

            if (room.state() == RoomState.COUNT_DOWN) {
                room.cancelCountDown();
            }
        }
    }

    public static void matchLoading(Session session, ClientMessage msg) {
        msg.readInt();
        int playerId = session.getPlayerId();
        int roomId = msg.readShort();
        short status = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);

            room.sendBroadcast(MessageBuilder.matchLoading(playerId, roomId, status));
        }
    }

    public static void playerReady(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        if (session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);

            if (!room.getConfirmedPlayers().contains(playerId)) {
                room.getConfirmedPlayers().add(playerId);

                // Instead of waiting 5 seconds (or not), we send an udp ping immediately to
                // the client so we can update his udp port (if changed) before match starts
                UdpPing.sendUdpPing(session);
            }

            if (room.getConfirmedPlayers().size() >= room.getPlayers().size()) {
                room.setState(RoomState.PLAYING);
                room.setTimeStart(System.nanoTime());
                room.sendBroadcast(MessageBuilder.playerReady((byte)0));
            }
        }
    }

    public static void startMatch(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);

            byte result = 0;

            if (room.getConfirmedPlayers().size() < room.getPlayers().size()) {
                result = -1;
            }

            session.send(MessageBuilder.startMatch(result));
        }
    }

    public static void matchResult(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        msg.ignoreBytes(4);

        if (session.getRoomId() != roomId) return;

        Room room = getRoomById(roomId);

        // If match was not playing
        if (room.state() != RoomState.PLAYING) return;

        room.setState(RoomState.RESULT);

        MatchResult result = MatchResult.fromMessage(msg,
                room.redTeamSize() + room.blueTeamSize());

        // Apply level gap bonus if the levels difference in room settings is less than 10
        boolean levelGapReward = room.getMaxLevel() - room.getMinLevel() < 10;
        boolean goldenTime = GameEvents.isGoldenTime();

        // Check the match countdown
        long countdown = 300 - ((System.nanoTime() - room.getTimeStart()) / 1000000000);
        short gameCountdown = result.getCountdown();

        // If countdown sent by client is not valid (more than 20 secs less than server's),
        // disable rewards and player's history updating
        if (gameCountdown < countdown && gameCountdown - countdown > 20) {
            room.resetTrainingFactor();
        }

        try (Connection con = MySqlManager.getConnection()) {
            // Reward players
            result.getPlayers().stream().forEach(pr -> {
                int playerId = pr.getPlayerId();
                int reward = RewardCalculator.calculateReward(pr, room, gameCountdown);

                int appliedReward = reward;

                // If player's position is a DF branch
                if (Position.trunk(PlayerInfo.getPosition(playerId, con)) == Position.DF) {
                    short scoredGoals = room.getPlayerTeam(playerId) == RoomTeam.RED ?
                            result.getRedTeam().getGoals() : result.getBlueTeam().getGoals();
                    short concededGoals = room.getPlayerTeam(playerId) == RoomTeam.RED ?
                            result.getBlueTeam().getGoals() : result.getRedTeam().getGoals();

                    // If player's team did not lose and conceded 1 or less goals,
                    // increase his rewards by 30%
                    appliedReward += concededGoals <= 1 && scoredGoals >= concededGoals ?
                            (reward * 30) / 100 : 0;
                }

                appliedReward += levelGapReward ? (reward * 10) / 100 : 0;
                appliedReward += goldenTime ? (reward * 50) / 100 : 0;
                // If player is mvp increase his rewards by 25%
                appliedReward += result.getMom() == playerId ? (reward * 25) / 100 : 0;

                final MutableInteger points = new MutableInteger(appliedReward);
                final MutableInteger experience = new MutableInteger(appliedReward);

                // Apply item reward bonuses
                PlayerInfo.getInventoryItems(playerId, con).values().stream()
                        .filter(i -> i.getExpiration().isUsage() && i.isSelected())
                        .forEach(i -> {
                            Soda bonusOne = Soda.fromId(i.getBonusOne());

                            if (bonusOne != null) {
                                bonusOne.applyBonus(reward, experience, points);
                            }

                            Soda bonusTwo = Soda.fromId(i.getBonusTwo());

                            if (bonusTwo != null) {
                                bonusTwo.applyBonus(reward, experience, points);
                            }
                        });

                pr.setExperience(experience.get() * Configuration.getInt("game.rewards.exp"));
                pr.setPoints(points.get() * Configuration.getInt("game.rewards.point"));
            });

            // Broadcast match result message after calculating rewards
            result.getPlayers().stream().forEach(pr ->
                    room.getPlayers().get(pr.getPlayerId()).sendAndFlush(
                            MessageBuilder.matchResult(result, pr, room, con))
            );

            // Send match result message to observers players in the room
            if (room.getObservers().size() > 0) {
                // Because observer players does not count in stats,
                // we pass an empty PlayerResult instance
                ServerMessage observerMsg = MessageBuilder.matchResult(result,
                        new PlayerResult(), room, con);

                room.getObservers().stream().forEach(o ->
                                room.getPlayers().get(o).sendAndFlush(observerMsg));
            }

            result.getPlayers().stream().forEach(pr -> {
                int playerId = pr.getPlayerId();

                // Add the experience and points earned to the player
                PlayerInfo.sumPoints(pr.getPoints(), playerId, con);
                PlayerInfo.sumExperience(pr.getExperience(), playerId, con);

                short levels = 0;

                if (pr.getExperience() > 0) {
                    // Check if player did level up and apply level up operations if needed
                    levels = CharacterManager.checkExperience(playerId, con);

                    // If player did level up, send him the updated stats points
                    if (levels > 0) {
                        room.getPlayers().get(playerId)
                                .sendAndFlush(MessageBuilder.playerStats(playerId, con));
                    }
                }

                // If match was not in training mode, update player's history
                if (room.getTrainingFactor() > 0) {
                    TeamResult teamResult = room.getPlayerTeam(playerId) == RoomTeam.RED ?
                            result.getRedTeam() : result.getBlueTeam();

                    RewardCalculator.updatePlayerHistory(pr, teamResult,
                            result.getMom(), con);

                    MutableBoolean expired = new MutableBoolean(false);

                    // Decrease by 1 the remain usage of usage items
                    PlayerInfo.getInventoryItems(playerId, con).values().stream()
                            .filter(i -> i.getExpiration().isUsage() && i.isSelected())
                            .forEach(item -> {
                                item.sumUsages((short) -1);
                                PlayerInfo.setInventoryItem(item, playerId, con);

                                if (item.getUsages() <= 0) {
                                    expired.set(true);
                                }
                            });

                    if (expired.get()) {
                        CharacterManager.sendItemList(room.getPlayers().get(playerId));
                    }

                    if (levels <= 0) {
                        room.getPlayers().get(playerId)
                                .sendAndFlush(MessageBuilder.playerStats(playerId, con));
                    }
                }
            });

            room.getConfirmedPlayers().clear();
        } catch (SQLException ignored) {}
    }

    public static void unknown1(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);

            room.setState(RoomState.WAITING);

            room.sendBroadcast(MessageBuilder.unknown1());
        }
    }

    public static void unknown2(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);

            room.sendBroadcast(MessageBuilder.unknown2());

            if (GameEvents.isGoldenTime() || GameEvents.isClubTime()) {
                room.sendBroadcast(MessageBuilder.nextTip("", (byte) 0));
            }
        }
    }

    public static void cancelLoading(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        Room room = getRoomById(roomId);

        if (room != null && room.state() == RoomState.LOADING &&
                room.getHost() == session.getPlayerId()) {
            room.setState(RoomState.WAITING);
            room.sendBroadcast(MessageBuilder.cancelLoading());
        }
    }
}
