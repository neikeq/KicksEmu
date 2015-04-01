package com.neikeq.kicksemu.game.chat;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.game.table.LevelInfo;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.enums.RoomState;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChatCommands {

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

        if (room != null && room.state() == RoomState.WAITING) {
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

    private static void onProgress(Session session, String... args) {
        String expNeeded;
        int playerId = session.getPlayerId();

        try (Connection con = MySqlManager.getConnection()) {
            short playerLvl = PlayerInfo.getLevel(playerId, con);
            short askedLvl = args.length < 2 ? (short)(playerLvl + 1) : Short.valueOf(args[1]);

            if (playerLvl >= askedLvl || askedLvl > 60) {
                return;
            }

            LevelInfo lvlInfo = TableManager.getLevelInfo(c -> c.getLevel() == askedLvl);
            int expForAskedLvl = lvlInfo.getExperience();
            final int exp = PlayerInfo.getExperience(playerId, con);
            expNeeded = String.valueOf(expForAskedLvl - exp);

            ChatUtils.sendServerMessage(session, expNeeded + " to reach Lv " + askedLvl);
        } catch (SQLException | NumberFormatException ignored) {}
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

        if (room != null && room.isLobbyScreen()) {
            if (room.getMaster() == playerId || PlayerInfo.isModerator(playerId)) {
                int targetId = CharacterUtils.getCharacterIdByName(args[1]);
                Session target = room.getPlayers().get(targetId);

                if (target != null && !target.leaveRoom(RoomLeaveReason.KICKED)) {
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
            } else {
                ChatUtils.sendServerMessage(session, "Player not found.");
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

    private static void onObserver(Session session) {
        int playerId = session.getPlayerId();

        if (PlayerInfo.isModerator(playerId)) {
            boolean observer = !session.isObserver();
            session.setObserver(observer);

            if (session.getRoomId() > 0) {
                Room room = RoomManager.getRoomById(session.getRoomId());

                if (room != null) {
                    room.sendBroadcast(MessageBuilder.setObserver(playerId, observer));
                }
            }

            ChatUtils.sendServerMessage(session,
                    "Observer mode " + (observer ? "enabled." : "disabled."));
        }
    }

    private static void onVisible(Session session) {
        int playerId = session.getPlayerId();

        if (PlayerInfo.isModerator(playerId)) {
            boolean visible = !PlayerInfo.isVisible(playerId);
            PlayerInfo.setVisible(visible, playerId);

            ChatUtils.sendServerMessage(session,
                    "Visible mode " + (visible ? "enabled." : "disabled."));
        }
    }

    private static void defineCommands() {
        commands = new LinkedHashMap<>();
        commands.put("host", ChatCommands::onMaster);
        commands.put("progress", ChatCommands::onProgress);
        commands.put("who", ChatCommands::onWho);
        commands.put("kick", ChatCommands::onKick);
        commands.put("punish", ChatCommands::onPunish);
        commands.put("notice", ChatCommands::onNotice);
        commands.put("observer", (s, a) -> ChatCommands.onObserver(s));
        commands.put("visible", (s, a) -> ChatCommands.onVisible(s));
    }

    @FunctionalInterface
    private interface InputHandler {
        void handle(Session session, String ... args);
    }
}
