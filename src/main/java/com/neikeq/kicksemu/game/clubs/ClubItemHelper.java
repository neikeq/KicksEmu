package com.neikeq.kicksemu.game.clubs;

import com.neikeq.kicksemu.game.table.ItemInfo;
import com.neikeq.kicksemu.network.packets.in.MessageException;

import java.util.HashMap;
import java.util.Map;

public class ClubItemHelper {

    private Map<Integer, ClubItemEffect> clubItemEffects = new HashMap<>();

    public void applyEffect(ItemInfo itemInfo, ClubItemRequest request, int clubId)
            throws MessageException {

        ClubItemEffect effect = clubItemEffects.get(itemInfo.getType());

        if (effect == null) {
            throw new MessageException("Club item has no effect.", -12);
        }

        effect.apply(request, clubId);
    }

    public ClubItemHelper() {
        clubItemEffects.put(301, (itemInfo, clubId) -> {
            if (ClubInfo.isUniformActive(clubId)) {
                throw new MessageException("Club uniform is already purchased.", -8);
            }

            ClubInfo.setUniformActive(true, clubId);
        });
        clubItemEffects.put(302, (itemInfo, clubId) ->
        { throw new MessageException("Club item effect not yet implemented.", -11); });
        clubItemEffects.put(303, (itemInfo, clubId) -> {
            // TODO Result -7: You have already purchased this Number
            throw new MessageException("Club item effect not yet implemented.", -11);
        });
        clubItemEffects.put(304, (itemInfo, clubId) ->
        { throw new MessageException("Club item effect not yet implemented.", -11); });
        clubItemEffects.put(305, (itemInfo, clubId) -> {
            // TODO Result -9: Maximum club members reached
            throw new MessageException("Club item effect not yet implemented.", -11);
        });
        clubItemEffects.put(306, (itemInfo, clubId) ->
        { throw new MessageException("Club item effect not yet implemented.", -11); });
        clubItemEffects.put(309, (itemInfo, clubId) ->
        { throw new MessageException("Club item effect not yet implemented.", -11); });
    }

    @FunctionalInterface
    private interface ClubItemEffect {

        void apply(ClubItemRequest request, int clubId) throws MessageException;
    }
}
