package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.utils.DateUtils;

import java.util.HashMap;
import java.util.Map;

public class SwapLocker {

    private final Map<Integer, Long> lockedPlayers = new HashMap<>();

    public void lockPlayer(int playerId) {
        int lockTime = 2000;
        lockedPlayers.put(playerId, DateUtils.currentTimeMillis() + lockTime);
    }

    public boolean isPlayerLocked(int playerId) {
        Long expireTime = lockedPlayers.get(playerId);

        return expireTime != null && expireTime > DateUtils.currentTimeMillis();
    }
}
