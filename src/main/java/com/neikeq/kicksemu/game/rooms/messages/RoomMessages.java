package com.neikeq.kicksemu.game.rooms.messages;

import com.neikeq.kicksemu.config.Configuration;
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
import com.neikeq.kicksemu.storage.ConnectionRef;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.game.events.GameEvents;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.Optional;

public class RoomMessages {

    private static final int MAX_ROOM_NAME_LENGTH = 30;
    static final int MAX_ROOM_PASSWORD_LENGTH = 4;
    static final byte MAX_ROOM_LEVEL = 60;
    static final byte MIN_ROOM_LEVEL = 1;

    private static class RoomSettings {

        String name = "Welcome";
        String password = "";
        RoomAccessType accessType = RoomAccessType.FREE;
        RoomMode mode = RoomMode.AI_GOALKEEPER;
        RoomSize maxSize = RoomSize.SIZE_4V4;
        byte minLevel = MIN_ROOM_LEVEL;
        byte maxLevel = MAX_ROOM_LEVEL;
    }

    public static void roomList(Session session, ClientMessage msg) {
        short page = msg.readShort();
        session.send(MessageBuilder.roomList(RoomManager.getRoomsFromPage(page),
                page, (short) 0));
    }

    public static void createRoom(Session session, ClientMessage msg) {
        if (session.getRoomId() > 0) {
            return;
        }

        short result = RoomAccessType.fromShort(msg.readShort()).map(type -> {
            String name = msg.readString(45);
            String password = msg.readString(5);

            return RoomMode.fromInt(msg.readByte())
                    .filter(rm -> rm.isValidForServer(ServerManager.getServerType()))
                    .map(roomMode -> {
                        byte minLevel = msg.readByte();
                        byte maxLevel = msg.readByte();

                        if ((minLevel < MIN_ROOM_LEVEL) || (maxLevel > MAX_ROOM_LEVEL)) {
                            return (short) -3; // Wrong level settings
                        }

                        msg.ignoreBytes(4); // Ignore ball and map and use default

                        return RoomSize.fromInt(msg.readByte())
                                .filter(rs -> rs != RoomSize.SIZE_2V2)
                                .map(maxSize -> {
                                    short level = PlayerInfo.getLevel(session.getPlayerId());

                                    if ((level < minLevel) || (level > maxLevel)) {
                                        return (short) -4; // Invalid level
                                    }

                                    RoomSettings settings = new RoomSettings();

                                    settings.name = name;
                                    settings.password = password;
                                    settings.accessType = type;
                                    settings.mode = roomMode;
                                    settings.maxSize = maxSize;
                                    settings.minLevel = minLevel;
                                    settings.maxLevel = maxLevel;

                                    createRoom(session, settings);

                                    return (short) 0;
                                }).orElse((short) -1);
                    }).orElse((short) -1);
        }).orElse((short) -1);

        // Send the result to the client
        session.send(MessageBuilder.createRoom((short) 0, result));
    }

    private static void createRoom(Session session, RoomSettings settings) {
        Room room = new Room();

        // Limit the length of the name and the password
        if (settings.name.length() > MAX_ROOM_NAME_LENGTH) {
            settings.name = settings.name.substring(0, MAX_ROOM_NAME_LENGTH);
        }

        if (settings.password.length() > MAX_ROOM_PASSWORD_LENGTH) {
            settings.password = settings.password.substring(0, MAX_ROOM_PASSWORD_LENGTH);
        }

        // If password is blank, disable password usage
        if ((settings.accessType == RoomAccessType.PASSWORD) && settings.password.isEmpty()) {
            settings.accessType = RoomAccessType.FREE;
        }

        // Set room information from received data
        room.setName(settings.name);
        room.setPassword(settings.password);
        room.setAccessType(settings.accessType);
        room.setRoomMode(settings.mode);
        room.setMinLevel(settings.minLevel);
        room.setMaxLevel(settings.maxLevel);
        room.setMaxSize(settings.maxSize);

        synchronized (RoomManager.ROOMS_LOCKER) {
            // Get the room id
            room.setId(RoomManager.getSmallestMissingIndex());
            // Add it to the rooms list
            RoomManager.addRoom(room);
            // Add the player to the room
            room.addPlayer(session);
        }

        // Notify the client to join the room
        session.send(MessageBuilder.joinRoom(Optional.of(room), session.getPlayerId(), (short) 0));
    }

