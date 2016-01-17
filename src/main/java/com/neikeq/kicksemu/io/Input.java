package com.neikeq.kicksemu.io;

import com.neikeq.kicksemu.game.chat.ChatUtils;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.servers.ServerType;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.KicksEmu;
import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.config.Localization;
import com.neikeq.kicksemu.io.logging.Logger;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.game.events.GameEvents;
import org.quartz.SchedulerException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeMap;

/** Input listener. */
public class Input {

    /** List of valid commands. */
    private Map<String, InputHandler> commands;

    public void listen() {
        try (Scanner scanner = new Scanner(System.in)) {
        	while (KicksEmu.isAlive()) {
	            if (scanner.hasNextLine()) {
	                String input = scanner.nextLine();

	                if (!input.isEmpty()) {
	                    handle(input);
	                }
	            }
	        }
        }
    }

    private void handle(String input) {
        String[] args = input.split(" ");

        if (commands.containsKey(args[0])) {
            commands.get(args[0]).handle(args);
        } else {
            System.out.println(Localization.get("input.error", input));
        }
    }

    private void handleSave(String ... arg) {
        if (arg.length < 2) {
            System.out.println("Expected argument {'config', 'lang'}");
        }

        try {
            switch (arg[1]) {
                case "config":
                    Configuration.getInstance().save();
                    break;
                case "lang":
                    Localization.getInstance().save();
                    break;
                default:
                    System.out.println(Localization.get("input.error", arg[1]));
            }
        } catch (IOException e) {
            Output.println("Cannot save " + arg[1] + " file: " + e.getMessage(), Level.WARNING);
        }
    }

    private void handleLogs(String ... arg) {
        if (arg.length < 2) {
            System.out.println("Expected boolean argument");
        }

        try {
            Logger logger = Logger.getInstance();
            logger.setLogging(Boolean.valueOf(arg[1]));
            System.out.println("Logging " + (logger.getLogging() ? "enabled." : "disabled."));
        } catch (IllegalArgumentException ignored) {
            System.out.println("Invalid parameter for logging. Expected boolean type.");
        }
    }

    private void handleVerbosity(String ... arg) {
        if (arg.length < 2) {
            System.out.println("Expected argument {'debug', 'info', 'warning', 'critical'}");
        }

        try {
            Level specifiedLevel = Level.valueOf(arg[1].toUpperCase());
            KicksEmu.getOutput().setLevel(specifiedLevel);
        } catch (IllegalArgumentException ignored) {
            System.out.println(Localization.get("input.error", arg[1]));
        }
    }

    private void handleNotice(String ... arg) {
        StringBuilder message = new StringBuilder();

        for (int i = 1; i < arg.length; i++) {
            message.append(arg[i]);
            message.append(" ");
        }

        ChatUtils.broadcastNotice(message.toString());
    }

    private void handleStats() {
        ServerType serverType = ServerManager.getServerType();

        System.out.println("- Server: " + ServerManager.getServerId());
        System.out.println("- Type: " + serverType);
        System.out.println("- Connected users: " + ServerManager.connectedPlayers());

        // If this is a game server
        if (serverType != ServerType.MAIN) {
            System.out.println("- Users in lobby: " +
                    LobbyManager.getMainLobby().getPlayers().size());
            System.out.println("- Open rooms: " + RoomManager.roomsCount());
        }
    }

    /**
     * -- Usage --
     * Print the state of golden time: "goldentime"
     * Start custom golden time with H:m duration: "goldentime H:m"
     * Stop custom golden time: "goldentime 0:0" or "goldentime 0"
     * @param arg arguments
     */
    private void handleGoldenTime(String ... arg) {
        if (arg.length < 2) {
            Output.println("Golden time is " +
                    (GameEvents.isGoldenTime() ? "" : "not ") + "active.");
            return;
        }

        try {
            String[] duration = arg[1].split(":");
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
            System.out.println("The specified duration is invalid. Expected: H:m");
        } catch (SchedulerException e) {
            System.out.println("Something went wrong with the scheduler: " + e.getMessage());
        }
    }

    /**
     * -- Usage --
     * Print the state of club time: "goldentime"
     * Start custom club time with H:m duration: "goldentime H:m"
     * Stop custom club time: "goldentime 0:0" or "goldentime 0"
     * @param arg arguments
     */
    private void handleClubTime(String ... arg) {
        if (arg.length < 2) {
            Output.println("Club time is " +
                    (GameEvents.isClubTime() ? "" : "not ") + "active.");
            return;
        }

        try {
            String[] duration = arg[1].split(":");
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
            System.out.println("The specified duration is invalid. Expected: H:m");
        } catch (SchedulerException e) {
            System.out.println("Something went wrong with the scheduler: " + e.getMessage());
        }
    }

    /**
     * -- Usage --
     * "send playerId messageId [radix, [bytes]]"
     * @param arg arguments
     */
    private void handleSend(String ... arg) {
        if (arg.length < 3) {
            Output.println("Expected more arguments. Required: 'send playerId messageId'");
            return;
        }

        try {
            int targetId = Integer.valueOf(arg[1]);
            int messageId = Integer.valueOf(arg[2]);

            Optional<Session> maybeTarget = ServerManager.getSession(targetId);

            maybeTarget.ifPresent(target -> {
                ServerMessage msg = new ServerMessage(messageId);

                if (arg.length > 3) {
                    int radix = Integer.valueOf(arg[3]);

                    for (int i = 4; i < arg.length; i++) {
                        msg.writeByte(Byte.valueOf(arg[i], radix));
                    }
                }

                target.sendAndFlush(msg);
            });

            if (!maybeTarget.isPresent()) {
                Output.println("Target not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println();
        }
    }

    private void defineCommands() {
        commands = new TreeMap<>();
        commands.put("save", this::handleSave);
        commands.put("logs", this::handleLogs);
        commands.put("verb", this::handleVerbosity);
        commands.put("stop", arg -> KicksEmu.stop());
        commands.put("notice", this::handleNotice);
        commands.put("stats", arg -> handleStats());
        commands.put("goldentime", this::handleGoldenTime);
        commands.put("clubtime", this::handleClubTime);
        commands.put("send", this::handleSend);
    }

    public Input() {
        defineCommands();
    }

    @FunctionalInterface
    private interface InputHandler {
        void handle(String ... arg);
    }
}
