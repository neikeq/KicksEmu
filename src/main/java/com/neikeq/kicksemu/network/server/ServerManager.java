package com.neikeq.kicksemu.network.server;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.servers.GameServerType;
import com.neikeq.kicksemu.game.servers.ServerBase;
import com.neikeq.kicksemu.game.servers.ServerInfo;
import com.neikeq.kicksemu.game.servers.ServerUtils;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.handle.GameMessageHandler;
import com.neikeq.kicksemu.network.packets.in.handle.MainMessageHandler;
import com.neikeq.kicksemu.network.packets.in.handle.MessageHandler;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {

    private final ServerType serverType;

    private static MessageHandler messageHandler;
    private static Map<Integer, Session> players = new ConcurrentHashMap<>();
    private static ServerBase serverBase;
    private static short serverId;

    private static final Object locker = new Object();

    private void initializeMain() {
        messageHandler = new MainMessageHandler();
        serverId = 0;
    }

    private void initializeGame() throws SQLException {
        messageHandler = new GameMessageHandler();

        serverBase = ServerBase.fromConfig();
        serverId = serverBase.getId();

        if (!ServerUtils.serverExist(Configuration.getShort("game.id"))) {
            ServerUtils.insertServer(serverBase);
        } else {
            ServerUtils.updateServer(serverBase);
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

    public static void cleanPossibleConnectedUsers() {
        final String query = "UPDATE users SET online = -1, server = -1 WHERE server = ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setShort(1, getServerId());

            stmt.executeUpdate();
        } catch (SQLException ignored) {}
    }

    private static void updateConnectedUsers() {
        if (getServerId() > 0) {
            ServerInfo.setConnectedUsers((short) connectedPlayers(), getServerId());
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

    public static int connectedPlayers() {
        return getPlayers().size();
    }

    public ServerManager(String serverTypeId) throws SQLException {
        this(ServerType.valueOf(serverTypeId.toUpperCase()));
    }

    public static short getServerId() {
        return serverId;
    }

    public ServerBase getServerBase() {
        return serverBase;
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

    private ServerManager(ServerType type) throws SQLException {
        serverType = type;

        switch (getServerType()) {
            case MAIN:
                initializeMain();
                break;
            case GAME:
                initializeGame();
                break;
            default:
                throw new IllegalArgumentException("Unexpected ServerType.");
        }
    }
}
