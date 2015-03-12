package com.neikeq.kicksemu.game.chat;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.lobby.Lobby;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatManager {

    public static void chatMessage(Session session, ClientMessage msg) {
        msg.ignoreBytes(6);

        int playerId = session.getPlayerId();

        String name = msg.readString(15);
        ChatMessageType type = ChatMessageType.fromInt(msg.readByte());
        String message = msg.readString(67);

        if (type != null && !Flood.isPlayerLocked(playerId) &&
                !Flood.onPlayerChat(playerId)) {
            switch (type) {
                case NORMAL:
                    handleNormalMessage(session, name, message);
                    break;
                case TEAM:
                    handleTeamMessage(session, name, message);
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

        if (PlayerInfo.getName(playerId).equals(name)) {
            if (!message.isEmpty()) {
                Lobby lobby = session.getCurrentLobby();

                ChatMessageType type = PlayerInfo.isModerator(playerId) ?
                        ChatMessageType.MODERATOR : ChatMessageType.NORMAL;

                lobby.getPlayers().stream()
                        .filter(id -> !PlayerInfo.getIgnoredList(id).containsPlayer(playerId))
                        .forEach(targetId -> {
                            Session targetSession = ServerManager.getSessionById(targetId);

                            if (targetSession != null) {
                                targetSession.sendAndFlush(MessageBuilder.chatMessage(playerId,
                                        name, type, message));
                            }
                        });
            }
        }
    }

    private static void onMessageTeam(Session session, String name, String message) {
        int playerId = session.getPlayerId();

        if (PlayerInfo.getName(playerId).equals(name)) {
            if (!message.isEmpty()) {
                ChatMessageType type = ChatMessageType.TEAM;

                Room room = RoomManager.getRoomById(session.getRoomId());

                if (room != null && room.isPlayerIn(playerId)) {
                    ServerMessage msg = MessageBuilder.chatMessage(playerId, name, type, message);

                    room.sendTeamBroadcast(msg, room.getPlayerTeam(playerId), playerId);
                }
            }
        }
    }

    private static void onMessageWhisper(Session session, String name, String message) {
        int playerId = session.getPlayerId();

        if (PlayerInfo.getName(playerId).equals(name)) {
            ChatMessageType type = ChatMessageType.WHISPER_TO;

            String target = retrieveTargetFromWhisper(message);
            String whisper = retrieveMessageFromWhisper(message);

            if (target.isEmpty()) {
                type = ChatMessageType.INVALID_PLAYER;
            } else if (target.equals(name)) {
                type = ChatMessageType.CANNOT_SELF_WHISPER;
            }

            if (type == ChatMessageType.WHISPER_TO) {
                int targetId = CharacterUtils.getCharacterIdByName(target);
                Session targetSession = ServerManager.getSessionById(targetId);

                // If target player was found
                if (targetSession != null) {
                    // If the target player accepts whispers and is not ignoring this player
                    if (UserInfo.getSettings(targetSession.getUserId()).getWhispers() &&
                            !PlayerInfo.getIgnoredList(targetId) .containsPlayer(playerId)) {
                        ServerMessage msgWhisper = MessageBuilder.chatMessage(targetId, name,
                                ChatMessageType.WHISPER_FROM, whisper);
                        targetSession.sendAndFlush(msgWhisper);
                    } else {
                        type = ChatMessageType.WHISPERS_DISABLED;
                    }
                } else {
                    type = ChatMessageType.INVALID_PLAYER;
                }
            }

            ServerMessage response = MessageBuilder.chatMessage(playerId, target,
                    type, whisper);
            session.sendAndFlush(response);
        }
    }

    private static void onMessageClub(Session session, String name, String message) {
        int playerId = session.getPlayerId();
        int clubId = PlayerInfo.getClubId(playerId);

        if (clubId > 0 && PlayerInfo.getName(playerId).equals(name)) {
            if (!message.isEmpty()) {
                ChatMessageType type = ChatMessageType.CLUB;

                ServerManager.getPlayers().values().stream()
                        .filter(s -> PlayerInfo.getClubId(s.getPlayerId()) == clubId)
                        .forEach(s -> s.sendAndFlush(MessageBuilder.chatMessage(playerId,
                                name, type, message)));
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
            CommandHandler.handle(session, message.substring(1));
        } else {
            onMessageNormal(session, name, message);
        }
    }

    private static void handleTeamMessage(Session session, String name, String message) {
        if (isClubMessage(message)) {
            onMessageClub(session, name, message.substring(2));
        } else if (isWhisperMessage(message)) {
            onMessageWhisper(session, name, message);
        } else if (isCommandMessage(message)) {
            CommandHandler.handle(session, message.substring(1));
        } else {
            onMessageTeam(session, name, message);
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

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return message.substring(1);
        }
    }

    private static String retrieveMessageFromWhisper(String message) {
        int index = message.indexOf(" ");

        if (index >= 0) {
            return message.substring(++index);
        } else {
            return "";
        }
    }
}
