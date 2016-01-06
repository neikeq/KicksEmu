package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.inventory.types.ItemType;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.table.ItemInfo;
import com.neikeq.kicksemu.network.packets.in.MessageException;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;

import java.util.Optional;

class SpecialItem {

    public static boolean isSpecial(int itemType) {
        switch (itemType) {
            case 204:
            case 100:
                return true;
            default:
                return false;
        }
    }

    public static void applyEffect(ItemInfo item, Session session) throws MessageException {
        int player = session.getPlayerId();

        Optional<MessageException> e = ItemType.fromInt(item.getType()).map(type -> {
            MessageException me = null;

            switch (type) {
                case STATS_RESET:
                    CharacterManager.resetStats(player);
                    session.send(MessageBuilder.playerStats(player));
                    break;
                case FACE:
                    PlayerInfo.setFace((short) ((item.getId() / 10) - 100000), player);
                    break;
                default:
                    me = new MessageException("Special item has no effect.", -12);
            }

            return Optional.ofNullable(me);
        }).orElse(Optional.of(new MessageException("Invalid item type for special item.", -1)));

        if (e.isPresent()) {
            throw e.get();
        }
    }
}
