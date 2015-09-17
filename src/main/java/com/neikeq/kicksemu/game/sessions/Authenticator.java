package com.neikeq.kicksemu.game.sessions;

import com.neikeq.kicksemu.config.Constants;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.clubs.ClubManager;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.misc.Moderation;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.game.users.UserUtils;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.network.server.udp.UdpPing;
import com.neikeq.kicksemu.storage.MySqlManager;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.utils.Password;
import com.neikeq.kicksemu.utils.ThreadUtils;

import io.netty.util.concurrent.ScheduledFuture;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Authenticator {

    public static synchronized void certifyLogin(Session session, ClientMessage msg) {
        String username = msg.readString(30).toLowerCase();
        char[] password = msg.readChars(20);
        int clientVersion = msg.readInt();

        try {
            certifyAuthenticate(username, password, clientVersion);

            session.setAuthenticated(true);
            session.setUserId(UserUtils.getIdFromUsername(username));

            SessionManager.generateSession(session);

            UserInfo.setServer(ServerManager.getServerId(), session.getUserId());
            UserInfo.setOnline(0, session.getUserId());

            session.send(MessageBuilder.certifyLogin(session.getSessionId(),
                    session.getUserId(), (short) 0));
        } catch (AuthenticationException e) {
            session.sendAndFlush(MessageBuilder.certifyLogin(session.getSessionId(),
                    session.getUserId(), (short) e.getErrorCode()));
            session.close();
        }
    }

    private static void certifyAuthenticate(String username, char[] password,
                                            int clientVersion) throws AuthenticationException {
        if (clientVersion != Constants.REQUIRED_CLIENT_VERSION) {
            throw new AuthenticationException("Invalid client version.",
                    AuthenticationCode.CLIENT_VERSION);
        }

        final String query = "SELECT id, password FROM users WHERE username = ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    if (!Password.validate(password, result.getString("password"))) {
                        throw new AuthenticationException("Invalid password.",
                                AuthenticationCode.INVALID_PASSWORD);
                    }

                    // Overwrite password with NOPs for security
                    Arrays.fill(password, '\0');

                    int id = result.getInt("id");

                    if (Moderation.isUserBanned(id)) {
                        throw new AuthenticationException("The account is blocked.",
                                AuthenticationCode.ACCOUNT_BLOCKED);
                    }

                    if (UserUtils.isAlreadyConnected(id)) {
                        throw new AuthenticationException("The account is already connected.",
                                AuthenticationCode.ALREADY_CONNECTED);
                    }
                } else {
                    throw new AuthenticationException("The account does not exist",
                            AuthenticationCode.ACCOUNT_NOT_FOUND);
                }
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AuthenticationException(e.getMessage(), AuthenticationCode.AUTH_FAILURE);
        }
    }

    public static synchronized void instantLogin(Session session, ClientMessage msg) {
        int sessionId = msg.readInt();

        int accountId = SessionInfo.getUserId(sessionId);
        int characterId = SessionInfo.getPlayerId(sessionId);

        try {
            instantAuthenticate(accountId, characterId,
                    session.getRemoteAddress(), SessionInfo.getHash(sessionId));

            session.setAuthenticated(true);
            session.setUserId(accountId);
            session.setPlayerId(characterId);
            session.setSessionId(sessionId);
            SessionInfo.resetExpiration(sessionId);

            UserInfo.setServer(ServerManager.getServerId(), session.getUserId());
            UserInfo.setOnline(0, session.getUserId());

            ServerManager.addPlayer(characterId, session);

            session.send(MessageBuilder.instantLogin(sessionId, (short) 0));
        } catch (AuthenticationException e) {
            session.send(MessageBuilder.instantLogin(sessionId, (short) e.getErrorCode()));
            session.close();
        }
    }

    private static void instantAuthenticate(int accountId, int characterId,
                                            InetSocketAddress address,
                                            String sessionHash) throws AuthenticationException {
        final String query = "SELECT 1 FROM users WHERE id = ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, accountId);

            try (ResultSet result = stmt.executeQuery()) {
                if (!result.next()) {
                    throw new AuthenticationException("The account does not exist.",
                            AuthenticationCode.ACCOUNT_NOT_FOUND);
                }

                if (Moderation.isUserBanned(accountId)) {
                    throw new AuthenticationException("The account is blocked.",
                            AuthenticationCode.ACCOUNT_BLOCKED);
                }
                boolean connected = UserUtils.isAlreadyConnected(accountId);

                // Give a bit of time and try again (3 attempts)
                for (int i = 0; i < 4 && connected; i++) {
                    ThreadUtils.sleep(500);
                    connected = UserUtils.isAlreadyConnected(accountId);
                }

                if (connected) {
                    throw new AuthenticationException("The account is already connected.",
                            AuthenticationCode.ALREADY_CONNECTED);
                }

                if (Password.isInvalidAddressHash(address, sessionHash)) {
                    throw new AuthenticationException("Invalid session hash.",
                            AuthenticationCode.SYSTEM_PROBLEM);
                }

                if (!UserInfo.hasCharacter(characterId, accountId)) {
                    throw new AuthenticationException("The account if not the character owner.",
                            AuthenticationCode.ACCESS_FAILURE);
                }
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AuthenticationException(e.getMessage(), AuthenticationCode.AUTH_FAILURE);
        }
    }

    public static synchronized void gameLogin(Session session, ClientMessage msg) {
        int sessionId = msg.readInt();

        int accountId = SessionInfo.getUserId(sessionId);
        int characterId = SessionInfo.getPlayerId(sessionId);

        String hash = SessionInfo.getHash(sessionId);

        // Reject the request if the character assigned to
        // this session is not the same as the one specified by the client
        if (characterId != msg.readInt()) {
            session.close();
            return;
        }

        try {
            gameAuthenticate(accountId, characterId, session.getRemoteAddress(), hash);

            session.setAuthenticated(true);
            SessionInfo.resetExpiration(sessionId);

            session.setUserId(accountId);
            session.setPlayerId(characterId);
            session.setSessionId(sessionId);

            UserInfo.setServer(ServerManager.getServerId(), session.getUserId());
            UserInfo.setOnline(characterId, session.getUserId());

            ServerManager.addPlayer(characterId, session);
            LobbyManager.addPlayer(characterId);

            ClubManager.onMemberConnectedStateChanged(session);

            session.send(MessageBuilder.gameLogin((short) 0));
        } catch (AuthenticationException e) {
            session.send(MessageBuilder.gameLogin((short) e.getErrorCode()));
            session.close();
        }
    }

    private static void gameAuthenticate(int accountId, int characterId,
                                         InetSocketAddress address,
                                         String sessionHash) throws AuthenticationException {
        if (ServerManager.isServerFull()) {
            throw new AuthenticationException("The server is full.", -4);
        }

        if (!CharacterUtils.characterExist(characterId)) {
            throw new AuthenticationException("The character does not exist.", -2);
        }

        if (PlayerInfo.getOwner(characterId) != accountId) {
            throw new AuthenticationException("The account is not the character owner.", -2);
        }

        boolean connected = UserUtils.isAlreadyConnected(accountId);

        // Give a bit of time and try again (3 attempts)
        for (int i = 0; i < 4 && connected; i++) {
            ThreadUtils.sleep(500);
            connected = UserUtils.isAlreadyConnected(accountId);
        }

        if (connected || ServerManager.isPlayerConnected(characterId)) {
            throw new AuthenticationException("Already connected.", -3);
        }

        try {
            if (Password.isInvalidAddressHash(address, sessionHash)) {
                throw new AuthenticationException("Invalid session hash.",
                        AuthenticationCode.SYSTEM_PROBLEM);
            }

            if (CharacterUtils.shouldUpdatePosition(characterId)) {
                throw new AuthenticationException("Player must update to an advanced position.",
                        AuthenticationCode.SYSTEM_PROBLEM);
            }
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new AuthenticationException(e.getMessage(), AuthenticationCode.SYSTEM_PROBLEM);
        }
    }

    public static void udpConfirm(Session session) {
        synchronized (session.getLocker()) {
            if (!session.isUdpAuthenticated()) {
                try {
                    session.getLocker().wait(5000);
                } catch (InterruptedException e) {
                    Output.println(e.getMessage(), Level.DEBUG);
                }
            }
        }

        boolean authenticated = session.isUdpAuthenticated();

        session.send(MessageBuilder.udpConfirm(authenticated));

        if (!authenticated) {
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
