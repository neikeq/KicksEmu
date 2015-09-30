package com.neikeq.kicksemu.game.rooms.challenges;

import java.util.Set;

public class WinStreakCache {

    private byte wins;
    private Set<Integer> players;

    public boolean matchesTeam(Set<Integer> team) {
        return team.size() == players.size() &&
                players.stream().noneMatch(player -> !team.contains(player));
    }

    public WinStreakCache(byte wins, Set<Integer> players) {
        this.setWins(wins);
        this.setPlayers(players);
    }

    private WinStreakCache() {
        throw new AssertionError();
    }

    public byte getWins() {
        return wins;
    }

    public void setWins(byte wins) {
        this.wins = wins;
    }

    public Set<Integer> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Integer> players) {
        this.players = players;
    }
}
