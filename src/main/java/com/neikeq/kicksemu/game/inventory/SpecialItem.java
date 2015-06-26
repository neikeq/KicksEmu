package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.inventory.types.ItemType;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;

class SpecialItem {

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

        ItemType type = ItemType.fromInt(itemType);

        if (type == null) return;

        switch (type) {
            case STATS_RESET:
                CharacterManager.resetStats(playerId);
                session.send(MessageBuilder.playerStats(playerId));
                break;
            case FACE:
                PlayerInfo.setFace((short) (itemId / 10 - 100000), playerId);
                break;
            default:
        }
    }
}
