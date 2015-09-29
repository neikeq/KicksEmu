package com.neikeq.kicksemu.game.rooms.messages;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.rooms.enums.RoomBall;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.enums.RoomMap;
import com.neikeq.kicksemu.game.rooms.enums.RoomMode;
import com.neikeq.kicksemu.game.rooms.enums.RoomSize;
import com.neikeq.kicksemu.game.rooms.enums.RoomState;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.rooms.enums.RoomAccessType;
import com.neikeq.kicksemu.game.rooms.match.MatchResult;
import com.neikeq.kicksemu.game.rooms.match.MatchResultHandler;
import com.neikeq.kicksemu.game.servers.ServerType;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.network.server.udp.UdpPing;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.utils.GameEvents;

public class RoomMessages {

    private static final int MAX_ROOM_NAME_LENGTH = 30;
    static final int MAX_ROOM_PASSWORD_LENGTH = 4;
    static final byte MAX_ROOM_LEVEL = 60;
    static final byte MIN_ROOM_LEVEL = 1;

    public static void roomList(Session session, ClientMessage msg) {
        short page = msg.readShort();
        session.send(MessageBuilder.roomList(RoomManager.getRoomsFromPage(page),
                page, (short) 0));
    }

    public static void createRoom(Session session, ClientMessage msg) {
        // If player is not already in a room
        if (session.getRoomId() <= 0) {
            RoomAccessType type = RoomAccessType.fromShort(msg.readShort());
            String name = msg.readString(45);
            String password = msg.readString(5);

            RoomMode roomMode = RoomMode.fromInt(msg.readByte());

            byte minLevel = msg.readByte();
            byte maxLevel = msg.readByte();

            RoomMap map = RoomMap.fromInt(msg.readShort());
            RoomBall ball = RoomBall.fromInt(msg.readShort());
            RoomSize maxSize = RoomSize.fromInt(msg.readByte());

            // Check that everything is correct
            short result = 0;

            ServerType serverType = ServerManager.getServerType();

            if (minLevel < MIN_ROOM_LEVEL || maxLevel > MAX_ROOM_LEVEL) {
                result = -3; // Wrong level settings
            } else if (maxSize == null || type == null || map == null || ball == null ||
                    roomMode == null || roomMode.notValidForServer(serverType)) {
                result = -1; // System problem
            } else {
                short playerLevel = PlayerInfo.getLevel(session.getPlayerId());

                if (playerLevel < minLevel || playerLevel > maxLevel) {
                    result = -4; // Invalid level
                }
            }

            // Send the result to the client
            session.send(MessageBuilder.createRoom((short) 0, result));

            // If everything is correct, create the room
            if (result == 0) {
                Room room = new Room();

                // Limit the length of the name and the password
                if (name.length() > MAX_ROOM_NAME_LENGTH) {
                    name = name.substring(0, MAX_ROOM_NAME_LENGTH);
                }

                if (password.length() > MAX_ROOM_PASSWORD_LENGTH) {
                    password = password.substring(0, MAX_ROOM_PASSWORD_LENGTH);
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
                room.setMinLevel(minLevel);
                room.setMaxLevel(maxLevel);
                room.setMap(map);
                room.setBall(ball);
                room.setMaxSize(maxSize);

                synchronized (RoomManager.ROOMS_LOCKER) {
                    // Get the room id
                    room.setId(RoomManager.getSmallestMissingIndex());
                    // Add it to the rooms list
                    RoomManager.addRoom(room);
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

        Room room = RoomManager.getRoomById(roomId);

        // Try to join the room.
        if (room != null) {
            room.tryJoinRoom(session, password);
        } else {
            // Result -3 means that the room does not exists.
            session.send(MessageBuilder.joinRoom(null, session.getPlayerId(), (short) -3));
        }
    }

    public static void quickJoinRoom(Session session) {
        // Ignore the message if the player is already in a room
        if (session.getRoomId() > 0) return;

        int playerId = session.getPlayerId();
        Room room = RoomManager.getQuickRoom(PlayerInfo.getLevel(playerId));

        // If a valid room was found
        if (room != null) {
            room.tryJoinRoom(session, "");
        } else {
            // Notify the player that no rooms were found
            session.send(MessageBuilder.quickJoinRoom((short) -2));
        }
    }

    public static void leaveRoom(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        Room room = RoomManager.getRoomById(roomId);

        if (room != null && room.isPlayerIn(playerId) && room.isInLobbyScreen()) {
            session.leaveRoom(RoomLeaveReason.LEAVED);
        }
    }

    public static void roomMap(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short mapId = msg.readShort();

        Room room = RoomManager.getRoomById(roomId);

        if (room != null && room.isPlayerIn(session.getPlayerId())) {
            RoomMap map = RoomMap.fromInt(mapId);

            if (map != null) {
                room.setMap(map);

                // Notify players in room that map changed
                room.broadcast(MessageBuilder.roomMap(mapId));
            }
        }
    }

    public static void roomBall(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short ballId = msg.readShort();

        Room room = RoomManager.getRoomById(roomId);

        if (room != null && room.isPlayerIn(session.getPlayerId())) {
            RoomBall ball = RoomBall.fromInt(ballId);

            if (ball != null) {
                room.setBall(ball);

                // Notify players in room that ball changed
                room.broadcast(MessageBuilder.roomBall(ballId));
            }
        }
    }

    public static void roomSettings(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        RoomAccessType type = RoomAccessType.fromShort(msg.readShort());
        String name = msg.readString(45);
        String password = msg.readString(5);
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

        // Check that the settings are valid

        short result = 0;

        Room room = RoomManager.getRoomById(roomId);

        ServerType serverType = ServerManager.getServerType();

        if (maxSize == null || type == null || roomMode == null ||
                roomMode.notValidForServer(serverType)) {
            result = -1; // System problem
        } else if (room == null) {
            result = -2; // Room does not exist
        } else if (room.getMaster() != session.getPlayerId()) {
            result = -3; // Player is not room's master
        } else if (maxSize.toInt() < room.getCurrentSize()) {
            result = -4; // Size is lower than players in room
        } else if (minLevel > maxLevel) {
            result = -5; // Wrong level settings
        } else if (!room.isValidMaxLevel(maxLevel)) {
            result = -7; // Invalid maximum level
        } else if (!room.isValidMinLevel(minLevel)) {
            result = -8; // Invalid minimum level
        } else {
            short playerLevel = PlayerInfo.getLevel(session.getPlayerId());

            if (playerLevel < minLevel || playerLevel > maxLevel) {
                result = -6; // Invalid level
            } else {
                // Limit the length of the name and the password
                if (name.length() > MAX_ROOM_NAME_LENGTH) {
                    name = name.substring(0, MAX_ROOM_NAME_LENGTH);
                }

                if (password.length() > MAX_ROOM_PASSWORD_LENGTH) {
                    password = password.substring(0, MAX_ROOM_PASSWORD_LENGTH);
                }

                // Update room settings
                room.setAccessType(type);
                room.setName(name);
                room.setPassword(password);
                room.setRoomMode(roomMode);
                room.setMinLevel(minLevel);
                room.setMaxLevel(maxLevel);
                room.setMaxSize(maxSize);

                room.broadcast(MessageBuilder.roomSettings(room, result));
            }
        }

        if (result != 0) {
            session.send(MessageBuilder.roomSettings(room, result));
        }
    }

    public static void swapTeam(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        Room room = RoomManager.getRoomById(roomId);

        // If the room is valid, the player is inside it and it is in waiting state
        if (room != null && room.isPlayerIn(playerId) && room.state() == RoomState.WAITING) {
            if (!room.getSwapLocker().isPlayerLocked(playerId)) {
                RoomTeam currentTeam = room.getPlayerTeam(playerId);
                RoomTeam newTeam = room.swapPlayerTeam(playerId, currentTeam);

                if (newTeam != currentTeam) {
                    room.getSwapLocker().lockPlayer(playerId);
                    room.broadcast(MessageBuilder.swapTeam(playerId, newTeam));
                }
            }
        }
    }

    public static void kickPlayer(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerToKick = msg.readInt();

        if (ServerManager.getServerType() != ServerType.CLUB) {
            short result = 0;

            Room room = RoomManager.getRoomById(session.getRoomId());

            // If the room exist and the player is inside it
            if (room != null && room.getId() == roomId) {
                // If the player is the room master
                if (room.getMaster() == session.getPlayerId() && room.isInLobbyScreen()) {
                    // If the player is in the room
                    if (room.isPlayerIn(playerToKick)) {
                        room.getPlayer(playerToKick).leaveRoom(RoomLeaveReason.KICKED);
                    } else {
                        result = -4; // Player not found
                    }
                } else {
                    result = -3; // Not the room master
                }
            } else {
                result = -2; // Invalid room
            }

            // If there is something wrong, notify the client
            if (result != 0) {
                session.send(MessageBuilder.kickPlayer(result));
            }
        }
    }

    public static void invitePlayer(Session session, ClientMessage msg) {
        Room room = RoomManager.getRoomById(session.getRoomId());

        // If the player is in a room
        if (room != null) {
            int playerToInvite = msg.readInt();

            short result = 0;

            if (room.isNotFull()) {
                // If the player to invite is in the main lobby
                if (LobbyManager.getMainLobby().getPlayers().contains(playerToInvite)) {
                    Session sessionToInvite = ServerManager.getSession(playerToInvite);

                    if (UserInfo.getSettings(sessionToInvite.getUserId()).getInvites()) {
                        byte level = (byte) PlayerInfo.getLevel(sessionToInvite.getPlayerId());

                        // If player level meets the level requirement of the room
                        if (room.getMinLevel() <= level && room.getMaxLevel() >= level) {
                            ServerMessage invitation = MessageBuilder.invitePlayer(result,
                                    room, session.getCache().getName());
                            sessionToInvite.sendAndFlush(invitation);
                        } else {
                            result = -5; // Player does not meet the level requirements
                        }
                    } else {
                        result = -3; // Player does not accept invitations
                    }
                } else {
                    result = -2; // Player not found
                }
            }

            // If there is something wrong, notify the client
            if (result != 0) {
                session.send(MessageBuilder.invitePlayer(result, null, ""));
            }
        }
    }

    public static void startCountDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            if (room != null) {
                int playerId = session.getPlayerId();
                byte type = msg.readByte();

                switch (type) {
                    case 1:
                        if (!room.getConfirmedPlayers().contains(playerId)) {
                            room.getConfirmedPlayers().add(playerId);
                        }

                        if (room.getConfirmedPlayers().size() >= room.getCurrentSize()) {
                            room.getConfirmedPlayers().clear();
                            room.broadcast(MessageBuilder.startCountDown((byte) 1));
                        }
                        break;
                    case -1:
                        if (room.isWaiting() && room.getMaster() == playerId) {
                            room.startCountdown();
                        }
                        break;
                    default:
                }
            }
        }
    }

    public static void hostInfo(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            if (room.getHost() == session.getPlayerId()) {
                room.determineMatchMission();
                room.sendHostInfo();
            }
        }
    }

    public static void countDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short count = msg.readShort();

        Room room = RoomManager.getRoomById(roomId);

        if (room != null && room.getMaster() == session.getPlayerId()) {
            room.onCountdown(count);
        }
    }

