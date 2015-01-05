package com.neikeq.kicksemu.game.chat;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.server.ServerManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommandHandler {

    private static Map<String, InputHandler> commands;

    public static void handle(Session session, String message) {
        if (commands == null) {
            defineCommands();
        }

        String[] params = message.split(" ");

        if (commands.containsKey(params[0])) {
            commands.get(params[0]).handle(session, params);
        } else {
            ChatUtils.sendServerMessage(session, "Invalid command.");
        }
    }

    private static void onMaster(Session session, String... args) {
        if (args.length < 2) return;

        int playerId = session.getPlayerId();
        Room room = RoomManager.getRoomById(session.getRoomId());

        if (room != null) {
            if (room.getMaster() == playerId || PlayerInfo.isModerator(playerId)) {
                int targetId = CharacterUtils.getCharacterIdByName(args[1]);

                if (targetId > 0 && targetId != room.getMaster() && room.isPlayerIn(targetId)) {
                    room.setMaster(targetId);
                    room.setHost(targetId);
                } else {
                    ChatUtils.sendServerMessage(session, "Player not found.");
                }
            } else {
                ChatUtils.sendServerMessage(session, "You are not the room's master.");
            }
        }
    }

    private static void onWho(Session session, String... args) {
        if (args.length < 2) return;

        Room room = RoomManager.getRoomById(session.getRoomId());

        if (room != null) {
            switch (args[1]) {
                case "master":
                    ChatUtils.sendServerMessage(session,
                            "Master: " + PlayerInfo.getName(room.getMaster()));
                    break;
                case "host":
                    ChatUtils.sendServerMessage(session,
                            "Host: " + PlayerInfo.getName(room.getHost()));
                    break;
                default:
            }
        }
    }

    private static void onKick(Session session, String... args) {
        if (args.length < 2) return;

        int playerId = session.getPlayerId();
        Room room = RoomManager.getRoomById(session.getRoomId());

        if (room != null) {
            if (room.getMaster() == playerId || PlayerInfo.isModerator(playerId)) {
                int targetId = CharacterUtils.getCharacterIdByName(args[1]);
                Session target = room.getPlayers().get(targetId);

                if (!target.leaveRoom(RoomLeaveReason.KICKED)) {
                    ChatUtils.sendServerMessage(session, "Player not found.");
                }
            } else {
                ChatUtils.sendServerMessage(session, "You are not the room's master.");
            }
        }
    }

    private static void onPunish(Session session, String ... args) {
        if (args.length < 2) return;

        if (PlayerInfo.isModerator(session.getPlayerId())) {
            int targetId = CharacterUtils.getCharacterIdByName(args[1]);

            if (ServerManager.isPlayerConnected(targetId)) {
                Session target = ServerManager.getSessionById(targetId);
                ChatUtils.sendServerMessage(target, "You have been punished by a moderator.");
                target.close();

                ChatUtils.sendServerMessage(session, "Player punished: " + args[1]);
            }
        }
    }

    private static void onNotice(Session session, String ... args) {
        if (PlayerInfo.isModerator(session.getPlayerId())) {
            StringBuilder message = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                message.append(args[i]);
                message.append(" ");
            }

            ChatUtils.broadcastNotice(message.toString());
        }
    }

    private static void onObserver(Session session, String ... args) {
        if (PlayerInfo.isModerator(session.getPlayerId())) {
            session.setObserver(!session.isObserver());
        }
    }

    private static void defineCommands() {
        commands = new LinkedHashMap<>();
        commands.put("host", CommandHandler::onMaster);
        commands.put("who", CommandHandler::onWho);
        commands.put("kick", CommandHandler::onKick);
        commands.put("punish", CommandHandler::onPunish);
        commands.put("notice", CommandHandler::onNotice);
        commands.put("observer", CommandHandler::onObserver);
    }

    @FunctionalInterface
    private interface InputHandler {
        void handle(Session session, String ... args);
    }
}
