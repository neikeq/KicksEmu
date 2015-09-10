package com.neikeq.kicksemu.game.rooms.messages;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.clubs.MemberInfo;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.rooms.ChallengeRoom;
import com.neikeq.kicksemu.game.rooms.ClubRoom;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.rooms.TeamManager;
import com.neikeq.kicksemu.game.rooms.challenges.Challenge;
import com.neikeq.kicksemu.game.rooms.challenges.ChallengeOrganizer;
import com.neikeq.kicksemu.game.rooms.enums.RoomBall;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.enums.RoomMap;
import com.neikeq.kicksemu.game.rooms.enums.RoomMode;
import com.neikeq.kicksemu.game.rooms.enums.RoomSize;
import com.neikeq.kicksemu.game.rooms.enums.RoomAccessType;
import com.neikeq.kicksemu.game.servers.ServerType;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;

import java.util.Map;

public class ClubRoomMessages extends RoomMessages {

    private static final int MAX_ROOM_NAME_LENGTH = 14;
    static final byte MIN_ROOM_LEVEL = 3;
    static final byte MIN_TEAM_PLAYERS = 4;

    public static void roomList(Session session, ClientMessage msg) {
        short page = msg.readShort();
        session.send(MessageBuilder.clubRoomList(RoomManager.getRoomsFromPage(page),
                page, (byte) 0));
    }

