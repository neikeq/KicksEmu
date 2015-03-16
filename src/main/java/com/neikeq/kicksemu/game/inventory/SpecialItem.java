package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.sessions.Session;

public class SpecialItem {

    public static boolean isSpecialItem(int itemType) {
        switch (itemType) {
            case 204:
            case 100:
                return true;
            default:
                return false;
        }
    }

    public static void handle(int itemId, int itemType,
                              Session session) {
        int playerId = session.getPlayerId();

        switch (itemType) {
            case 204:
                CharacterManager.resetStats(playerId);
                break;
            case 100:
                PlayerInfo.setFace((short)(itemId / 10 - 100000), playerId);
                break;
            default:
        }
    }
}
