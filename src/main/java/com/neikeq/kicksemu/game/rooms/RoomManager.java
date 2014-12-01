package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

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

    public static void addRoom(Room room) {
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
                short playerLevel = session.getPlayerInfo().getLevel();

                if (playerLevel < minLevel || playerLevel > maxLevel) {
                    result = (byte) 252; // Invalid level
                }
            }

            // Send the validation result to the client
            ServerMessage response = MessageBuilder.createRoom((short)0, result);
            session.send(response);

            // If everything is correct, create the room
            if (result == 0) {
                Room room = new NormalRoom();

                // Limit the length of the name
                if (name.length() > MAX_ROOM_NAME_LENGTH) {
                    name = name.substring(0, MAX_ROOM_NAME_LENGTH);
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
                        room.getPlayerTeam(session.getPlayerInfo().getId()), result);
                session.send(msgJoinRoom);

                // Send the room info to the client
                ServerMessage msgRoomInfo = MessageBuilder.roomInfo(room);
                session.send(msgRoomInfo);

                // Send to the client information about players inside the room
                sendRoomPlayersInfo(session, room);
            }
        }
    }

    private static void sendRoomPlayersInfo(Session session, Room room) {
        for (Session s : room.getPlayers().values()) {
            ServerMessage roomPlayerInfo = MessageBuilder.roomPlayerInfo(s, room);
            session.send(roomPlayerInfo);
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
                        short level = session.getPlayerInfo().getLevel();

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
                    room.getPlayerTeam(session.getPlayerInfo().getId()), result);
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
        int playerId = session.getPlayerInfo().getId();

        Room room = getRoomById(roomId);

        if (room != null && room.isPlayerIn(playerId)) {
            RoomLeaveReason reason = RoomLeaveReason.LEAVED;

            session.leaveRoom(reason);

            ServerMessage msgLeaveRoom = MessageBuilder.leaveRoom(playerId, reason);
            session.send(msgLeaveRoom);
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

        if (room != null && room.isPlayerIn(session.getPlayerInfo().getId())) {
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

        if (room != null && room.isPlayerIn(session.getPlayerInfo().getId())) {
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

        short playerLevel = session.getPlayerInfo().getLevel();

        // TODO check if the server allows this goalkeeperMode
        if (maxSize == null || type == null || goalkeeperMode == null) {
            result = (byte) 255; // System problem
        } else if (room == null) {
            result = (byte) 254; // Room does not exist
        } else if (room.getMaster() != session.getPlayerInfo().getId()) {
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
}
