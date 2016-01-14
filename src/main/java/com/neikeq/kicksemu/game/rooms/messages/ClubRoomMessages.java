package com.neikeq.kicksemu.game.rooms.messages;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.chat.ChatUtils;
import com.neikeq.kicksemu.game.clubs.MemberInfo;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.rooms.ChallengeRoom;
import com.neikeq.kicksemu.game.rooms.ClubRoom;
import com.neikeq.kicksemu.game.rooms.ClubRoomSettings;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.rooms.TeamManager;
import com.neikeq.kicksemu.game.rooms.challenges.Challenge;
import com.neikeq.kicksemu.game.rooms.challenges.ChallengeOrganizer;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.enums.RoomMode;
import com.neikeq.kicksemu.game.rooms.enums.RoomAccessType;
import com.neikeq.kicksemu.game.rooms.enums.RoomState;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.game.events.GameEvents;

import java.util.Map;
import java.util.Optional;

public class ClubRoomMessages extends RoomMessages {

    public static void roomList(Session session, ClientMessage msg) {
        short page = msg.readShort();
        session.send(MessageBuilder.clubRoomList(RoomManager.getRoomsFromPage(page), page));
    }

    public static void createRoom(Session session, ClientMessage msg) {
        if (!GameEvents.isClubTime()) {
            ChatUtils.sendServerMessage(session, "Club time is not active.");
            return;
        }

        if (session.getRoomId() > 0) {
            return;
        }

        short result = RoomAccessType.fromShort(msg.readShort()).map(type -> {
            ClubRoomSettings settings = new ClubRoomSettings();

            settings.setAccessType(type);
            settings.setName(msg.readString(15));
            settings.setPassword(msg.readString(5));

            return RoomMode.fromInt(msg.readByte())
                    .filter(rm -> rm.isValidForServer(ServerManager.getServerType()))
                    .map(roomMode -> {
                        settings.setRoomMode(roomMode);

                        // TODO Check result -2: Too many players in the opponent team. What does that even mean?

                        int playerId = session.getPlayerId();

                        if (PlayerInfo.getLevel(playerId) < ClubRoomSettings.MIN_ROOM_LEVEL) {
                            return (short) -3; // Does not meet the level requirements
                        }

                        int clubId = MemberInfo.getClubId(playerId);

                        if (clubId <= 0) {
                            return (short) -4; // Not a club member
                        }

                        if (RoomManager.getRoomById(clubId).isPresent()) {
                            return (short) -5; // The club already has a team
                        }

                        createRoom(session, clubId, settings);

                        return (short) 0;
                    }).orElse((short) -1);
        }).orElse((short) -1);

        session.send(MessageBuilder.clubCreateRoom((short) 0, result));
    }

    private static void createRoom(Session session, int clubId, ClubRoomSettings settings) {
        ClubRoom room = new ClubRoom();

        room.setSettings(settings);

        synchronized (RoomManager.ROOMS_LOCKER) {
            // Get the room id
            room.setId(clubId);
            // Add it to the rooms list
            RoomManager.addRoom(room);
            // Add the player to the room
            room.addPlayer(session);
        }
    }

    public static void joinRoom(Session session, ClientMessage msg) {
        if (session.getRoomId() > 0) return;

        int roomId = msg.readShort();
        String password = msg.readString(4);

        short result = RoomManager.getRoomById(roomId)
                .map(room -> room.tryJoinRoom(session, password))
                .orElse((short) -3);

        if (result != 0) {
            session.send(MessageBuilder.clubJoinRoom(Optional.empty(), (short) -3));
        }
    }

    public static void quickJoinRoom(Session session) {
        // Ignore the message if the player is already in a room
        if (session.getRoomId() > 0) return;

        int playerId = session.getPlayerId();
        short result = RoomManager.getRoomById(MemberInfo.getClubId(playerId))
                .map(room -> room.tryJoinRoom(session, "")).orElse((short) -2);

        if (result != 0) {
            session.send(MessageBuilder.clubQuickJoinRoom((short) -2));
        }
    }