    public static void joinRoom(Session session, ClientMessage msg) {
        if (session.getRoomId() > 0) return;

        int roomId = msg.readShort();
        String password = msg.readString(4);

        Optional<Room> room = RoomManager.getRoomById(roomId);

        // Try to join the room.
        if (room.isPresent()) {
            room.get().tryJoinRoom(session, password);
        } else {
            // Result -3 means that the room does not exists.
            session.send(MessageBuilder.joinRoom(null, session.getPlayerId(), (short) -3));
        }
    }

    public static void quickJoinRoom(Session session) {
        // Ignore the message if the player is already in a room
        if (session.getRoomId() > 0) return;

        Optional<Room> room = RoomManager.getQuickRoom(PlayerInfo.getLevel(session.getPlayerId()));

        // If a valid room was found
        if (room.isPresent()) {
            room.get().tryJoinRoom(session, "");
        } else {
            // Notify the player that no rooms were found
            session.send(MessageBuilder.quickJoinRoom((short) -2));
        }
    }

    public static void leaveRoom(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        RoomManager.getRoomById(roomId)
                .filter(room -> room.isPlayerIn(playerId) && room.isInLobbyScreen())
                .ifPresent(room -> session.leaveRoom(RoomLeaveReason.LEAVED));
    }

    public static void roomMap(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short mapId = msg.readShort();

        RoomManager.getRoomById(roomId)
                .filter(room -> room.isPlayerIn(session.getPlayerId()))
                .ifPresent(room ->
                        RoomMap.fromInt(mapId).ifPresent(map -> {
                            room.setMap(map);

                            // Notify players in room that map changed
                            room.broadcast(MessageBuilder.roomMap(mapId));
                        }));
    }

    public static void roomBall(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short ballId = msg.readShort();

        RoomManager.getRoomById(roomId)
                .filter(room -> room.isPlayerIn(session.getPlayerId()))
                .ifPresent(room ->
                        RoomBall.fromInt(ballId).ifPresent(ball -> {
                            room.setBall(ball);

                            // Notify players in room that ball changed
                            room.broadcast(MessageBuilder.roomBall(ballId));
                        }));
    }

    public static void roomSettings(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        short result = RoomManager.getRoomById(roomId).map(room ->
                RoomAccessType.fromShort(msg.readShort()).map(type -> {
                    String name = msg.readString(45);
                    String password = msg.readString(5);

                    if (room.getMaster() != session.getPlayerId()) {
                        return (short) -3; // Player is not room's master
                    }


                    Optional<RoomMode> roomMode = RoomMode.fromInt(msg.readByte())
                            .filter(rm -> rm.isValidForServer(ServerManager.getServerType()));
                    byte minLevel = msg.readByte();
                    byte maxLevel = msg.readByte();
                    Optional<RoomSize> maxSize = RoomSize.fromInt(msg.readByte())
                            .filter(ms -> ms != RoomSize.SIZE_2V2);

                    if (!maxSize.isPresent() || !roomMode.isPresent()) {
                        return (short) -1;
                    }

                    if (minLevel > maxLevel) {
                        return (short) -5; // Wrong level settings
                    }

                    if (minLevel < MIN_ROOM_LEVEL) {
                        minLevel = MIN_ROOM_LEVEL;
                    }

                    if (maxLevel > MAX_ROOM_LEVEL) {
                        maxLevel = MAX_ROOM_LEVEL;
                    }

                    if (maxSize.filter(ms -> ms.toInt() < room.getCurrentSize()).isPresent()) {
                        return (short) -4; // Size is lower than players in room
                    }

                    if (!room.isValidMaxLevel(maxLevel)) {
                        return (short) -7; // Invalid maximum level
                    }

                    if (!room.isValidMinLevel(minLevel)) {
                        return (short) -8; // Invalid minimum level
                    }

                    short playerLevel = PlayerInfo.getLevel(session.getPlayerId());

                    if ((playerLevel < minLevel) || (playerLevel > maxLevel)) {
                        return (short) -6; // Invalid level
                    }

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
                    room.setRoomMode(roomMode.get());
                    room.setMinLevel(minLevel);
                    room.setMaxLevel(maxLevel);
                    room.setMaxSize(maxSize.get());

                    room.broadcast(MessageBuilder.roomSettings(Optional.of(room), (short) 0));

                    return (short) 0;
                }).orElse((short) -1)
        ).orElse((short) -2);

        if (result != 0) {
            session.send(MessageBuilder.roomSettings(Optional.empty(), result));
        }
    }

