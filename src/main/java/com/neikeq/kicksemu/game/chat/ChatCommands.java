package com.neikeq.kicksemu.game.chat;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.misc.ignored.IgnoredManager;
import com.neikeq.kicksemu.game.rooms.messages.ClubRoomMessages;
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
import com.neikeq.kicksemu.game.events.GameEvents;
import com.neikeq.kicksemu.utils.DateUtils;
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

            if ((room != null) && (room.state() == RoomState.WAITING)) {
                if ((room.getMaster() == playerId) || PlayerInfo.isModerator(playerId)) {
                    int targetId = CharacterUtils.getCharacterIdByName(args[1]);

                    if ((targetId > 0) && (targetId != room.getMaster()) &&
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
            short playerLevel = PlayerInfo.getLevel(playerId, con);
            short requestedLevel = (args.length < 2) ?
                    (short) (playerLevel + 1) : Short.valueOf(args[1]);

            if ((playerLevel >= requestedLevel) || (requestedLevel > 60)) {
                return;
            }

            LevelInfo lvlInfo = TableManager.getLevelInfo(c -> c.getLevel() == requestedLevel);
            int expForAskedLvl = lvlInfo.getExperience();
            final int exp = PlayerInfo.getExperience(playerId, con);

            ChatUtils.sendServerMessage(session, df.format(expForAskedLvl - exp) +
                    " to reach Lv " + requestedLevel);
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
                        Session master = room.getPlayer(room.getMaster());
                        ChatUtils.sendServerMessage(session,
                                "Master: " + master.getCache().getName());
                        break;
                    case "host":
                        Session host = room.getPlayer(room.getHost());
                        ChatUtils.sendServerMessage(session,
                                "Host: " + host.getCache().getName());
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

        if ((room != null) && room.isInLobbyScreen()) {
            if ((room.getMaster() == playerId) || PlayerInfo.isModerator(playerId)) {
                int targetId = CharacterUtils.getCharacterIdByName(args[1]);

                if (targetId != session.getPlayerId()) {
                    Session target = room.getPlayer(targetId);

                    if ((target != null) && !target.leaveRoom(RoomLeaveReason.KICKED)) {
                        ChatUtils.sendServerMessage(session, "Player not found.");
                    }
                } else {
                    ChatUtils.sendServerMessage(session, "You cannot kick yourself.");
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
                Session target = ServerManager.getSession(targetId);
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
            Room room = null;

            if (session.getRoomId() > 0) {
                room = RoomManager.getRoomById(session.getRoomId());

                if ((room != null) && !room.isWaiting()) {
                    return;
                }
            }

            boolean observer = !session.isObserver();
            session.setObserver(observer);

            if (room != null) {
                if (!observer) {
                    room.getObservers().remove(Integer.valueOf(playerId));
                } else {
                    room.getObservers().add(playerId);
                }

                room.broadcast(MessageBuilder.setObserver(playerId, observer));
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
                String[] duration = args[1].split(":");
                int minutes = Integer.valueOf(duration[0]) * 60;
                if (duration.length > 1) {
                    minutes += Integer.valueOf(duration[1]);
                }

                GameEvents.setCustomGoldenTime((minutes <= 0) ? 0 : minutes);

                if (minutes > 0) {
                    int hours = minutes / 60;
                    int hourMinutes = minutes % 60;

                    ChatUtils.broadcastNotice("Golden time enabled for " +
                            (hours > 0 ? hours + " hours" : "") +
                            (hours > 0 && hourMinutes > 0 ? " and " : "") +
                            (hourMinutes > 0 ? hourMinutes + " minutes" : "") + ".");
                } else {
                    ChatUtils.broadcastNotice("Golden time disabled.");
                }

                if ((minutes <= 0) && GameEvents.isGoldenTime()) {
                    ChatUtils.broadcastNotice("Scheduled Golden time is still active.");
                }
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
                ChatUtils.sendServerMessage(session,
                        "The specified duration is invalid. Expected: H:m");
            } catch (SchedulerException ignored) {
                ChatUtils.sendServerMessage(session, "Something went wrong with the scheduler.");
            }
        }
    }

    /**
     * -- Usage --
     * Print the state of club time: "#goldentime"
     * Start custom club time with H:m duration: "#goldentime H:m"
     * Stop custom club time: "#goldentime 0:0" or "#goldentime 0"
     * @param session session that used the command
     * @param args arguments
     */
    private static void onClubTime(Session session, String ... args) {
        if (args.length < 2) {
            ChatUtils.sendServerMessage(session, "Club time is " +
                    (GameEvents.isGoldenTime() ? "" : "not ") + "active.");
            return;
        }

        if (PlayerInfo.isModerator(session.getPlayerId())) {
            try {
                String[] duration = args[1].split(":");
                int minutes = Integer.valueOf(duration[0]) * 60;
                if (duration.length > 1) {
                    minutes += Integer.valueOf(duration[1]);
                }

                GameEvents.setCustomClubTime((minutes <= 0) ? 0 : minutes);

                if (minutes > 0) {
                    int hours = minutes / 60;
                    int hourMinutes = minutes % 60;

                    ChatUtils.broadcastNotice("Club time enabled for " +
                            (hours > 0 ? hours + " hours" : "") +
                            (hours > 0 && hourMinutes > 0 ? " and " : "") +
                            (hourMinutes > 0 ? hourMinutes + " minutes" : "") + ".");
                } else {
                    ChatUtils.broadcastNotice("Club time disabled.");
                }

                if ((minutes <= 0) && GameEvents.isClubTime()) {
                    ChatUtils.broadcastNotice("Scheduled Club time is still active.");
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

    private static void onChallenge(Session session, String... args) {
        if (args.length < 2) return;

        if (ServerManager.getServerType() == ServerType.CLUB) {
            switch (args[1].toLowerCase()) {
                case "cancel":
                    ClubRoomMessages.cancelChallenge(session);
                    break;
                default:
            }
        }
    }

    private static void onGt(Session session) {
        if (GameEvents.isGoldenTime()) {
            ChatUtils.sendServerMessage(session, "Golden time is active.");
        } else {
            int minutes = GameEvents.remainMinutesForNextGoldenTime();

            if (minutes == -1) {
                ChatUtils.sendServerMessage(session, "No Golden time scheduled today.");
            } else {
                ChatUtils.sendServerMessage(session, "Next Golden time starts in " +
                        DateUtils.minuteToHourAndMinutes(minutes, "%d:%02d"));
            }
        }
    }

    private static void onCt(Session session) {
        if (GameEvents.isClubTime()) {
            ChatUtils.sendServerMessage(session, "Club time is active.");
        } else {
            int minutes = GameEvents.remainMinutesForNextClubTime();

            if (minutes == -1) {
                ChatUtils.sendServerMessage(session, "No Club time scheduled today.");
            } else {
                ChatUtils.sendServerMessage(session, "Next Club time starts in " +
                        DateUtils.minuteToHourAndMinutes(minutes, "%d:%02d"));
            }
        }
    }

    public static void initialize() {
        commands.put("host", ChatCommands::onMaster);
        commands.put("progress", ChatCommands::onProgress);
        commands.put("who", ChatCommands::onWho);
        commands.put("kick", ChatCommands::onKick);
        commands.put("punish", ChatCommands::onPunish);
        commands.put("notice", ChatCommands::onNotice);
        commands.put("observer", (s, a) -> onObserver(s));
        commands.put("visible", (s, a) -> onVisible(s));
        commands.put("goldentime", ChatCommands::onGoldenTime);
        commands.put("clubtime", ChatCommands::onClubTime);
        commands.put("block", ChatCommands::onBlock);
        commands.put("unblock", ChatCommands::onUnblock);
        commands.put("challenge", ChatCommands::onChallenge);
        commands.put("gt", (s, a) -> onGt(s));
        commands.put("ct", (s, a) -> onCt(s));
    }

    @FunctionalInterface
    private interface InputHandler {
        void handle(Session session, String ... args);
    }
}
