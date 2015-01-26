package com.neikeq.kicksemu.network.server;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.config.Localization;
import com.neikeq.kicksemu.config.Table;
import com.neikeq.kicksemu.game.servers.ServerBase;
import com.neikeq.kicksemu.game.servers.ServerInfo;
import com.neikeq.kicksemu.game.servers.ServerUtils;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.handle.GameMessageHandler;
import com.neikeq.kicksemu.network.packets.in.handle.MainMessageHandler;
import com.neikeq.kicksemu.network.packets.in.handle.MessageHandler;

import java.util.HashMap;
import java.util.Map;

public class ServerManager {

    private final ServerType serverType;

    private static MessageHandler messageHandler;
    private static Map<Integer, Session> players;
    private static ServerBase serverBase;
    private static short serverId;

    private static final Object locker = new Object();

    public ServerManager(String serverTypeId) {
        this(ServerType.valueOf(serverTypeId.toUpperCase()));
    }

    private ServerManager(ServerType type) {
        serverType = type;
        players = new HashMap<>();
    }

    public boolean init() {
        switch (getServerType()) {
            case MAIN:
                initializeMain();
                return true;
            case GAME:
                return initializeGame();
            default:
                return false;
        }
    }

    void initializeMain() {
        messageHandler = new MainMessageHandler();

        Table.initializeTables();
    }

    private boolean initializeGame() {
        Output.println(Localization.get("game.init"), Level.INFO);

        messageHandler = new GameMessageHandler();

        serverBase = ServerBase.fromConfig();
        serverId = serverBase.getId();

        Table.initializeTables();

        if (!ServerUtils.serverExist(Configuration.getShort("game.id"))) {
            return ServerUtils.insertServer(serverBase);
        } else {
            return ServerUtils.updateServer(serverBase);
        }
    }

    public static void addPlayer(int characterId, Session session) {
        synchronized (locker) {
            if (!getPlayers().containsKey(characterId)) {
                getPlayers().put(characterId, session);
            }

            updateConnectedUsers();
        }
    }

    public static void removePlayer(int characterId) {
        synchronized (locker) {
            getPlayers().remove(characterId);
            updateConnectedUsers();
        }
    }

    private static void updateConnectedUsers() {
        if (serverId > 0) {
            ServerInfo.setConnectedUsers((short) getPlayers().size(), serverId);
        }
    }

    public static Session getSessionById(int id) {
        synchronized (locker) {
            return getPlayers().get(id);
        }
    }

    public static boolean isServerFull() {
        synchronized (locker) {
            return getPlayers().size() >= serverBase.getMaxUsers();
        }
    }

    public static boolean isPlayerConnected(int characterId) {
        synchronized (locker) {
            return getPlayers().containsKey(characterId);
        }
    }

    public ServerType getServerType() {
        return serverType;
    }

    public static Map<Integer, Session> getPlayers() {
        return players;
    }

    public static MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