    public static void swapTeam(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        RoomManager.getRoomById(roomId)
                .filter(r -> r.isPlayerIn(playerId) && (r.state() == RoomState.WAITING) &&
                        !r.getSwapLocker().isPlayerLocked(playerId))
                .ifPresent(room ->
                        room.getPlayerTeam(playerId).ifPresent(currentTeam -> {
                            RoomTeam newTeam = room.swapPlayerTeam(playerId, currentTeam);

                            if (newTeam != currentTeam) {
                                room.getSwapLocker().lockPlayer(playerId);
                                room.broadcast(MessageBuilder.swapTeam(playerId, newTeam));
                            }
                        })
                );
    }

    public static void kickPlayer(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerToKick = msg.readInt();

        if (ServerManager.getServerType() != ServerType.CLUB) {
            short result = RoomManager.getRoomById(session.getRoomId())
                    .filter(room -> room.getId() == roomId)
                    .map(room -> {
                        // If the player is the room master
                        if ((room.getMaster() == session.getPlayerId()) && room.isInLobbyScreen()) {
                            // If the player is in the room
                            if (room.isPlayerIn(playerToKick)) {
                                room.getPlayer(playerToKick).leaveRoom(RoomLeaveReason.KICKED);
                            } else {
                                return (short) -4; // Player not found
                            }
                        } else {
                            return (short) -3; // Not the room master
                        }

                        return (short) 0;
                    }).orElse((short) -2);

            // If there is something wrong, notify the client
            if (result != 0) {
                session.send(MessageBuilder.kickPlayer(result));
            }
        }
    }

    public static void invitePlayer(Session session, ClientMessage msg) {
        RoomManager.getRoomById(session.getRoomId()).ifPresent(room -> {
            int playerToInvite = msg.readInt();

            short result = 0;

            if (room.isNotFull()) {
                // If the player to invite is in the main lobby
                result = !LobbyManager.getMainLobby().getPlayers().contains(playerToInvite) ?
                        -2 :
                        ServerManager.getSession(playerToInvite).map(targetSession -> {
                            if (UserInfo.getSettings(targetSession.getUserId()).getInvites()) {
                                short level = PlayerInfo.getLevel(targetSession.getPlayerId());

                                // If player level meets the level requirement of the room
                                if ((room.getMinLevel() <= level) && (room.getMaxLevel() >= level)) {
                                    ServerMessage invitation = MessageBuilder.invitePlayer((short) 0,
                                            room, session.getCache().getName());
                                    targetSession.sendAndFlush(invitation);
                                } else {
                                    return (short) -5; // Player does not meet the level requirements
                                }
                            } else {
                                return (short) -3; // Player does not accept invitations
                            }

                            return (short) 0;
                        }).orElse((short) -2);
            }

            // If there is something wrong, notify the client
            if (result != 0) {
                session.send(MessageBuilder.invitePlayer(result, null, ""));
            }
        });
    }

