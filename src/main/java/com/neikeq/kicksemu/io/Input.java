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

    private void handleGoldenTime(String ... arg) {
        if (arg.length < 2) return;

        try {
            float duration = Float.valueOf(arg[1]);
            GameEvents.setCustomGoldenTime(duration <= 0 ? 0 : duration);

            Output.println("Golden time " +
                    (duration > 0 ? "enabled for " + duration + " hours." : "disabled."));
        } catch (NumberFormatException ignored) {
            System.out.println("The specified duration is invalid.");
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
    }

    public Input() {
        defineCommands();
    }

    @FunctionalInterface
    private interface InputHandler {
        void handle(String ... arg);
    }
}