    public static void kickPlayer(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerToKick = msg.readInt();

        short result = RoomManager.getRoomById(session.getRoomId()).map(room -> {
            short directResult = 0;

            // If the room exist and the player is inside it
            if (room.getId() == roomId) {
                // If the player is the room master
                if ((room.getMaster() == session.getPlayerId()) && room.isInLobbyScreen()) {
                    // If the player is in the room
                    if (room.isPlayerIn(playerToKick)) {
                        room.getPlayer(playerToKick).leaveRoom(RoomLeaveReason.KICKED);
                    } else {
                        directResult = -4; // Player not found
                    }
                } else {
                    directResult = -3; // Not the room master
                }
            } else {
                directResult = -2; // Invalid room
            }

            return directResult;
        }).orElse((short) -2);

        // If there is something wrong, notify the client
        if (result != 0) {
            session.send(MessageBuilder.clubKickPlayer(result));
        }
    }

    public static void roomSettings(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        // Check that settings are valid

        short result = RoomManager.getRoomById(roomId).map(room ->
                RoomAccessType.fromShort(msg.readShort()).map(type -> {
                    ClubRoomSettings settings = new ClubRoomSettings();

                    settings.setAccessType(type);
                    settings.setName(msg.readString(15));
                    settings.setPassword(msg.readString(5));

                    if (room.getMaster() != session.getPlayerId()) {
                        // Player is not the room master (displays the same as -2 though...)
                        return (short) -3;
                    } else {
                        // Update room settings
                        room.setAccessType(settings.getAccessType());
                        room.setName(settings.getName());
                        room.setPassword(settings.getPassword());

                        room.broadcast(MessageBuilder.clubRoomSettings(Optional.of(room), (short) 0));
                    }

                    return (short) 0;
                }).orElse((short) -1)
        ).orElse((short) -2);

        if (result != 0) {
            session.send(MessageBuilder.clubRoomSettings(Optional.empty(), result));
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
                        ServerManager.getSession(playerToInvite).map(sessionToInvite -> {
                            short directResult = 0;

                            if (UserInfo.getSettings(sessionToInvite.getUserId()).getInvites()) {
                                int targetClubId = MemberInfo.getClubId(playerToInvite);

                                // If the target player is a member of the club
                                if (targetClubId == room.getId()) {
                                    sessionToInvite.sendAndFlush(
                                            MessageBuilder.clubInvitePlayer((short) 0, room,
                                                    session.getCache().getName()));
                                } else {
                                    directResult = -6; // Target is not a member of the club
                                }
                            } else {
                                directResult = -3; // Target does not accept invitations
                            }

                            return directResult;
                        }).orElse((short) -2);
            }

            // If there is something wrong, notify the client
            if (result != 0) {
                session.send(MessageBuilder.clubInvitePlayer(result, null, ""));
            }
        });
    }

    public static void registerTeam(Session session, ClientMessage msg) {
        if (!GameEvents.isClubTime()) {
            ChatUtils.sendServerMessage(session, "Club time is not active.");
            return;
        }

        int roomId = msg.readShort();

        if ((roomId == session.getRoomId()) && !TeamManager.isRegistered(roomId)) {
            short result = RoomManager.getRoomById(roomId).map(room -> {
                short directResult = 0;

                if (room.state() == RoomState.APPLYING) {
                    directResult = -1;
                } else if (room.getCurrentSize() == ClubRoom.REQUIRED_TEAM_PLAYERS) {
                    TeamManager.register((ClubRoom) room);
                    room.broadcast(MessageBuilder.clubRegisterTeam((short) 0));
                } else {
                    directResult = -3; // Not enough players
                }

                return directResult;
            }).orElse((short) -2);

            if (result != 0) {
                session.send(MessageBuilder.clubRegisterTeam(result));
            }
        }
    }

    public static void unregisterTeam(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if ((roomId == session.getRoomId()) && TeamManager.isRegistered(roomId)) {
            short result = RoomManager.getRoomById(roomId).map(room -> {
                TeamManager.unregister(room.getId());
                room.broadcast(MessageBuilder.clubUnregisterTeam((short) 0));
                return (short) 0;
            }).orElse((short) -2);

            if (result != 0) {
                session.send(MessageBuilder.clubUnregisterTeam(result));
            }
        }
    }

    public static void teamList(Session session, ClientMessage msg) {
        short page = msg.readShort();

        int roomId = session.getRoomId();

        if ((roomId > 0) && TeamManager.isRegistered(roomId)) {
            RoomManager.getRoomById(roomId).ifPresent(room -> {
                Map<Integer, ClubRoom> teams = TeamManager.getTeamsFromPage(page, roomId);
                session.send(MessageBuilder.clubTeamList(teams, (ClubRoom) room, page));
            });
        }
    }

    public static void challengeTeam(Session session, ClientMessage msg) {
        if (!GameEvents.isClubTime()) {
            ChatUtils.sendServerMessage(session, "Club time is not active.");
            return;
        }

        int roomId = msg.readShort();
        int target = msg.readShort();

        if (roomId == target) {
            session.send(MessageBuilder.clubChallengeTeam(target, true, (short) -10));
            return;
        }

        RoomManager.getRoomById(roomId).filter(room -> room.getMaster() == session.getPlayerId())
                .map(room -> (ClubRoom) room)
                .ifPresent(room -> {
                    short result;

                    if (roomId != session.getRoomId()) {
                        result = -4; // Problem detected. Invalid room
                    } else if (!TeamManager.isRegistered(roomId) ||
                            !TeamManager.isRegistered(target)) {
                        result = -2; // Problem with the team information. Not registered.
                    } else {
                        result = RoomManager.getRoomById(target).map(r -> (ClubRoom) r)
                                .map(targetRoom -> {

                                    if (!room.isWaiting() || !targetRoom.isWaiting()) {
                                        return (short) -8; // The club is playing a match
                                    }

                                    if (room.isChallenging() || targetRoom.isChallenging()) {
                                        return (short) -6; // The club is applying for a match
                                    }

                                    // Send the request to the target team
                                    short directResult = targetRoom.onChallengeRequest(roomId);

                                    if (directResult == 0) {
                                        room.setChallengeTarget(target);
                                    }

                                    return directResult;
                                }).orElse((short) -5);
                    }

                    ServerMessage message = MessageBuilder.clubChallengeTeam(target, true, result);

                    if (result == 0) {
                        room.broadcast(message);
                    } else {
                        session.send(message);
                    }
                });
    }

    public static void challengeResponse(Session session, ClientMessage msg) {
        int requesterId = msg.readShort();
        int roomId = msg.readShort();

        // Ignore the message if the room or the requester room is invalid
        if ((roomId == requesterId) || (roomId != session.getRoomId())) {
            return;
        }

        RoomManager.getRoomById(roomId).filter(room -> room.getMaster() == session.getPlayerId())
                .map(room -> (ClubRoom) room)
                .ifPresent(room -> {
                    boolean accepted = msg.readBoolean();

                    short result;
                    Optional<ClubRoom> requester = Optional.empty();

                    // Not enough result messages, so we will need to reuse some...

                    if (!TeamManager.isRegistered(roomId) || !TeamManager.isRegistered(requesterId)) {
                        result = -2; // Problem with the team information. Not registered.
                    } else {
                        requester = RoomManager.getRoomById(requesterId).map(r -> (ClubRoom) r);

                        result = requester.map(req -> {
                            short directResult = 0;

                            if (req.getChallengeTarget() != roomId) {
                                directResult = -6;
                            } else {
                                directResult = req.onChallengeResponse(roomId, accepted);

                                if (directResult == 0) {
                                    if (accepted) {
                                        ChallengeRoom challengeRoom = new ChallengeRoom();
                                        ChallengeOrganizer.add(challengeRoom, room, req);
                                        challengeRoom.addChallengePlayers();
                                        challengeRoom.setMaster(room.getMaster());
                                    } else {
                                        directResult = -5; // Challenge request refused
                                    }
                                }
                            }

                            return directResult;
                        }).orElse((short) -2);
                    }

                    if (!accepted || (result != 0)) {
                        room.setChallengeTarget(0);
                    }

                    ServerMessage message = MessageBuilder.clubChallengeResponse(requesterId,
                            accepted, result);

                    if (result == 0) {
                        room.broadcast(message);
                    } else {
                        session.send(message);
                    }
                });
    }

    public static void cancelChallenge(Session session) {
        RoomManager.getRoomById(session.getRoomId())
                .filter(room -> room.getMaster() == session.getPlayerId())
                .map(room -> (ClubRoom) room)
                .ifPresent(room ->
                        ChallengeOrganizer.getChallengeById(room.getChallengeId())
                                .filter(challenge -> challenge.getRoom().isInLobbyScreen())
                                .ifPresent(Challenge::cancel));
    }
}