    public static void cancelCountDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            if (room != null && room.getMaster() == session.getPlayerId()) {
                room.cancelCountdown();
            }
        }
    }

    public static void matchLoading(Session session, ClientMessage msg) {
        msg.readInt();
        int playerId = session.getPlayerId();
        int roomId = msg.readShort();
        short status = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            if (room.isLoading()) {
                room.broadcast(MessageBuilder.matchLoading(playerId, roomId, status));
            }
        }
    }

    public static void playerReady(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            if (room.isLoading()) {
                if (!room.getConfirmedPlayers().contains(playerId)) {
                    room.getConfirmedPlayers().add(playerId);

                    // Instead of waiting 5 seconds (or not), we send an udp ping immediately to
                    // the client so we can update his udp port (if changed) before match starts
                    UdpPing.sendUdpPing(session);
                }

                if (room.getConfirmedPlayers().size() >= room.getCurrentSize()) {
                    room.setState(RoomState.PLAYING);
                    room.setTimeStart(DateUtils.currentTimeMillis());
                    room.broadcast(MessageBuilder.playerReady((short) 0));

                    if (room.getLoadingTimeoutFuture().isCancellable()) {
                        room.getLoadingTimeoutFuture().cancel(true);
                    }
                }
            } else {
                session.send(MessageBuilder.playerReady((short) 0));
            }
        }
    }

    public static void startMatch(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            short result = 0;

            if (room.isLoading() && room.getConfirmedPlayers().size() < room.getCurrentSize()) {
                result = -1;
            }

            session.send(MessageBuilder.startMatch(result));
        }
    }

    public static void matchResult(Session session, ClientMessage msg) {
        final int roomId = msg.readShort();
        msg.ignoreBytes(4);

        if (session.getRoomId() != roomId) return;

        Room room = RoomManager.getRoomById(roomId);

        if (room.state() != RoomState.PLAYING) return;

        room.setState(RoomState.RESULT);

        MatchResult result = MatchResult.fromMessage(msg, room.getTeamSizes());

        try (MatchResultHandler resultHandler = new MatchResultHandler(room, result)) {
            resultHandler.handleResult();
        } catch (Exception e) {
            Output.println("Match result exception: " + e.getMessage(), Level.DEBUG);
        }

        room.getConfirmedPlayers().clear();
    }

    public static void unknown1(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);
            if (room != null && room.getMaster() == session.getPlayerId()) {
                room.setState(RoomState.WAITING);
                room.broadcast(MessageBuilder.unknown1());
            }
        }
    }

    public static void toRoomLobby(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);
            if (room != null && room.getMaster() == session.getPlayerId()) {
                room.broadcast(MessageBuilder.toRoomLobby());

                if (GameEvents.isGoldenTime() || GameEvents.isClubTime()) {
                    room.broadcast(MessageBuilder.nextTip("", (short) 0));
                }
            }
        }
    }

    public static void cancelLoading(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);
            if (room != null && room.isLoading() &&
                    (room.getHost() == playerId || room.getMaster() == playerId)) {
                room.setState(RoomState.WAITING);
                room.broadcast(MessageBuilder.cancelLoading());
            }
        }
    }
}
