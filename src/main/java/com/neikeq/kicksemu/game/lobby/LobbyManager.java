package com.neikeq.kicksemu.game.lobby;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.storage.ConnectionRef;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LobbyManager {

    private static final MainLobby mainLobby = new MainLobby();

    private static final int PLAYERS_PER_PAGE = 10;

    public static void addPlayer(int id) {
        getMainLobby().addPlayer(id);
    }

    public static void removePlayer(int id) {
        getMainLobby().removePlayer(id);
    }

    public static void lobbyList(Session session, ClientMessage msg) {
        try (ConnectionRef con = ConnectionRef.ref()) {
            List<Integer> visiblePlayers = getMainLobby().getVisiblePlayers(con);

            byte pagesCount = (byte) Math.ceil(
                    (double) visiblePlayers.size() / (double) PLAYERS_PER_PAGE);

            byte page = (byte) Math.min(msg.readByte(), pagesCount);
            int indexFrom = page * PLAYERS_PER_PAGE;
            int indexTo = indexFrom + 10;

            List<Integer> players = new ArrayList<>();

            for (int i = indexFrom; (i < indexTo) && (i < visiblePlayers.size()); i++) {
                players.add(visiblePlayers.get(i));
            }

            if (!players.isEmpty()) {
                Integer[] playersArray = new Integer[players.size()];

                session.send(MessageBuilder.lobbyList(players.toArray(playersArray), page, con));
            }
        } catch (SQLException e) {
            Output.println("Exception when handling lobby list message: " +
                    e.getMessage(), Level.DEBUG);
        }
    }

    public static MainLobby getMainLobby() {
        return mainLobby;
    }
}
