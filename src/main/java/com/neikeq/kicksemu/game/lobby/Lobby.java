package com.neikeq.kicksemu.game.lobby;

import java.util.List;

public interface Lobby {

    List<Integer> getPlayers();

    void addPlayer(int playerId);
    void removePlayer(int playerId);
}
