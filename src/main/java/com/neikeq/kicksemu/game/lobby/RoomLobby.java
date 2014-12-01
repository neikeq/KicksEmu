package com.neikeq.kicksemu.game.lobby;

import java.util.ArrayList;
import java.util.List;

public class RoomLobby implements Lobby {

    private final List<Integer> players = new ArrayList<>();

    private final Object locker = new Object();

    public List<Integer> getPlayers() {
        return players;
    }

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
}
