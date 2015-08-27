package com.neikeq.kicksemu.game.rooms.messages;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.clubs.MemberInfo;
import com.neikeq.kicksemu.game.rooms.ClubRoom;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.rooms.enums.RoomBall;
import com.neikeq.kicksemu.game.rooms.enums.RoomMap;
import com.neikeq.kicksemu.game.rooms.enums.RoomMode;
import com.neikeq.kicksemu.game.rooms.enums.RoomSize;
import com.neikeq.kicksemu.game.rooms.enums.RoomAccessType;
import com.neikeq.kicksemu.game.servers.ServerType;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.server.ServerManager;

public class ClubRoomMessages extends RoomMessages {

    private static final int MAX_ROOM_NAME_LENGTH = 14;

    public static void createRoom(Session session, ClientMessage msg) {
        RoomAccessType type = RoomAccessType.fromShort(msg.readShort());
        String name = msg.readString(15);
        String password = msg.readString(4);
        msg.ignoreBytes(1);

        int playerId = session.getPlayerId();
        int clubId = MemberInfo.getClubId(playerId);

        RoomMode roomMode = RoomMode.fromInt(msg.readByte());

        // Check that everything is correct
        byte result = 0;

        ServerType serverType = ServerManager.getServerType();

        if (type == null || roomMode == null || roomMode.notValidForServer(serverType)) {
            result = (byte) -1; // System problem
        } else if (PlayerInfo.getLevel(playerId) < 5) {
            result = (byte) -3; // Does not meet the level requirements
        } else if (clubId <= 0) {
            result = (byte) -4; // Not a club member
        } else if (RoomManager.getRoomById(clubId) != null) {
            result = (byte) -5; // The club already has a team
        }

        // TODO Check result -2: Too many players in the opponent team. What does that even mean?

        // Send the result to the client
        session.send(MessageBuilder.clubCreateRoom((short) 0, result));

        // If everything is correct, create the room
        if (result == 0) {
            ClubRoom room = new ClubRoom();

            // Limit the length of the name
            if (name.length() > MAX_ROOM_NAME_LENGTH) {
                name = name.substring(0, MAX_ROOM_NAME_LENGTH);
            }

            // If password is blank, disable password usage
            if (type == RoomAccessType.PASSWORD && password.isEmpty()) {
                type = RoomAccessType.FREE;
            }

            // Set room information from received data
            room.setName(name);
            room.setPassword(password);
            room.setAccessType(type);
            room.setRoomMode(roomMode);
            room.setMinLevel(MIN_ROOM_LEVEL);
            room.setMaxLevel(MAX_ROOM_LEVEL);
            room.setMap(RoomMap.RESERVOIR);
            room.setBall(RoomBall.TEAM_ARENA);
            room.setMaxSize(RoomSize.SIZE_4V4);

            synchronized (RoomManager.ROOMS_LOCKER) {
                // Get the room id
                room.setId(clubId);
                // Add it to the rooms list
                RoomManager.addRoom(room);
                // Add the player to the room
                room.addPlayer(session);
            }
        }
    }

    public static void joinRoom(Session session, ClientMessage msg) {
        if (session.getRoomId() > 0) return;

        int roomId = msg.readShort();
        String password = msg.readString(4);

        Room room = RoomManager.getRoomById(roomId);

        // Try to join the room.
        if (room != null) {
            room.tryJoinRoom(session, password);
        } else {
            // Result -3 means that the room does not exists.
            session.send(MessageBuilder.clubJoinRoom(null, (byte) -3));
        }
    }

    public static void roomList(Session session, ClientMessage msg) {
        short page = msg.readShort();
        session.send(MessageBuilder.clubRoomList(RoomManager.getRoomsFromPage(page),
                page, (byte) 0));
    }
}
