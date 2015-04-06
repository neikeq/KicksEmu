package com.neikeq.kicksemu.game.sessions;

import com.neikeq.kicksemu.config.Constants;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.Position;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.misc.Moderation;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.game.users.UserUtils;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.network.server.udp.UdpPing;
import com.neikeq.kicksemu.storage.MySqlManager;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.utils.Password;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Authenticator {

    public static synchronized void certifyLogin(Session session, ClientMessage msg) {
        String username = msg.readString(30).toLowerCase();
        char[] password = msg.readChars(20);
        int clientVersion = msg.readInt();

        byte result = certifyAuthenticate(username, password, clientVersion);

        if (result == AuthResult.SUCCESS) {
            session.setAuthenticated(true);
            session.setUserId(UserUtils.getIdFromUsername(username));

            SessionManager.generateSession(session);

            UserInfo.setServer(ServerManager.getServerId(), session.getUserId());
            UserInfo.setOnline(0, session.getUserId());
        }

        session.send(MessageBuilder.certifyLogin(session.getSessionId(),
                session.getUserId(), result));

        if (result != AuthResult.SUCCESS) {
            session.close();
        }
    }

    private static byte certifyAuthenticate(String username, char[] password, int clientVersion) {
        byte authResult;

        if (clientVersion == Constants.REQUIRED_CLIENT_VERSION) {
            String query = "SELECT password, id FROM users WHERE username = ?";

            try (Connection con = MySqlManager.getConnection();
                 PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, username);

                try (ResultSet result = stmt.executeQuery()) {
                    if (result.next()) {
                        if (Password.validate(password, result.getString("password"))) {
                            // Overwrite password for security
                            Arrays.fill(password, '\0');

                            int id = result.getInt("id");

                            if (!Moderation.isUserBanned(id)) {
                                if (!UserUtils.isAlreadyConnected(id)) {
                                    authResult = AuthResult.SUCCESS;
                                } else {
                                    authResult = AuthResult.ALREADY_CONNECTED;
                                }
                            } else {
                                authResult = AuthResult.ACCOUNT_BLOCKED;
                            }
                        } else {
                            authResult = AuthResult.INVALID_PASSWORD;
                        }
                    } else {
                        authResult = AuthResult.ACCOUNT_NOT_FOUND;
                    }
                }
            } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                authResult = AuthResult.AUTH_FAILURE;
            }
        } else {
            authResult = AuthResult.CLIENT_VERSION;
        }

        return authResult;
    }

    public static synchronized void instantLogin(Session session, ClientMessage msg) {
        int sessionId = msg.readInt();

        int accountId = SessionInfo.getUserId(sessionId);
        int characterId = SessionInfo.getPlayerId(sessionId);

        String hash = SessionInfo.getHash(sessionId);

        byte result = instantAuthenticate(accountId);

        boolean validHash = false;

        try {
            validHash = Password.validateAddress(session.getRemoteAddress(), hash);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ignored) {}

        if (validHash) {
            if (result == AuthResult.SUCCESS) {
                session.setUserId(accountId);

                if (UserInfo.hasCharacter(characterId, session.getUserId())) {
                    session.setAuthenticated(true);
                    SessionInfo.resetExpiration(sessionId);
                    session.setPlayerId(characterId);
                    session.setSessionId(sessionId);

                    UserInfo.setServer(ServerManager.getServerId(), session.getUserId());
                    UserInfo.setOnline(0, session.getUserId());

                    ServerManager.addPlayer(characterId, session);
                } else {
                    // Account does not contain such character
                    result = AuthResult.ACCESS_FAILURE;
                }
            }
        } else {
            result = AuthResult.SYSTEM_PROBLEM;
        }

        ServerMessage response = MessageBuilder.instantLogin(sessionId, result);
        session.send(response);

        if (result != AuthResult.SUCCESS) {
            session.close();
        }
    }

    private static byte instantAuthenticate(int accountId) {
        byte authResult;

        String query = "SELECT 1 FROM users WHERE id = ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, accountId);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    if (!Moderation.isUserBanned(accountId)) {
                        boolean connected = UserUtils.isAlreadyConnected(accountId);

                        if (connected) {
                            // Give a bit of time and try again (max 3 times)
                            for (int i = 0; i < 3 && connected; i++) {
                                try {
                                    Thread.sleep(500);
                                    connected = UserUtils.isAlreadyConnected(accountId);
                                } catch (InterruptedException ignored) {}
                            }
                        }

                        if (!connected) {
                            authResult = AuthResult.SUCCESS;
                        } else {
                            authResult = AuthResult.ALREADY_CONNECTED;
                        }
                    } else {
                        authResult = AuthResult.ACCOUNT_BLOCKED;
                    }
                } else {
                    authResult = AuthResult.ACCOUNT_NOT_FOUND;
                }
            }
        } catch (SQLException e) {
            authResult = AuthResult.AUTH_FAILURE;
        }

        return authResult;
    }

    public static synchronized void gameLogin(Session session, ClientMessage msg) {
        int sessionId = msg.readInt();

        int accountId = SessionInfo.getUserId(sessionId);
        int characterId = SessionInfo.getPlayerId(sessionId);

        String hash = SessionInfo.getHash(sessionId);

        // If the character assigned to this session is not the same as
        // the one specified by the client, reject the request
        if (characterId != msg.readInt()) {
            session.close();
            return;
        }

        byte result = gameAuthenticate(accountId, characterId);

        boolean validHash = false;

        try {
            validHash = Password.validateAddress(session.getRemoteAddress(), hash);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ignored) {}

        if (validHash && (PlayerInfo.getLevel(characterId) < 18 ||
                Position.isAdvancedPosition(PlayerInfo.getPosition(characterId)))) {
            if (result == AuthResult.SUCCESS) {
                session.setAuthenticated(true);
                SessionInfo.resetExpiration(sessionId);

                session.setUserId(accountId);
                session.setPlayerId(characterId);
                session.setSessionId(sessionId);

                UserInfo.setServer(ServerManager.getServerId(), session.getUserId());
                UserInfo.setOnline(characterId, session.getUserId());

                ServerManager.addPlayer(characterId, session);
                LobbyManager.addPlayer(characterId);
            }
        } else {
            result = AuthResult.SYSTEM_PROBLEM;
        }

        ServerMessage response = MessageBuilder.gameLogin(result);
        session.send(response);

        if (result != AuthResult.SUCCESS) {
            session.close();
        }
    }

    private static byte gameAuthenticate(int accountId, int characterId) {
        byte authResult;

        if (!ServerManager.isServerFull()) {
            if (CharacterUtils.characterExist(characterId)) {
                if (PlayerInfo.getOwner(characterId) == accountId) {
                    boolean connected = UserUtils.isAlreadyConnected(accountId);

                    if (connected) {
                        // Give a bit of time and try again (max 3 times)
                        for (int i = 0; i < 3 && connected; i++) {
                            try {
                                Thread.sleep(500);
                                connected = UserUtils.isAlreadyConnected(accountId);
                            } catch (InterruptedException ignored) {}
                        }
                    }

                    if (!ServerManager.isPlayerConnected(characterId) && !connected) {
                        authResult = AuthResult.SUCCESS;
                    } else {
                        // Already connected
                        authResult = (byte)253;
                    }
                } else {
                    // Character problem: Invalid owner
                    authResult = (byte)254;
                }
            } else {
                // Character problem: Character does not exist
                authResult = (byte)254;
            }
        } else {
            // Server is full
            authResult = (byte)252;
        }

        return authResult;
    }

    public static void udpConfirm(Session session) {
        synchronized (session.getLocker()) {
            if (!session.isUdpAuthenticated()) {
                try {
                    session.getLocker().wait(5000);
                } catch (InterruptedException ignored) {}
            }
        }

        boolean result = session.isUdpAuthenticated();

        ServerMessage response = MessageBuilder.udpConfirm(result);
        session.send(response);

        if (!result) {
            session.close();
        } else if (session.getUdpPingFuture() == null) {
            ScheduledFuture<?> udpPingFuture = session.getChannel().eventLoop()
                    .scheduleAtFixedRate(new UdpPing(session), 10, 10, TimeUnit.SECONDS);
            session.setUdpPingFuture(udpPingFuture);
        }
    }

    public static void udpAuthentication(Session session) {
        if (session != null) {
            synchronized (session.getLocker()) {
                session.setUdpAuthenticated(true);
                session.setLastPingResponse(DateUtils.currentTimeMillis());
                session.getLocker().notify();
            }
        }
    }
}