    public static void startCountDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            RoomManager.getRoomById(roomId).ifPresent(room -> {
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
                        if (room.isWaiting() && (room.getMaster() == playerId)) {
                            room.startCountdown();
                        }
                        break;
                    default:
                }
            });
        }
    }

    public static void hostInfo(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        RoomManager.getRoomById(roomId)
                .filter(room -> room.getHost() == session.getPlayerId())
                .ifPresent(room -> {
                    if (room.isInLobbyScreen()) {
                        room.determineMatchMission();
                    }
                    room.broadcastHostInfo();
                });
    }

    public static void countDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short count = msg.readShort();

        RoomManager.getRoomById(roomId)
                .filter(room -> room.getMaster() == session.getPlayerId())
                .ifPresent(room -> room.onCountdown(count));
    }

    public static void cancelCountDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            RoomManager.getRoomById(roomId)
                    .filter(room -> room.getMaster() == session.getPlayerId())
                    .ifPresent(Room::cancelCountdown);
        }
    }

    public static void matchLoading(Session session, ClientMessage msg) {
        msg.readInt();
        int player = session.getPlayerId();
        int roomId = msg.readShort();
        short status = msg.readShort();

        if (session.getRoomId() == roomId) {
            RoomManager.getRoomById(roomId)
                    .filter(Room::isLoading)
                    .ifPresent(room ->
                            room.broadcast(MessageBuilder.matchLoading(player, roomId, status)));
        }
    }

    public static void playerReady(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        if (session.getRoomId() == roomId) {
            MutableBoolean roomLoading = new MutableBoolean(false);

            RoomManager.getRoomById(roomId)
                    .filter(Room::isLoading)
                    .ifPresent(room -> {
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

                        roomLoading.setTrue();
                    });

            if (roomLoading.isFalse()) {
                session.send(MessageBuilder.playerReady((short) 0));
            }
        }
    }

    public static void startMatch(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            short result = RoomManager.getRoomById(roomId)
                    .filter(r -> r.isLoading() &&
                            (r.getConfirmedPlayers().size() < r.getCurrentSize()))
                    .map(room -> (short) -1)
                    .orElse((short) 0);

            session.send(MessageBuilder.startMatch(result));
        }
    }

    public static void matchResult(Session session, ClientMessage msg) {
        final int roomId = msg.readShort();
        msg.ignoreBytes(4);

        RoomManager.getRoomById(roomId)
                .filter(room -> (room.getHost() == session.getPlayerId()) &&
                        (room.state() == RoomState.PLAYING))
                .ifPresent(room -> {
                    room.setState(RoomState.RESULT);

                    MatchResult result = MatchResult.fromMessage(msg, room.getPlayers().keySet());

                    try (ConnectionRef con = ConnectionRef.ref()) {
                        MatchResultHandler handler = new MatchResultHandler(room, result, con);
                        handler.handleResult();
                    } catch (Exception e) {
                        Output.println("Match result exception: " + e.getMessage(), Level.DEBUG);
                    }

                    room.getConfirmedPlayers().clear();
                });
    }

    public static void matchForcedResult(Session session, ClientMessage msg) {
        boolean handleResult = true;

        if (!Configuration.getBoolean("game.match.result.force")) {
            handleResult = RoomManager.getRoomById(session.getPlayerId())
                    .filter(Room::isForcedResultAllowed).isPresent();
        }

        if (handleResult) {
            matchResult(session, msg);
        }
    }

    public static void unknown1(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            RoomManager.getRoomById(roomId)
                    .filter(room -> room.getMaster() == session.getPlayerId())
                    .ifPresent(room -> {
                        room.setState(RoomState.WAITING);
                        room.broadcast(MessageBuilder.unknown1());
                    });
        }
    }

    public static void toRoomLobby(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            RoomManager.getRoomById(roomId)
                    .filter(room -> room.getMaster() == session.getPlayerId())
                    .ifPresent(room -> {
                        room.broadcast(MessageBuilder.toRoomLobby());

                        if (GameEvents.isGoldenTime() || GameEvents.isClubTime()) {
                            room.broadcast(MessageBuilder.nextTip("", (short) 0));
                        }
                    });
        }
    }

    public static void cancelLoading(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        if (session.getRoomId() == roomId) {
            RoomManager.getRoomById(roomId)
                    .filter(room -> room.isLoading() &&
                            ((room.getHost() == playerId) || (room.getMaster() == playerId)))
                    .ifPresent(room -> {
                        room.setState(RoomState.WAITING);
                        room.broadcast(MessageBuilder.cancelLoading());
                    });
        }
    }
}
