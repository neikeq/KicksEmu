package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.rooms.enums.*;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.MessageId;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

        int i = 0;
        int startIndex = page * ROOMS_PER_PAGE;

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

    private static void sendRoomPlayersInfo(Session session, Room room) {
        for (Session s : room.getPlayers().values()) {
            ServerMessage roomPlayerInfo = MessageBuilder.roomPlayerInfo(s, room);
            session.send(roomPlayerInfo);
        }
    }

    public static void createRoom(Session session, ClientMessage msg) {
        if (session.getRoomId() <= 0) {
            // Read data from message

            RoomType type = RoomType.fromInt(msg.readShort());
            String name = msg.readString(45);
            String password = msg.readString(4);

            msg.ignoreBytes(1);

            GoalkeeperMode goalkeeperMode = GoalkeeperMode.fromInt(msg.readByte());

            byte minLevel = msg.readByte();
            byte maxLevel = msg.readByte();

            RoomMap map = RoomMap.fromInt(msg.readShort());
            RoomBall ball = RoomBall.fromInt(msg.readShort());

            RoomSize maxSize = RoomSize.fromInt(msg.readByte());

            // Check that everything is correct

            byte result = 0;

            // TODO check if the server allows this goalkeeperMode
            if (minLevel < MIN_ROOM_LEVEL || maxLevel > MAX_ROOM_LEVEL) {
                result = (byte) 253; // Wrong level settings
            } else if (maxSize == null || type == null ||
                    map == null || ball == null || goalkeeperMode == null) {
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
                room.setGoalkeeperMode(goalkeeperMode);
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
                ServerMessage msgJoinRoom = MessageBuilder.joinRoom(room.getId(),
                        room.getPlayerTeam(session.getPlayerId()), result);
                session.send(msgJoinRoom);

                // Send the room info to the client
                ServerMessage msgRoomInfo = MessageBuilder.roomInfo(room);
                session.send(msgRoomInfo);

                // Send to the client information about players inside the room
                sendRoomPlayersInfo(session, room);
            }
        }
    }

    public static void joinRoom(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        String password = msg.readString(4);

        byte result = 0;

        Room room = getRoomById(roomId);

        boolean roomExists = room != null;

        if (roomExists) {
            if (!room.isFull()) {
                // If room does not have a password, or typed password matches room's password
                if (room.getType() != RoomType.PASSWORD || password.equals(room.getPassword())) {
                    if (!room.isPlaying()) {
                        short level = PlayerInfo.getLevel(session.getPlayerId());

                        // If player level is not allowed in room settings
                        if (level < room.getMinLevel() || level > room.getMaxLevel()) {
                            result = (byte)248; // Invalid level
                        }
                    } else {
                        result = (byte)250; // Match already started
                    }
                } else {
                    result = (byte)251; // Wrong password
                }
            } else {
                result = (byte)252; // Room is full
            }
        } else {
            result = (byte)253; // Room does not exist
        }

        if (result == 0 && roomExists) {
            room.addPlayer(session);

            // Send the notification to the client
            ServerMessage response = MessageBuilder.joinRoom(roomId,
                    room.getPlayerTeam(session.getPlayerId()), result);
            session.send(response);

            // Send the room info to the client
            ServerMessage msgRoomInfo = MessageBuilder.roomInfo(room);
            session.send(msgRoomInfo);

            // Send to the client information about players inside the room
            sendRoomPlayersInfo(session, room);
        } else {
            // Send the validation result to the client
            ServerMessage response = MessageBuilder.joinRoom(roomId, null, result);
            session.send(response);
        }
    }

    public static void leaveRoom(Session session, ClientMessage msg) {
        short roomId = msg.readShort();
        int playerId = session.getPlayerId();

        Room room = getRoomById(roomId);

        if (room != null && room.isPlayerIn(playerId)) {
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
        GoalkeeperMode goalkeeperMode = GoalkeeperMode.fromInt(msg.readByte());
        byte minLevel = msg.readByte();
        byte maxLevel = msg.readByte();
        RoomSize maxSize = RoomSize.fromInt(msg.readByte());

        if (minLevel < MIN_ROOM_LEVEL) {
            minLevel = MIN_ROOM_LEVEL;
        }

        if (maxLevel > MAX_ROOM_LEVEL) {
            maxLevel = MAX_ROOM_LEVEL;
        }

        // Check that everything is correct

        byte result = 0;

        Room room = rooms.get(roomId);

        short playerLevel = PlayerInfo.getLevel(session.getPlayerId());

        // TODO check if the server allows this goalkeeperMode
        if (maxSize == null || type == null || goalkeeperMode == null) {
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
        } else if (playerLevel < minLevel || playerLevel > maxLevel) {
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
            room.setGoalkeeperMode(goalkeeperMode);
            room.setMinLevel(minLevel);
            room.setMaxLevel(maxLevel);
            room.setMaxSize(maxSize);

            ServerMessage msgRoomSettings = MessageBuilder.roomSettings(room, result);
            room.sendBroadcast(msgRoomSettings);
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
        if (room != null && room.isPlayerIn(playerId) && room.getState() == RoomState.WAITING) {
            RoomTeam currentTeam = room.getPlayerTeam(playerId);
            RoomTeam newTeam = room.swapPlayerTeam(playerId, currentTeam);

            ServerMessage msgSwapTeam = MessageBuilder.swapTeam(playerId, newTeam);

            if (newTeam != currentTeam) {
                room.sendBroadcast(msgSwapTeam);
            } else {
                session.send(msgSwapTeam);
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
            if (room.getMaster() == session.getPlayerId()) {
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
                                room.getId(), PlayerInfo.getName(session.getPlayerId()));
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
                ServerMessage response = MessageBuilder.invitePlayer(result, 0, "");
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
                        ServerMessage msgAllConfirmed = MessageBuilder.startCountDown((byte)1);
                        room.sendBroadcast(msgAllConfirmed);
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

            ServerMessage msgHostInfo = MessageBuilder.hostInfo(room);
            room.sendBroadcast(msgHostInfo);
        }
    }

    public static void countDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short count = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);

            ServerMessage msgCountDown = MessageBuilder.countDown(count);
            room.sendBroadcast(msgCountDown);
        }
    }

    public static void cancelCountDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);

            if (room.getState() == RoomState.COUNT_DOWN) {
                room.cancelCountDown();
            }
        }
    }

    public static void matchLoading(Session session, ClientMessage msg) {
        int playerId = msg.readInt();
        int roomId = msg.readShort();
        short status = msg.readShort();

        if (session.getPlayerId() == playerId && session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);

            ServerMessage msgMatchLoading = MessageBuilder.matchLoading(playerId, roomId, status);
            room.sendBroadcast(msgMatchLoading);
        }
    }

    public static void playerReady(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        if (session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);

            if (!room.getConfirmedPlayers().contains(playerId)) {
                room.getConfirmedPlayers().add(playerId);
            }

            if (room.getConfirmedPlayers().size() >= room.getPlayers().size()) {
                room.setState(RoomState.PLAYING);
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


    /**
     * Currently, since match making packets are not yet analyzed, we are just copying
     * the message and broadcasting it to the room (with little modifications).
     */
    public static void matchResult(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        msg.readInt();
        int mom = msg.readInt();
        short result = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = getRoomById(roomId);

            // If match started
            if (room.getState() == RoomState.PLAYING) {
                // TODO Temporary rewards. Must be removed/replaced in the future.
                int minPlayers = 6;
                boolean reward = false;

                if (room.getRedTeam().size() == room.getBlueTeam().size() &&
                        room.getPlayers().size() >= minPlayers ||
                        Configuration.getBoolean("game.rewards.practice")) {
                    reward = true;
                    room.getPlayers().values().stream()
                            .forEach(s -> {
                                PlayerInfo.setPoints(Configuration.getInt("game.rewards.point"),
                                        s.getPlayerId());
                                PlayerInfo.setExperience(Configuration.getInt("game.rewards.exp"),
                                        s.getPlayerId());
                                UserInfo.setKash(Configuration.getInt("game.rewards.kash"),
                                        s.getUserId());

                                CharacterManager.checkExperience(s.getPlayerId());
                            });
                }
                // -----------------

                ByteBuf response = ByteBufAllocator.DEFAULT.buffer().order(ByteOrder.LITTLE_ENDIAN);

                try {
                    response.writeBytes(new byte[10]);
                    response.writeShort(610);
                    response.writeInt(MessageId.MATCH_RESULT);
                    response.writeShort(0);
                    response.writeInt(mom);
                    response.writeShort(result);

                    for (int i = 0; i < msg.getSize() - 17; i++) {
                        response.writeByte(msg.readByte());
                    }

                    response.writeShort(0);

                    response.writeInt(reward ? Configuration.getInt("game.rewards.exp") : 0);
                    response.writeInt(reward ? Configuration.getInt("game.rewards.point") : 0);
                    response.writeBytes(new byte[127]);

                    for (Session s : room.getPlayers().values()) {
                        response.retain();
                        s.getChannel().writeAndFlush(response);
                        s.sendAndFlush(MessageBuilder.playerProgress(s.getPlayerId()));
                    }
                } finally {
                    response.release();
                }

                room.setState(RoomState.RESULT);
                room.getConfirmedPlayers().clear();
            }
        }
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
        }
    }
}
