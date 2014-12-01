package com.neikeq.kicksemu.io;

import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.KicksEmu;
import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.config.Location;
import com.neikeq.kicksemu.io.logging.Logger;

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
            commands.get(args[0]).handle(args[1]);
        } else {
            System.out.println(Location.get("input.error", input));
        }
    }

    private void handleSave(String arg) {
        try {
            switch (arg) {
                case "config":
                    Configuration.getInstance().save();
                    break;
                case "lang":
                    Location.getInstance().save();
                    break;
                default:
                    System.out.println(Location.get("input.error", arg));
            }
        } catch (IOException e) {
            Output.println("Cannot save " + arg + " file: " + e.getMessage(), Level.WARNING);
        }
    }

    private void handleLogs(String arg) {
        switch (arg) {
            case "true":
                Logger.getInstance().setLogging(true);
                break;
            case "false":
                Logger.getInstance().setLogging(false);
                break;
            default:
                System.out.println(Logger.getInstance().getLogging());
        }
    }

    private void handleVerbosity(String arg) {
        switch (arg) {
            case "debug":
                KicksEmu.getInstance().getOutput().setLevel(Level.DEBUG);
                break;
            case "info":
                KicksEmu.getInstance().getOutput().setLevel(Level.INFO);
                break;
            case "warning":
                KicksEmu.getInstance().getOutput().setLevel(Level.WARNING);
                break;
            case "critical":
                KicksEmu.getInstance().getOutput().setLevel(Level.CRITICAL);
                break;
            default:
                System.out.println(Location.get("input.error", arg));
        }
    }

    public void defineCommands() {
        commands = new TreeMap<>();
        commands.put("save", this::handleSave);
        commands.put("logs", this::handleLogs);
        commands.put("verb", this::handleVerbosity);
        commands.put("stop", (String arg) -> KicksEmu.getInstance().stop());
    }

    public Input() {
        defineCommands();
    }

    @FunctionalInterface
    private interface InputHandler {
        void handle(String arg);
    }
}
