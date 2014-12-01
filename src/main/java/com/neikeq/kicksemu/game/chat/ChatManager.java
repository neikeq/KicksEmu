package com.neikeq.kicksemu.game.chat;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.lobby.Lobby;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;

public class ChatManager {

    public static void chatMessage(Session session, ClientMessage msg) {
        msg.ignoreBytes(2);

        int characterId = msg.readInt();

        if (session.getPlayerInfo().getId() == characterId) {
            String characterName = msg.readString(15);
            ChatMessageType type = ChatMessageType.fromInt(msg.readByte());
            String message = msg.readString(55);

            switch (type) {
                case NORMAL:
                    handleNormalMessage(session, characterName, message);
                    break;
                case TEAM:
                    break;
                case CLUB:
                    break;
                default:
            }
        }
    }

    public static void onMessageNormal(Session session, String name, String message) {
        if (session.getPlayerInfo().getName().equals(name)) {
            Lobby lobby = session.getCurrentLobby();

            ChatMessageType type = session.getPlayerInfo().isModerator() ?
                    ChatMessageType.MODERATOR : ChatMessageType.NORMAL;

            int playerId = session.getPlayerInfo().getId();

            lobby.getPlayers().stream().forEach(targetId -> {
                // TODO Check if the current target muted the player who writes this message
                ServerMessage msg = MessageBuilder.chatMessage(playerId, name,
                        type, message);
                Session targetSession = ServerManager.getSessionById(targetId);

                if (targetSession != null) {
                    targetSession.sendAndFlush(msg);
                }
            });
        }
    }

    public static void onMessageTeam(Session session, String name, String message) {

    }

    public static void onMessageWhisper(Session session, String name, String message) {
        if (session.getPlayerInfo().getName().equals(name)) {
            boolean result = false;

            StringBuilder whisper = new StringBuilder ();
            String target = extractTargetFromWhisper(message, whisper);

            if (!target.isEmpty()) {
                int targetId = CharacterUtils.getCharacterIdByName(target);
                Session targetSession = ServerManager.getSessionById(targetId);

                if (targetSession != null) {
                    // TODO Check if the target player muted the whisperer
                    ServerMessage msg = MessageBuilder.chatMessage(targetId, name,
                            ChatMessageType.WHISPER_FROM, whisper.toString());
                    targetSession.sendAndFlush(msg);

                    result = true;
                }
            }

            int playerId = session.getPlayerInfo().getId();

            ServerMessage response = MessageBuilder.chatMessage(playerId, name,
                    result ? ChatMessageType.WHISPER_TO : ChatMessageType.INVALID_PLAYER,
                    whisper.toString());
            session.sendAndFlush(response);
        }
    }

    public static void onMessageClub(Session session, String name, String message) {

    }

    public static void handleNormalMessage(Session session, String name, String message) {
        if (isClubMessage(message)) {
            onMessageClub(session, name, message);
        } else if (isWhisperMessage(message)) {
            onMessageWhisper(session, name, message);
        } else if (isTeamMessage(message)) {
            onMessageTeam(session, name, message);
        } else {
            onMessageNormal(session, name, message);
        }
    }

    public static boolean isWhisperMessage(String message) {
        return message.startsWith("/") && !isClubMessage(message);
    }

    public static boolean isTeamMessage(String message) {
        return message.startsWith(";");
    }

    public static boolean isClubMessage(String message) {
        return message.startsWith("//");
    }

    public static String extractTargetFromWhisper(String message, StringBuilder whisper) {
        try {
            String target = message.substring(message.indexOf("/") + 1);

            int endIndex = target.length();

            if (message.contains(" ")) {
                endIndex = target.indexOf(" ");
                target = target.substring(0, endIndex);
            }

            whisper.append(message.substring(++endIndex, message.length()));

            return target;
        } catch (IndexOutOfBoundsException ex) {
            return "";
        }
    }
}
