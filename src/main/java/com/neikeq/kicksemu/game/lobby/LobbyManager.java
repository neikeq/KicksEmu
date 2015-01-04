package com.neikeq.kicksemu.game.lobby;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

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

        for (int i = index; i < index + 10; i++) {
            if (i < getMainLobby().getPlayers().size()) {
                players.add(getMainLobby().getPlayers().get(i));
            } else {
                break;
            }
        }

        ServerMessage response = MessageBuilder.lobbyList(players, page, (byte)0);
        session.send(response);
    }

    public static MainLobby getMainLobby() {
        return mainLobby;
    }
}
