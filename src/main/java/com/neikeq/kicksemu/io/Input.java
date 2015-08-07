package com.neikeq.kicksemu.io;

import com.neikeq.kicksemu.game.chat.ChatUtils;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.servers.ServerType;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.KicksEmu;
import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.config.Localization;
import com.neikeq.kicksemu.io.logging.Logger;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.utils.GameEvents;
import org.quartz.SchedulerException;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/** Input listener. */
public class Input {

    /** List of valid commands. */
    private Map<String, InputHandler> commands;

    public void listen() {
        try (Scanner scanner = new Scanner(System.in)) {
        	while (Thread.currentThread().isAlive()) {
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

        if (args.length < 2) {
            args = new String[] { args[0], "" };
        }

        if (commands.containsKey(args[0])) {
            commands.get(args[0]).handle(args);
        } else {
            System.out.println(Localization.get("input.error", input));
        }
    }

    private void handleSave(String ... arg) {
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
        try {
            Logger logger = Logger.getInstance();
            logger.setLogging(Boolean.valueOf(arg[1]));
            System.out.println("Logging " + (logger.getLogging() ? "enabled." : "disabled."));
        } catch (IllegalArgumentException ignored) {
            System.out.println("Invalid parameter for logging. Expected boolean type.");
        }
    }

    private void handleVerbosity(String ... arg) {
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
        System.out.println("- Type: " + serverType.toString());
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
            if (arg[1].equals("0")) {
                GameEvents.setCustomGoldenTime(0);
                ChatUtils.broadcastNotice("Golden time disabled.");
            } else {
                String[] duration = arg[1].split(":");
                int minutes = (Integer.valueOf(duration[0]) * 60) + Integer.valueOf(duration[1]);

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
            System.out.println("The specified duration is invalid. Expected: H:m");
        } catch (SchedulerException e) {
            System.out.println("Something went wrong with the scheduler: " + e.getMessage());
        }
    }

    private void defineCommands() {
        commands = new TreeMap<>();
        commands.put("save", this::handleSave);
        commands.put("logs", this::handleLogs);
        commands.put("verb", this::handleVerbosity);
        commands.put("stop", (arg) -> KicksEmu.stop());
        commands.put("notice", this::handleNotice);
        commands.put("stats", (arg) -> handleStats());
        commands.put("goldentime", this::handleGoldenTime);
        commands.put("gt", (arg) -> System.out.println(GameEvents.isGoldenTime()));
    }

    public Input() {
        defineCommands();
    }

    @FunctionalInterface
    private interface InputHandler {
        void handle(String ... arg);
    }
}
