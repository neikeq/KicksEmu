package com.neikeq.kicksemu.game.lists.ignored;

import java.util.ArrayList;
import java.util.List;

public class IgnoredList {

    private final List<Integer> ignoredPlayers;

    public void addPlayer(int playerId) {
        ignoredPlayers.add(playerId);
    }

    public void removePlayer(int playerId) {
        int index = ignoredPlayers.indexOf(playerId);

        if (index != -1) {
            ignoredPlayers.remove(index);
        }
    }

    public boolean containsPlayer(int playerId) {
        return ignoredPlayers.contains(playerId);
    }

    public int size() {
        return ignoredPlayers.size();
    }

    public List<Integer> fromPage(byte page) {
        List<Integer> players = new ArrayList<>();

        int index = page * 10;

        for (int i = index; i < index + 10 && i < ignoredPlayers.size(); i++) {
            players.add(ignoredPlayers.get(i));
        }

        return players;
    }

    public static IgnoredList fromString(String strPlayers) {
        IgnoredList ignoredList = new IgnoredList();

        for (String playerId : strPlayers.split(",")) {
            if (playerId.isEmpty()) {
                break;
            }

            ignoredList.addPlayer(Integer.parseInt(playerId));
        }

        return ignoredList;
    }

    @Override
    public String toString() {
        String strPlayers = "";

        for (int i = 0; i < ignoredPlayers.size(); i++) {
            strPlayers += (i > 0 ? "," :  "");
            strPlayers += String.valueOf(ignoredPlayers.get(i));
        }

        return strPlayers;
    }

    private IgnoredList() {
        ignoredPlayers = new ArrayList<>();
    }
}
