package com.neikeq.kicksemu.game.rooms.messages;

import com.neikeq.kicksemu.game.chat.ChatUtils;
import com.neikeq.kicksemu.game.rooms.ChallengeRoom;
import com.neikeq.kicksemu.game.rooms.challenges.Challenge;
import com.neikeq.kicksemu.game.rooms.challenges.ChallengeOrganizer;
import com.neikeq.kicksemu.game.rooms.enums.RoomBall;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.enums.RoomMap;
import com.neikeq.kicksemu.game.rooms.enums.RoomState;
import com.neikeq.kicksemu.game.rooms.match.ChallengeResultHandler;
import com.neikeq.kicksemu.game.rooms.match.MatchResult;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.server.udp.UdpPing;
import com.neikeq.kicksemu.storage.ConnectionRef;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.game.events.GameEvents;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.Optional;

public class ChallengeRoomMessages extends RoomMessages {

    private static Optional<ChallengeRoom> getRoomById(int roomId) {
        return ChallengeOrganizer.getChallengeById(roomId).map(Challenge::getRoom);
    }

    public static void leaveRoom(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int player = session.getPlayerId();

        getRoomById(roomId).filter(room -> room.isPlayerIn(player) && room.isInLobbyScreen())
                .ifPresent(room -> {
                    session.sendAndFlush(MessageBuilder.leaveRoom(player, RoomLeaveReason.LEAVED));
                    session.leaveRoom(RoomLeaveReason.LEAVED);
                });
    }

    public static void roomMap(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short mapId = msg.readShort();

        getRoomById(roomId).filter(room -> room.isPlayerIn(session.getPlayerId()))
                .ifPresent(room ->
                        RoomMap.fromInt(mapId).ifPresent(map -> {
                            room.setMap(map);

                            // Notify players in room that map changed
                            room.broadcast(MessageBuilder.roomMap(mapId));
                        })
                );
    }

    public static void roomBall(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short ballId = msg.readShort();

        getRoomById(roomId).filter(room -> room.isPlayerIn(session.getPlayerId()))
                .ifPresent(room ->
                        RoomBall.fromInt(ballId).ifPresent(ball -> {
                            room.setBall(ball);

                            // Notify players in room that ball changed
                            room.broadcast(MessageBuilder.roomBall(ballId));
                        })
                );
    }

    public static void startCountDown(Session session, ClientMessage msg) {
        if (!GameEvents.isClubTime()) {
            ChatUtils.sendServerMessage(session, "Club time is not active.");
            return;
        }

        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        getRoomById(roomId).filter(room -> room.isPlayerIn(playerId))
                .ifPresent(room -> {
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

    public static void hostInfo(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        getRoomById(roomId).filter(room -> room.getHost() == session.getPlayerId())
                .ifPresent(room -> {
                    room.determineMatchMission();
                    room.broadcastHostInfo();
                });
    }

    public static void countDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short count = msg.readShort();

        getRoomById(roomId).filter(room -> room.getMaster() == session.getPlayerId())
                .ifPresent(room -> room.onCountdown(count));
    }

    public static void cancelCountDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        getRoomById(roomId).filter(room -> room.getMaster() == session.getPlayerId())
                .ifPresent(ChallengeRoom::cancelCountdown);
    }

    public static void matchLoading(Session session, ClientMessage msg) {
        msg.readInt();
        int playerId = session.getPlayerId();
        int roomId = msg.readShort();
        short status = msg.readShort();

        getRoomById(roomId).filter(room -> room.isLoading() && room.isPlayerIn(playerId))
                .ifPresent(room ->
                        room.broadcast(MessageBuilder.matchLoading(playerId, roomId, status)));
    }

    public static void playerReady(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        MutableBoolean roomFound = new MutableBoolean(false);

        getRoomById(roomId).filter(room -> room.isLoading() && room.isPlayerIn(playerId))
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

                    roomFound.setTrue();
                });

        if (roomFound.isFalse()) {
            session.send(MessageBuilder.playerReady((short) 0));
        }
    }

    public static void startMatch(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        short result = getRoomById(roomId)
                .filter(room -> room.isLoading() &&
                        (room.getConfirmedPlayers().size() < room.getCurrentSize()))
                .map(room -> (short) -1).orElse((short) 0);

        session.send(MessageBuilder.startMatch(result));
    }

    public static void matchResult(Session session, ClientMessage msg) {
        final int roomId = msg.readShort();
        msg.ignoreBytes(4);

        getRoomById(roomId)
                .filter(room -> room.isPlayerIn(session.getPlayerId()) &&
                        (room.state() == RoomState.PLAYING))
                .ifPresent(room -> {
                    MatchResult result = MatchResult.fromMessage(msg, room.getPlayers().keySet());
                    handleMatchResult(room, result);
                });
    }

    private static void handleMatchResult(ChallengeRoom room, MatchResult result) {
        room.setState(RoomState.RESULT);

        try (ConnectionRef con = ConnectionRef.ref()) {
            ChallengeResultHandler resultHandler = new ChallengeResultHandler(room, result, con);
            resultHandler.handleResult();
        } catch (Exception e) {
            Output.println("Match result exception: " + e.getMessage(), Level.DEBUG);
        }

        room.getConfirmedPlayers().clear();
    }

    public static void unknown1(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        getRoomById(roomId).filter(room -> room.getMaster() == session.getPlayerId())
                .ifPresent(room -> {
                    room.setState(RoomState.WAITING);
                    room.broadcast(MessageBuilder.unknown1());
                });
    }

    public static void toRoomLobby(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        getRoomById(roomId).filter(room -> room.getMaster() == session.getPlayerId())
                .ifPresent(room -> {
                    room.broadcast(MessageBuilder.toRoomLobby());

                    if (GameEvents.isGoldenTime() || GameEvents.isClubTime()) {
                        room.broadcast(MessageBuilder.nextTip("", (short) 0));
                    }
                });
    }

    public static void cancelLoading(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        getRoomById(roomId).filter(room -> room.isLoading() &&
                ((room.getHost() == playerId) || (room.getMaster() == playerId)))
                .ifPresent(room -> {
                    room.setState(RoomState.WAITING);
                    room.broadcast(MessageBuilder.cancelLoading());
                });
    }
}
