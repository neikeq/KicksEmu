package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class Shop {

    // TODO Add security checks (level, position, price, expiration, etc).
    public static void purchaseSkill(Session session, ClientMessage msg) {
        Payment payment = Payment.fromInt(msg.readByte());
        int price = msg.readInt();
        int skillId = msg.readInt();
        int expiration = msg.readInt();

        if (payment != null) {
            int playerId = session.getPlayerId();
            int money = getMoneyFromPaymentMode(payment, playerId);

            byte result = 0;
            Skill skill = null;

            if ((payment == Payment.KASH && price > 50) || (payment == Payment.POINTS && price > 1000)) {
                if (price <= money) {
                    Map<Integer, Skill> skills = PlayerInfo.getInventorySkills(playerId);

                    if (alreadyPurchasedSkill(skillId, skills.values())) {
                        result = -10; // Already purchased
                    } else {
                        int inventoryId = getSmallestMissingId(skills.values());
                        byte selectionIndex = getSmallestMissingIndex(skills.values());
                        Timestamp timestamp = InventoryUtils.expirationToTimestamp(expiration);

                        skill = new Skill(skillId, inventoryId, expiration, selectionIndex,
                                timestamp.getTime() / 1000, true);

                        skills.put(inventoryId, skill);

                        PlayerInfo.setInventorySkills(skills, playerId);

                        sumMoneyToPaymentMode(payment, playerId, -price);
                    }
                } else {
                    result = (byte) (payment == Payment.KASH ? -8 : -5);
                }
            } else {
                result = -1;
            }

            ServerMessage response = MessageBuilder.purchaseSkill(playerId, skill, result);
            session.send(response);
        }
    }

    private static byte getSmallestMissingIndex(Collection<Skill> skills) {
        List<Byte> indexes = new ArrayList<>();

        indexes.addAll(skills.stream().map(Skill::getSelectionIndex)
                .collect(Collectors.toList()));

        for (byte i = 1; i <= skills.size() + 1; i++) {
            if (!indexes.contains(i)) {
                return i;
            }
        }

        return 1;
    }

    private static int getSmallestMissingId(Collection<Skill> skills) {
        List<Integer> ids = new ArrayList<>();

        ids.addAll(skills.stream().map(Skill::getInventoryId).collect(Collectors.toList()));

        for (int i = 0; i < skills.size() + 1; i++) {
            if (!ids.contains(i)) {
                return i;
            }
        }

        return 1;
    }

    public static boolean alreadyPurchasedSkill(int skillId, Collection<Skill> skills) {
        return skills.stream().filter(s -> s.getId() == skillId).findFirst().isPresent();
    }

    public static int getMoneyFromPaymentMode(Payment payment, int playerId) {
        switch (payment) {
            case KASH:
                return UserInfo.getKash(PlayerInfo.getOwner(playerId));
            case POINTS:
                return PlayerInfo.getPoints(playerId);
            default:
                return 0;
        }
    }

    public static void sumMoneyToPaymentMode(Payment payment, int playerId, int value) {
        switch (payment) {
            case KASH:
                UserInfo.setKash(value, PlayerInfo.getOwner(playerId));
            case POINTS:
                PlayerInfo.setPoints(value, playerId);
            default:
        }
    }
}
