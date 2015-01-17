package com.neikeq.kicksemu.game.inventory.shop;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Expiration;
import com.neikeq.kicksemu.game.inventory.InventoryUtils;
import com.neikeq.kicksemu.game.inventory.Product;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.table.CeleInfo;
import com.neikeq.kicksemu.game.inventory.table.InventoryTable;
import com.neikeq.kicksemu.game.inventory.table.SkillInfo;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

import java.util.Collection;
import java.util.Map;

public class Shop {

    public static void purchaseSkill(Session session, ClientMessage msg) {
        Payment payment = Payment.fromInt(msg.readByte());
        int price = msg.readInt();
        int skillId = msg.readInt();
        Expiration expiration = Expiration.fromInt(msg.readInt());

        // If the payment mode is invalid, ignore the request
        if (payment == null) return;

        int playerId = session.getPlayerId();
        int money = getMoneyFromPaymentMode(payment, playerId);
        short position = PlayerInfo.getPosition(playerId);
        short level = PlayerInfo.getLevel(playerId);

        // Get the information about the skill with the requested id which
        // is available for the player's position or its base position
        SkillInfo skillInfo = InventoryTable.getSkillInfo(s -> s.getId() == skillId &&
                (s.getPosition() == position || s.getPosition() == (position / 10 * 10)));

        Skill skill = null;
        byte result = 0;

        // If there is a skill with this id and the player position is valid for this skill
        if (skillInfo != null && expiration != null) {
            // If the player meets the level requirements for this skill
            if (level >= skillInfo.getLevel()) {
                int skillPrice = skillInfo.getPrice().getPriceFor(expiration, payment);

                // If the price sent by the client is not invalid
                if (skillPrice != -1 && skillPrice == price &&
                        skillInfo.getPayment().accepts(payment)) {
                    // If the player has enough money
                    if (price <= money) {
                        Map<Integer, Skill> skills = PlayerInfo.getInventorySkills(playerId);

                        // If the item is not already purchased
                        if (!alreadyPurchased(skillId, skills.values())) {
                            // Initialize skill with the requested data
                            int id = InventoryUtils.getSmallestMissingId(skills.values());
                            byte index = InventoryUtils.getSmallestMissingIndex(skills.values());

                            skill = new Skill(skillId, id, expiration.toInt(), index,
                                    InventoryUtils.expirationToTimestamp(expiration), true);

                            // Add it to the player's inventory
                            skills.put(id, skill);
                            PlayerInfo.setInventorySkills(skills, playerId);
                            // Deduct the price from the player's money
                            sumMoneyToPaymentMode(payment, playerId, -price);
                        } else {
                            // Already purchased
                            result = -10;
                        }
                    } else {
                        // Not enough money
                        result = (byte) (payment == Payment.KASH ? -8 : -5);
                    }
                } else {
                    // The payment mode or price sent by the client is invalid
                    result = (byte) (payment == Payment.KASH ? -2 : -3);
                }
            } else {
                // Invalid level
                result = -9;
            }
        } else {
            // System detected a problem
            // May be due to an invalid skill id or an invalid position for this skill
            result = -1;
        }

        ServerMessage response = MessageBuilder.purchaseSkill(playerId, skill, result);
        session.send(response);
    }

    public static void purchaseCele(Session session, ClientMessage msg) {
        Payment payment = Payment.fromInt(msg.readByte());
        int price = msg.readInt();
        int celeId = msg.readShort();
        Expiration expiration = Expiration.fromInt(msg.readInt());

        // If the payment mode is invalid, ignore the request
        if (payment == null) return;

        int playerId = session.getPlayerId();
        int money = getMoneyFromPaymentMode(payment, playerId);
        short level = PlayerInfo.getLevel(playerId);

        // Get the information about the celebration with the requested id
        CeleInfo celeInfo = InventoryTable.getCeleInfo(c -> c.getId() == celeId);

        Celebration cele = null;
        byte result = 0;

        // If there is a cele with this id and the player position is valid for this cele
        if (celeInfo != null && expiration != null) {
            // If the player meets the level requirements for this cele
            if (level >= celeInfo.getLevel()) {
                int celePrice = celeInfo.getPrice().getPriceFor(expiration, payment);

                // If the price sent by the client is not invalid
                if (celePrice != -1 && celePrice == price) {
                    // If the player has enough money
                    if (price <= money) {
                        Map<Integer, Celebration> celes = PlayerInfo.getInventoryCelebration(playerId);

                        // If the item is not already purchased
                        if (!alreadyPurchased(celeId, celes.values())) {
                            // Initialize cele with the requested data
                            int id = InventoryUtils.getSmallestMissingId(celes.values());
                            byte index = InventoryUtils.getSmallestMissingIndex(celes.values());
                            index = index > 9 ? 0 : index;

                            cele = new Celebration(celeId, id, expiration.toInt(), index,
                                    InventoryUtils.expirationToTimestamp(expiration), true);

                            // Add it to the player's inventory
                            celes.put(id, cele);
                            PlayerInfo.setInventoryCelebration(celes, playerId);
                            // Deduct the price from the player's money
                            sumMoneyToPaymentMode(payment, playerId, -price);
                        } else {
                            // Already purchased
                            result = -10;
                        }
                    } else {
                        // Not enough money
                        result = (byte) (payment == Payment.KASH ? -8 : -5);
                    }
                } else {
                    // The payment mode or price sent by the client is invalid
                    result = (byte) (payment == Payment.KASH ? -2 : -3);
                }
            } else {
                // Invalid level
                result = -9;
            }
        } else {
            // System detected a problem
            // May be due to an invalid cele id
            result = -1;
        }

        ServerMessage response = MessageBuilder.purchaseCele(playerId, cele, result);
        session.send(response);
    }

    private static boolean alreadyPurchased(int id, Collection<? extends Product> product) {
        return product.stream().filter(p -> p.getId() == id).findFirst().isPresent();
    }

    private static int getMoneyFromPaymentMode(Payment payment, int playerId) {
        switch (payment) {
            case KASH:
                return UserInfo.getKash(PlayerInfo.getOwner(playerId));
            case POINTS:
                return PlayerInfo.getPoints(playerId);
            default:
                return 0;
        }
    }

    private static void sumMoneyToPaymentMode(Payment payment, int playerId, int value) {
        switch (payment) {
            case KASH:
                UserInfo.setKash(value, PlayerInfo.getOwner(playerId));
            case POINTS:
                PlayerInfo.setPoints(value, playerId);
            default:
        }
    }
}
