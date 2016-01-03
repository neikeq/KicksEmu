package com.neikeq.kicksemu.game.lobby;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
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
        List<Integer> players = new ArrayList<>();

        byte page = msg.readByte();
        int index = page * PLAYERS_PER_PAGE;

        try (Connection con = MySqlManager.getConnection()) {
            List<Integer> visiblePlayers = getMainLobby().getVisiblePlayers(con);

            for (int i = index; i < (index + 10); i++) {
                if (i < visiblePlayers.size()) {
                    players.add(visiblePlayers.get(i));
                } else {
                    break;
                }
            }

            if (!players.isEmpty()) {
                Integer[] playersArray = new Integer[players.size()];

                session.send(MessageBuilder.lobbyList(players.toArray(playersArray),
                        page, (short) 0, con));
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
