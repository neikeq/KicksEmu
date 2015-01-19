package com.neikeq.kicksemu.game.lobby;

import com.neikeq.kicksemu.game.characters.PlayerInfo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainLobby implements Lobby {

    private final List<Integer> players = new ArrayList<>();

    private final Object locker = new Object();

    @Override
    public synchronized void addPlayer(int playerId) {
        synchronized (locker) {
            if (!players.contains(playerId)) {
                players.add(playerId);
            }
        }
    }

    @Override
    public synchronized void removePlayer(int playerId) {
        synchronized (locker) {
            int index = players.indexOf(playerId);

            if (index >= 0) {
                players.remove(index);
            }
        }
    }

    public List<Integer> getPlayers() {
        return players;
    }

    public List<Integer> getVisiblePlayers(Connection con) {
        return players.stream().filter(id ->
                !PlayerInfo.isModerator(id, con) || PlayerInfo.isVisible(id, con)
        ).collect(Collectors.toList());
    }
}
