package com.neikeq.kicksemu.network.server;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.servers.ServerBase;
import com.neikeq.kicksemu.game.servers.ServerInfo;
import com.neikeq.kicksemu.game.servers.ServerType;
import com.neikeq.kicksemu.game.servers.ServerUtils;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.MessageHandler;
import com.neikeq.kicksemu.storage.ConnectionRef;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {

    private static final Object locker = new Object();
    private static final Map<Integer, Session> players = new ConcurrentHashMap<>();
    private static final MessageHandler messageHandler = new MessageHandler();
    private static final ServerBase serverBase = new ServerBase();

    private static void initializeMainServer() {
        // Id 99 is reserved for main server. Ensure the id is not different.
        serverBase.setId((short) 99);
    }

    private static void initializeGameServer() throws SQLException {
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

        try (ConnectionRef con = ConnectionRef.ref();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setShort(1, getServerId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            Output.println("Exception when cleaning connected users: " +
                    e.getMessage(), Level.DEBUG);
        }
    }

    private static void updateConnectedUsers() {
        if (getServerId() > 0) {
            ServerInfo.setConnectedUsers((short) connectedPlayers(), getServerId());
        }
    }

    public static Session getSession(int id) {
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

    public static short getServerId() {
        return serverBase.getId();
    }

    public static ServerType getServerType() {
        return serverBase.getType();
    }

    public static Map<Integer, Session> getPlayers() {
        return players;
    }

    public static MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public static void initialize() throws SQLException {
        ServerType serverType = getServerType();

        if (serverType == null) {
            throw new IllegalArgumentException("Invalid server type.");
        } else if (serverType == ServerType.MAIN) {
            initializeMainServer();
        } else {
            initializeGameServer();
        }
    }

    private ServerManager() {
        throw new AssertionError("Server manager must not be initialized.");
    }
}
