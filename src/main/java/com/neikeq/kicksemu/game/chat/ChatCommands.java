package com.neikeq.kicksemu.game.chat;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.misc.ignored.IgnoredManager;
import com.neikeq.kicksemu.game.servers.ServerType;
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
import com.neikeq.kicksemu.utils.GameEvents;
import org.quartz.SchedulerException;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChatCommands {

    private static final Map<String, InputHandler> commands = new LinkedHashMap<>();

    public static void handle(Session session, String message) {
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

        if (ServerManager.getServerType() != ServerType.CLUB) {
            Room room = RoomManager.getRoomById(session.getRoomId());

            if (room != null && room.state() == RoomState.WAITING) {
                if (room.getMaster() == playerId || PlayerInfo.isModerator(playerId)) {
                    int targetId = CharacterUtils.getCharacterIdByName(args[1]);

                    if (targetId > 0 && targetId != room.getMaster() &&
                            room.isPlayerIn(targetId)) {
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
    }

    private static void onProgress(Session session, String... args) {
        int playerId = session.getPlayerId();
        DecimalFormat df = new DecimalFormat("#,###,###");

        try (Connection con = MySqlManager.getConnection()) {
            short playerLvl = PlayerInfo.getLevel(playerId, con);
            short askedLvl = args.length < 2 ? (short)(playerLvl + 1) : Short.valueOf(args[1]);

            if (playerLvl >= askedLvl || askedLvl > 60) {
                return;
            }

            LevelInfo lvlInfo = TableManager.getLevelInfo(c -> c.getLevel() == askedLvl);
            int expForAskedLvl = lvlInfo.getExperience();
            final int exp = PlayerInfo.getExperience(playerId, con);

            ChatUtils.sendServerMessage(session, df.format(expForAskedLvl - exp) +
                    " to reach Lv " + askedLvl);
        } catch (SQLException | NumberFormatException ignored) {}
    }

    private static void onWho(Session session, String... args) {
        if (args.length < 2) return;

        try {
            boolean specifiesRoomId = args.length > 2;

            Room room = RoomManager.getRoomById(specifiesRoomId ?
                    Integer.valueOf(args[2]) : session.getRoomId());

            if (room != null) {
                switch (args[1].toLowerCase()) {
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
            } else if (specifiesRoomId) {
                ChatUtils.sendServerMessage(session, "The room does not exist.");
            }
        } catch (NumberFormatException ignored) {
            ChatUtils.sendServerMessage(session, "The specified room id is invalid.");
        }
    }

    private static void onKick(Session session, String... args) {
        if (args.length < 2) return;

        int playerId = session.getPlayerId();
        Room room = RoomManager.getRoomById(session.getRoomId());

        if (room != null && room.isInLobbyScreen()) {
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

                if (room != null && room.isWaiting()) {
                    room.getObservers().add(playerId);
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

    /**
     * -- Usage --
     * Print the state of golden time: "#goldentime"
     * Start custom golden time with H:m duration: "#goldentime H:m"
     * Stop custom golden time: "#goldentime 0:0" or "#goldentime 0"
     * @param session session that used the command
     * @param args arguments
     */
    private static void onGoldenTime(Session session, String ... args) {
        if (args.length < 2) {
            ChatUtils.sendServerMessage(session, "Golden time is " +
                    (GameEvents.isGoldenTime() ? "" : "not ") + "active.");
            return;
        }

        if (PlayerInfo.isModerator(session.getPlayerId())) {
            try {
                if (args[1].equals("0")) {
                    GameEvents.setCustomGoldenTime(0);
                    ChatUtils.broadcastNotice("Golden time disabled.");
                } else {
                    String[] duration = args[1].split(":");
                    int minutes = (Integer.valueOf(duration[0]) * 60) +
                            Integer.valueOf(duration[1]);

                    GameEvents.setCustomGoldenTime(minutes <= 0 ? 0 : minutes);

                    if (minutes > 0) {
                        int hours = minutes / 60;
                        int mins = minutes % 60;

                        ChatUtils.broadcastNotice("Golden time enabled for " +
                                (hours > 0 ? hours + " hours" : "") +
                                (hours > 0 && mins > 0 ? " and " : "") +
                                (mins > 0 ? mins + " minutes" : "") + ".");
                    } else {
                        ChatUtils.broadcastNotice("Golden time disabled.");
                    }

                    if (minutes <= 0 && GameEvents.isGoldenTime()) {
                        ChatUtils.broadcastNotice("Scheduled Golden time is still active.");
                    }
                }
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
                ChatUtils.sendServerMessage(session,
                        "The specified duration is invalid. Expected: H:m");
            } catch (SchedulerException ignored) {
                ChatUtils.sendServerMessage(session, "Something went wrong with the scheduler.");
            }
        }
    }

    private static void onBlock(Session session, String ... args) {
        if (args.length < 2) return;

        String targetName = args[1];
        int targetId = CharacterUtils.getCharacterIdByName(targetName);

        byte result = IgnoredManager.blockPlayer(session, targetId);

        String response;

        switch (result) {
            case 0:
                response = "Player blocked: " + targetName;
                break;
            case -2:
                response = "Player not found: " + targetName;
                break;
            case -3:
                response = "Ignored list is full";
                break;
            case -4:
                response = "The player is already blocked";
                break;
            case -5:
                response = "You cannot block yourself";
                break;
            default:
                response = "Received unknown result... ";
        }

        ChatUtils.sendServerMessage(session, response);
    }

    private static void onUnblock(Session session, String ... args) {
        if (args.length < 2) return;

        String targetName = args[1];
        int targetId = CharacterUtils.getCharacterIdByName(targetName);

        byte result = IgnoredManager.unblockPlayer(session, targetId);

        String response;

        switch (result) {
            case 0:
                response = "Player unblocked: " + targetName;
                break;
            case -4:
                response = "Player not found: " + targetName;
                break;
            default:
                response = "Received unknown result... ";
        }

        ChatUtils.sendServerMessage(session, response);
    }

    public static void initialize() {
        commands.put("host", ChatCommands::onMaster);
        commands.put("progress", ChatCommands::onProgress);
        commands.put("who", ChatCommands::onWho);
        commands.put("kick", ChatCommands::onKick);
        commands.put("punish", ChatCommands::onPunish);
        commands.put("notice", ChatCommands::onNotice);
        commands.put("observer", (s, a) -> ChatCommands.onObserver(s));
        commands.put("visible", (s, a) -> ChatCommands.onVisible(s));
        commands.put("goldentime", ChatCommands::onGoldenTime);
        commands.put("block", ChatCommands::onBlock);
        commands.put("unblock", ChatCommands::onUnblock);
    }

    @FunctionalInterface
    private interface InputHandler {
        void handle(Session session, String ... args);
    }
}
