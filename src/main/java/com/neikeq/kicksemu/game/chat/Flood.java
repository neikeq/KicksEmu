package com.neikeq.kicksemu.game.chat;

import com.neikeq.kicksemu.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Flood {

    private static final Map<Integer, Long> lockedPlayers = new HashMap<>();
    private static final Map<Integer, ArrayList<Long>> chatHistory =  new HashMap<>();

    private static final int LOCK_TIME = 10000;
    private static final int CHAT_LIMIT = 4;
    private static final int TIME_LIMIT = 3000;

    private static void lockPlayer(int playerId) {
        lockedPlayers.put(playerId, DateUtils.currentTimeMillis() + LOCK_TIME);
    }

    public static boolean onPlayerChat(int playerId) {
        Long currentTime = DateUtils.currentTimeMillis();

        if (!chatHistory.containsKey(playerId)) {
            chatHistory.put(playerId, new ArrayList<>());
        }

        List<Long> chatTimes = chatHistory.get(playerId);

        if (chatTimes.size() > CHAT_LIMIT) {
            chatTimes.remove(0);
        }

        if ((chatTimes.size() >= CHAT_LIMIT) && ((currentTime - chatTimes.get(0)) < TIME_LIMIT)) {
            lockPlayer(playerId);
        } else {
            chatTimes.add(currentTime);
        }

        return isPlayerLocked(playerId);
    }

    public static boolean isPlayerLocked(int playerId) {
        Long expireTime = lockedPlayers.get(playerId);

        return (expireTime != null) && (expireTime > DateUtils.currentTimeMillis());
    }
}