    public static void createRoom(Session session, ClientMessage msg) {
        RoomAccessType type = RoomAccessType.fromShort(msg.readShort());
        String name = msg.readString(15);
        String password = msg.readString(5);

        int playerId = session.getPlayerId();
        int clubId = MemberInfo.getClubId(playerId);

        RoomMode roomMode = RoomMode.fromInt(msg.readByte());

        // Check that everything is correct
        byte result = 0;

        ServerType serverType = ServerManager.getServerType();

        if (type == null || roomMode == null || roomMode.notValidForServer(serverType)) {
            result = (byte) -1; // System problem
        } else if (PlayerInfo.getLevel(playerId) < MIN_ROOM_LEVEL) {
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

    public static void quickJoinRoom(Session session) {
        // Ignore the message if the player is already in a room
        if (session.getRoomId() > 0) return;

        int playerId = session.getPlayerId();
        Room room = RoomManager.getRoomById(MemberInfo.getClubId(playerId));

        // If a valid room was found
        if (room != null) {
            room.tryJoinRoom(session, "");
        } else {
            // Notify the player that no rooms were found
            session.send(MessageBuilder.clubQuickJoinRoom((byte) -2));
        }
    }

    public static void kickPlayer(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerToKick = msg.readInt();

        byte result = 0;

        Room room = RoomManager.getRoomById(session.getRoomId());

        // If the room exist and the player is inside it
        if (room != null && room.getId() == roomId) {
            // If the player is the room master
            if (room.getMaster() == session.getPlayerId() && room.isInLobbyScreen()) {
                // If the player is in the room
                if (room.isPlayerIn(playerToKick)) {
                    room.getPlayers().get(playerToKick).leaveRoom(RoomLeaveReason.KICKED);
                } else {
                    result = (byte) -4; // Player not found
                }
            } else {
                result = (byte) -3; // Not the room master
            }
        } else {
            result = (byte)- 2; // Invalid room
        }

        // If there is something wrong, notify the client
        if (result != 0) {
            session.send(MessageBuilder.clubKickPlayer(result));
        }
    }

    public static void roomSettings(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        RoomAccessType type = RoomAccessType.fromShort(msg.readShort());
        String name = msg.readString(15);
        String password = msg.readString(5);

        // Check that settings are valid

        byte result = 0;

        Room room = RoomManager.getRoomById(roomId);

        if (type == null) {
            result = (byte) -1; // System problem
        } else if (room == null) {
            result = (byte) -2; // Room does not exist
        } else if (room.getMaster() != session.getPlayerId()) {
            // Player is not room's master
            // Actually, it will display the same as -2...
            result = (byte) -3;
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

            room.sendBroadcast(MessageBuilder.clubRoomSettings(room, result));
        }

        if (result != 0) {
            session.send(MessageBuilder.clubRoomSettings(room, result));
        }
    }

    public static void invitePlayer(Session session, ClientMessage msg) {
        Room room = RoomManager.getRoomById(session.getRoomId());

        // If the player is in a room
        if (room != null) {
            int playerToInvite = msg.readInt();

            byte result = 0;

            if (room.isNotFull()) {
                // If the player to invite is in the main lobby
                if (LobbyManager.getMainLobby().getPlayers().contains(playerToInvite)) {
                    Session sessionToInvite = ServerManager.getPlayers().get(playerToInvite);

                    if (UserInfo.getSettings(sessionToInvite.getUserId()).getInvites()) {
                        int targetClubId = MemberInfo.getClubId(playerToInvite);

                        // If the target player is a member of the club
                        if (targetClubId == room.getId()) {
                            ServerMessage invitation = MessageBuilder.clubInvitePlayer(result,
                                    room, PlayerInfo.getName(session.getPlayerId()));
                            sessionToInvite.sendAndFlush(invitation);
                        } else {
                            result = (byte) -6; // Target player is not a member of the club
                        }
                    } else {
                        result = (byte) -3; // Target player does not accept invitations
                    }
                } else {
                    result = (byte) -2; // Target player not found
                }
            }

            // If there is something wrong, notify the client
            if (result != 0) {
                session.send(MessageBuilder.clubInvitePlayer(result, null, ""));
            }
        }
    }

    public static void registerTeam(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        byte result = 0;

        if (roomId == session.getRoomId() && !TeamManager.isRegistered(roomId)) {
            ClubRoom room = (ClubRoom) RoomManager.getRoomById(roomId);

            if (room != null) {
                if (room.getCurrentSize() == MIN_TEAM_PLAYERS) {
                    TeamManager.register(room);
                    room.sendBroadcast(MessageBuilder.clubRegisterTeam(result));
                } else {
                    result = -3; // Not enough players
                }
            } else {
                result = -2; // The club doesn't exist
            }
        }

        if (result != 0) {
            session.send(MessageBuilder.clubRegisterTeam(result));
        }
    }

    public static void unregisterTeam(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        byte result = 0;

        if (roomId == session.getRoomId() && TeamManager.isRegistered(roomId)) {
            Room room = RoomManager.getRoomById(roomId);

            if (room != null) {
                TeamManager.unregister(room.getId());
                room.sendBroadcast(MessageBuilder.clubUnregisterTeam(result));
            } else {
                result = -2; // The club doesn't exist
            }
        }

        if (result != 0) {
            session.send(MessageBuilder.clubRegisterTeam(result));
        }
    }

    public static void teamList(Session session, ClientMessage msg) {
        short page = msg.readShort();

        int roomId = session.getRoomId();

        if (roomId > 0 && TeamManager.isRegistered(roomId)) {
            Map<Integer, ClubRoom> teams = TeamManager.getTeamsFromPage(page, roomId);
            session.send(MessageBuilder.clubTeamList(teams, page));
        }
    }

    public static void challengeTeam(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int targetId = msg.readShort();

        ClubRoom room = (ClubRoom) RoomManager.getRoomById(roomId);

        if (room == null || room.getMaster() != session.getPlayerId()) return;

        byte result;

        if (roomId == targetId) {
            result = -10; // Cannot challenge own team
        } else if (roomId != session.getRoomId()) {
            result = -4; // Problem detected. Invalid room
        } else if (!TeamManager.isRegistered(roomId) || !TeamManager.isRegistered(targetId)) {
            result = -2; // Problem with the team information. Not registered.
        } else {
            ClubRoom targetRoom = (ClubRoom) RoomManager.getRoomById(targetId);

            if (targetRoom == null) {
                result = -5; // Problem detected. The club doesn't exist
            } else if (!room.isWaiting() || !targetRoom.isWaiting()) {
                result = -8; // The club is playing a match
            } else if (room.isChallenging() || targetRoom.isChallenging()) {
                result = -6; // The club is applying for a match
            } else {
                // Send the request to the target team
                result = targetRoom.onChallengeRequest(roomId);

                if (result == 0) {
                    room.setChallengeTarget(targetId);
                }
            }
        }

        session.send(MessageBuilder.clubChallengeTeam(targetId, true, result));
    }

    public static void challengeResponse(Session session, ClientMessage msg) {
        int requesterId = msg.readShort();
        int roomId = msg.readShort();
        boolean accepted = msg.readBoolean();

        ClubRoom room = (ClubRoom) RoomManager.getRoomById(roomId);
        ClubRoom requester = null;

        // Ignore the message if at least one of the following checks fails
        if (room == null || room.getMaster() != session.getPlayerId() ||
                roomId == requesterId || roomId != session.getRoomId()) return;

        byte result;

        // Not enough result messages, so we will need to reuse some...

        if (!TeamManager.isRegistered(roomId) || !TeamManager.isRegistered(requesterId)) {
            result = -2; // Problem with the team information. Not registered.
        } else {
            requester = (ClubRoom) RoomManager.getRoomById(requesterId);

            if (requester == null) {
                result = -2; // Problem with the team information. Requester team does not exist.
            } else if (requester.getChallengeTarget() != roomId) {
                result = -6; // Failed to create the match room... Invalid target.
            } else {
                result = requester.onChallengeResponse(roomId, accepted);

                if (result == 0) {
                    result = accepted ?
                            (byte) 0 : // Challenge request accepted
                            -5; // Challenge request refused by the team leader
                }
            }
        }

        if (!accepted || result != 0) {
            room.setChallengeTarget(0);
        } else {
            TeamManager.unregister(room.getId());
            TeamManager.unregister(requester.getId());

            Challenge challenge = ChallengeOrganizer.add(requester, room);
            ChallengeRoom challengeRoom = new ChallengeRoom(challenge);
            challenge.addObserver(challengeRoom);
            challengeRoom.setMaster(challenge.getRedTeam().getMaster());
            challengeRoom.setHost(challenge.getRedTeam().getMaster());
            challengeRoom.addPlayersFromRooms(challenge.getRedTeam(), challenge.getBlueTeam());
        }

        session.send(MessageBuilder.clubChallengeResponse(requesterId, accepted, result));
    }

    public static void cancelChallenge(Session session) {
        ClubRoom room = (ClubRoom) RoomManager.getRoomById(session.getRoomId());

        if (room != null && room.getMaster() == session.getPlayerId()) {
            Challenge challenge = ChallengeOrganizer.getChallengeById(room.getChallengeId());
            if (challenge != null) {
                challenge.cancel();
            }
        }

        session.send(MessageBuilder.clubCancelChallenge());
    }
}
