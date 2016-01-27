package com.neikeq.kicksemu.game.rooms.messages;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.rooms.RoomSettings;
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

    public static void roomList(Session session, ClientMessage msg) {
        short page = (short) Math.min(msg.readShort(), RoomManager.getPagesCount());
        session.send(MessageBuilder.roomList(RoomManager.getRoomsFromPage(page), page));
    }

    public static void createRoom(Session session, ClientMessage msg) {
        if (session.getRoomId() > 0) {
            return;
        }

        short result = RoomAccessType.fromShort(msg.readShort()).map(type -> {
            RoomSettings settings = new RoomSettings();

            settings.setAccessType(type);
            settings.setName(msg.readString(45));
            settings.setPassword(msg.readString(5));

            return RoomMode.fromInt(msg.readByte())
                    .filter(rm -> rm.isValidForServer(ServerManager.getServerType()))
                    .map(roomMode -> {
                        settings.setRoomMode(roomMode);
                        settings.setMinLevel(msg.readByte());
                        settings.setMaxLevel(msg.readByte());

                        if (settings.areInvalidLevelSettings()) {
                            return (short) -3; // Wrong level settings
                        }

                        msg.ignoreBytes(4); // Ignore ball and map and use default

                        return RoomSize.fromInt(msg.readByte())
                                .filter(rs -> rs != RoomSize.SIZE_2V2)
                                .map(maxSize -> {
                                    settings.setMaxSize(maxSize);

                                    short level = PlayerInfo.getLevel(session.getPlayerId());

                                    if ((level < settings.getMinLevel()) ||
                                            (level > settings.getMaxLevel())) {
                                        return (short) -4; // Invalid level
                                    }

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

        room.setSettings(settings);

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
        if (session.getRoomId() > 0) {
            return;
        }

        int roomId = msg.readShort();
        String password = msg.readString(4);

        short result = RoomManager.getRoomById(roomId)
                .map(room -> room.tryJoinRoom(session, password))
                .orElse((short) -3);

        if (result != 0) {
            session.send(MessageBuilder.joinRoom(Optional.empty(), session.getPlayerId(), result));
        }
    }

    public static void quickJoinRoom(Session session) {
        // Ignore the message if the player is already in a room
        if (session.getRoomId() > 0) {
            return;
        }

        short result = RoomManager.getQuickRoom(PlayerInfo.getLevel(session.getPlayerId()))
                .map(room -> room.tryJoinRoom(session, ""))
                .orElse((short) -2);

        if (result != 0) {
            session.send(MessageBuilder.clubQuickJoinRoom(result));
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

        short result = RoomManager.getRoomById(roomId).map(room -> {
            if (room.getMaster() != session.getPlayerId()) {
                return (short) -3; // Player is not room's master
            }

            return RoomAccessType.fromShort(msg.readShort()).map(type -> {
                RoomSettings settings = new RoomSettings();

                settings.setAccessType(type);
                settings.setName(msg.readString(45));
                settings.setPassword(msg.readString(5));

                Optional<RoomMode> roomMode = RoomMode.fromInt(msg.readByte())
                        .filter(rm -> rm.isValidForServer(ServerManager.getServerType()));

                settings.setMinLevel(msg.readByte());
                settings.setMaxLevel(msg.readByte());

                Optional<RoomSize> maxSize = RoomSize.fromInt(msg.readByte())
                        .filter(ms -> ms != RoomSize.SIZE_2V2);

                if (maxSize.isPresent() && roomMode.isPresent()) {
                    settings.setRoomMode(roomMode.get());
                    settings.setMaxSize(maxSize.get());
                } else {
                    return (short) -1;
                }

                if (settings.areInvalidLevelSettings()) {
                    return (short) -5;
                }

                if (maxSize.filter(ms -> ms.toInt() < room.getCurrentSize()).isPresent()) {
                    return (short) -4; // Size is lower than players in room
                }

                // Skipping needless result -6: Level settings are incompatible with player level

                if (!room.isValidMaxLevel(settings.getMaxLevel())) {
                    return (short) -7; // Invalid maximum level
                }

                if (!room.isValidMinLevel(settings.getMinLevel())) {
                    return (short) -8; // Invalid minimum level
                }

                room.setSettings(settings);

                room.broadcast(MessageBuilder.roomSettings(Optional.of(room), (short) 0));

                return (short) 0;
            }).orElse((short) -1);
        }).orElse((short) -2);

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

        if (ServerManager.getServerType() == ServerType.CLUB) {
            return;
        }

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
                .filter(r ->
                        ((r.getTrainingFactor() < 0) || (r.getHost() == session.getPlayerId())) &&
                                (r.state() == RoomState.PLAYING))
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
