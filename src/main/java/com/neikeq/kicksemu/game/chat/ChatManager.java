package com.neikeq.kicksemu.game.chat;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.clubs.MemberInfo;
import com.neikeq.kicksemu.game.lobby.Lobby;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatManager {

    public static void chatMessage(Session session, ClientMessage msg) {
        msg.ignoreBytes(6);

        int playerId = session.getPlayerId();

        String name = msg.readString(15);
        MessageType type = MessageType.fromInt(msg.readByte());
        String message = msg.readString(67);

        if ((type != null) && !Flood.isPlayerLocked(playerId) &&
                !Flood.onPlayerChat(playerId)) {
            switch (type) {
                case NORMAL:
                    handleNormalMessage(session, name, message);
                    break;
                case TEAM:
                    handleTeamMessage(session, name, message);
                    break;
                case CLUB:
                    handleClubMessage(session, name, message);
                    break;
                case WHISPER:
                    if (isWhisperMessage(message)) {
                        onMessageWhisper(session, name, message);
                    }
                    break;
                default:
            }
        }
    }

    private static void onMessageNormal(Session session, String name, String message) {
        int playerId = session.getPlayerId();

        if (session.getCache().getName().equals(name)) {
            if (!message.isEmpty()) {
                Lobby lobby = session.getCurrentLobby();

                MessageType type = PlayerInfo.isModerator(playerId) ?
                        MessageType.MODERATOR : MessageType.NORMAL;

                lobby.getPlayers().stream()
                        .filter(id -> !PlayerInfo.getIgnoredList(id).containsPlayer(playerId))
                        .forEach(targetId ->
                                ServerManager.getSession(targetId).ifPresent(targetSession -> {
                                    ServerMessage msg = MessageBuilder.chatMessage(playerId,
                                            name, type, message);
                                    targetSession.sendAndFlush(msg);
                                }));
            }
        }
    }

    private static void onMessageTeam(Session session, String name, String message) {
        int playerId = session.getPlayerId();

        if (session.getCache().getName().equals(name) && !message.isEmpty()) {
            RoomManager.getRoomById(session.getRoomId())
                    .filter(room -> room.isPlayerIn(playerId) &&
                            room.getRoomLobby().isTeamChatEnabled())
                    .ifPresent(room -> {
                        ServerMessage msg = MessageBuilder.chatMessage(playerId, name,
                                MessageType.TEAM, message);
                        room.broadcastToTeam(msg, room.getPlayerTeam(playerId), playerId);
                    });
        }
    }

    private static void onMessageWhisper(Session session, String name, String message) {
        int playerId = session.getPlayerId();

        if (session.getCache().getName().equals(name)) {
            MessageType type = MessageType.WHISPER_TO;

            String target = retrieveTargetFromWhisper(message);
            String whisper = retrieveMessageFromWhisper(message);

            if (target.isEmpty()) {
                type = MessageType.INVALID_PLAYER;
            } else if (target.equals(name)) {
                type = MessageType.CANNOT_SELF_WHISPER;
            }

            if (type == MessageType.WHISPER_TO) {
                int targetId = CharacterUtils.getCharacterIdByName(target);

                type = ServerManager.getSession(targetId).map(targetSession -> {
                    // If the target player accepts whispers and is not ignoring this player
                    if (UserInfo.getSettings(targetSession.getUserId()).getWhispers() &&
                            !PlayerInfo.getIgnoredList(targetId).containsPlayer(playerId)) {
                        ServerMessage msgWhisper = MessageBuilder.chatMessage(targetId, name,
                                MessageType.WHISPER_FROM, whisper);
                        targetSession.sendAndFlush(msgWhisper);
                    } else {
                        return MessageType.WHISPERS_DISABLED;
                    }

                    return MessageType.WHISPER_TO;
                }).orElse(MessageType.INVALID_PLAYER);
            }

            session.sendAndFlush(MessageBuilder.chatMessage(playerId, target, type, whisper));
        }
    }

    private static void onMessageClub(Session session, String name, String message) {
        int playerId = session.getPlayerId();
        int clubId = MemberInfo.getClubId(playerId);

        if ((clubId > 0) && session.getCache().getName().equals(name)) {
            if (!message.isEmpty()) {
                List<Session> clubSessions = ServerManager.getPlayers().values().stream()
                        .filter(s -> MemberInfo.getClubId(s.getPlayerId()) == clubId)
                        .collect(Collectors.toList());

                if (clubSessions.size() > 1) {
                    ServerMessage msg = MessageBuilder.chatMessage(playerId, name, MessageType.CLUB, message);

                    try {
                        clubSessions.forEach(s -> s.sendAndFlush(msg.retain()));
                    } finally {
                        msg.release();
                    }
                } else {
                    ChatUtils.sendServerMessage(session, "No club members connected.");
                }
            }
        }
    }

    private static void handleNormalMessage(Session session, String name, String message) {
        if (isClubMessage(message)) {
            onMessageClub(session, name, message.substring(2));
        } else if (isWhisperMessage(message)) {
            onMessageWhisper(session, name, message);
        } else if (isTeamMessage(message)) {
            onMessageTeam(session, name, message.substring(1));
        } else if (isCommandMessage(message)) {
            ChatCommands.handle(session, message.substring(1));
        } else {
            onMessageNormal(session, name, message);
        }
    }

    private static void handleTeamMessage(Session session, String name, String message) {
        if (isClubMessage(message)) {
            onMessageClub(session, name, message.substring(2));
        } else if (isWhisperMessage(message)) {
            onMessageWhisper(session, name, message);
        } else if (isTeamMessage(message)) {
            onMessageTeam(session, name, message.substring(1));
        } else if (isCommandMessage(message)) {
            ChatCommands.handle(session, message.substring(1));
        } else {
            onMessageTeam(session, name, message);
        }
    }

    private static void handleClubMessage(Session session, String name, String message) {
        if (isClubMessage(message)) {
            onMessageClub(session, name, message.substring(2));
        } else if (isWhisperMessage(message)) {
            onMessageWhisper(session, name, message);
        } else if (isTeamMessage(message)) {
            onMessageTeam(session, name, message.substring(1));
        } else if (isCommandMessage(message)) {
            ChatCommands.handle(session, message.substring(1));
        } else {
            onMessageClub(session, name, message);
        }
    }

    private static boolean isWhisperMessage(String message) {
        return message.startsWith("/") && !isClubMessage(message);
    }

    private static boolean isTeamMessage(String message) {
        return message.startsWith("\'");
    }

    private static boolean isClubMessage(String message) {
        return message.startsWith("//");
    }

    private static boolean isCommandMessage(String message) {
        return message.startsWith("#");
    }

    private static String retrieveTargetFromWhisper(String message) {
        Pattern pattern = Pattern.compile("/(.*?) ");
        Matcher matcher = pattern.matcher(message);

        return matcher.find() ? matcher.group(1) : message.substring(1);
    }

    private static String retrieveMessageFromWhisper(String message) {
        int index = message.indexOf(" ");

        return (index >= 0) ? message.substring(++index) : "";
    }
}
