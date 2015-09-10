package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.types.Position;
import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.game.inventory.types.ItemType;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.game.table.CeleInfo;
import com.neikeq.kicksemu.game.table.BonusInfo;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.game.table.ItemInfo;
import com.neikeq.kicksemu.game.table.LearnInfo;
import com.neikeq.kicksemu.game.table.OptionInfo;
import com.neikeq.kicksemu.game.table.SkillInfo;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public class Shop {

    private static final byte SLOTS_LIMIT = 12;

    public static void purchaseSkill(Session session, ClientMessage msg) {
        Payment payment = Payment.fromInt(msg.readByte());
        int price = msg.readInt();
        int skillId = msg.readInt();
        Expiration expiration = Expiration.fromInt(msg.readInt());

        // If the payment mode is invalid, ignore the request
        if (payment == null || payment == Payment.BOTH) return;

        int playerId = session.getPlayerId();
        int money = getMoneyFromPaymentMode(payment, session);
        short position = session.getCache().getPosition();
        short level = PlayerInfo.getLevel(playerId);

        // Get the information about the skill with the requested id which
        // is available for the player's position or its base position
        SkillInfo skillInfo = TableManager.getSkillInfo(s -> s.getId() == skillId &&
                (s.getPosition() == position || s.getPosition() == Position.basePosition(position)));

        Skill skill = null;
        short result = 0;

        // If there is a skill with this id and the player position is valid for this skill
        if (skillInfo != null && expiration != null) {
            // Reject request if expiration is not based on days
            if (expiration.getType() != 1) return;

            // If the player meets the level requirements for this skill
            if (level >= skillInfo.getLevel()) {
                int skillPrice = skillInfo.getPrice().getPriceFor(expiration, payment);

                // If the price sent by the client is valid
                if (skillPrice != -1 && skillPrice == price &&
                        skillInfo.getPayment().accepts(payment)) {
                    // If the player has enough money
                    if (price <= money) {
                        Map<Integer, Skill> skills = session.getCache().getSkills();

                        // If the item is not already purchased
                        if (notAlreadyPurchased(skillId, skills.values())) {
                            byte skillsInUse = (byte) skills.values().stream()
                                    .filter(s -> s.getSelectionIndex() > 0).count();

                            // Initialize skill with the requested data
                            int id = InventoryUtils.getSmallestMissingId(skills.values());
                            int slots = PlayerInfo.getSkillSlots(session.getCache().getItems());
                            byte index = skillsInUse >= slots ? 0 :
                                    InventoryUtils.getSmallestMissingIndex(skills.values());

                            skill = new Skill(skillId, id, expiration.toInt(), index,
                                    InventoryUtils.expirationToTimestamp(expiration), true);

                            // Add it to the player's inventory
                            skills.put(id, skill);
                            PlayerInfo.addInventorySkill(skill, playerId);
                            // Deduct the price from the player's money
                            sumMoneyToPaymentMode(payment, session, -price);
                        } else {
                            // Already purchased
                            result = -10;
                        }
                    } else {
                        // Not enough money
                        result = (short) (payment == Payment.CASH ? -8 : -5);
                    }
                } else {
                    // The payment mode or price sent by the client is invalid
                    result = (short) (payment == Payment.CASH ? -2 : -3);
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

        try (Connection con = MySqlManager.getConnection()) {
            session.send(MessageBuilder.purchaseSkill(playerId, skill, result, con));
        } catch (SQLException ignored) {}
    }

    public static void purchaseCele(Session session, ClientMessage msg) {
        Payment payment = Payment.fromInt(msg.readByte());
        int price = msg.readInt();
        int celeId = msg.readShort();
        Expiration expiration = Expiration.fromInt(msg.readInt());

        // If the payment mode is invalid, ignore the request
        if (payment == null || payment == Payment.BOTH) return;

        int playerId = session.getPlayerId();
        int money = getMoneyFromPaymentMode(payment, session);
        short level = PlayerInfo.getLevel(playerId);

        // Get the information about the celebration with the requested id
        CeleInfo celeInfo = TableManager.getCeleInfo(c -> c.getId() == celeId);

        Celebration cele = null;
        short result = 0;

        // If there is a cele with this id and the player position is valid for this cele
        if (celeInfo != null && expiration != null) {
            // Reject request if expiration is not based on days
            if (expiration.getType() != 1) return;

            // If the player meets the level requirements for this cele
            if (level >= celeInfo.getLevel()) {
                int celePrice = celeInfo.getPrice().getPriceFor(expiration, payment);

                Map<Integer, Celebration> celes = session.getCache().getCeles();

                // If the price sent by the client is valid
                if (celePrice != -1 && celePrice == price &&
                        celeInfo.getPayment().accepts(payment)) {
                    // If the player has enough money
                    if (price <= money) {
                        // If the item is not already purchased
                        if (notAlreadyPurchased(celeId, celes.values())) {
                            // Initialize cele with the requested data
                            int id = InventoryUtils.getSmallestMissingId(celes.values());
                            byte index = InventoryUtils.getSmallestMissingIndex(celes.values());
                            index = index > 5 ? 0 : index;

                            cele = new Celebration(celeId, id, expiration.toInt(), index,
                                    InventoryUtils.expirationToTimestamp(expiration), true);

                            // Add it to the player's inventory
                            celes.put(id, cele);
                            PlayerInfo.addInventoryCele(cele, playerId);
                            // Deduct the price from the player's money
                            sumMoneyToPaymentMode(payment, session, -price);
                        } else {
                            // Already purchased
                            result = -10;
                        }
                    } else {
                        // Not enough money
                        result = (short) (payment == Payment.CASH ? -8 : -5);
                    }
                } else {
                    // The payment mode or price sent by the client is invalid
                    result = (short) (payment == Payment.CASH ? -2 : -3);
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

        try (Connection con = MySqlManager.getConnection()) {
            session.send(MessageBuilder.purchaseCele(playerId, cele, result, con));
        } catch (SQLException ignored) {}
    }

    public static void purchaseLearn(Session session, ClientMessage msg) {
        Payment payment = Payment.fromInt(msg.readByte());
        int price = msg.readInt();
        int learnId = msg.readInt();

        // If the payment mode is invalid, ignore the request
        if (payment == null || payment == Payment.BOTH) return;

        int playerId = session.getPlayerId();
        int money = getMoneyFromPaymentMode(payment, session);
        short level = PlayerInfo.getLevel(playerId);

        // Get the information about the learn with the requested id
        LearnInfo learnInfo = TableManager.getLearnInfo(c -> c.getId() == learnId);

        Training learn = null;
        short result = 0;

        // If there is a learn with this id and the player position is valid for this learn
        if (learnInfo != null) {
            // If the player meets the level requirements for this learn
            if (level >= learnInfo.getLevel()) {
                int learnPrice = payment == Payment.POINTS ?
                        learnInfo.getPoints() : learnInfo.getCash();

                // If the price sent by the client is valid
                if (learnPrice != -1 && learnPrice == price &&
                        learnInfo.getPayment().accepts(payment)) {
                    // If the player has enough money
                    if (price <= money) {
                        Map<Integer, Training> learns = session.getCache().getLearns();

                        // If the item is not already purchased
                        if (notAlreadyPurchased(learnId, learns.values())) {
                            // Initialize learn with the requested data
                            int id = InventoryUtils.getSmallestMissingId(learns.values());

                            learn = new Training(learnId, id, true);

                            // Add it to the player's inventory
                            learns.put(id, learn);
                            PlayerInfo.addInventoryTraining(learn, playerId);
                            // Deduct the price from the player's money
                            sumMoneyToPaymentMode(payment, session, -price);
                        } else {
                            // Already purchased
                            result = -10;
                        }
                    } else {
                        // Not enough money
                        result = (short) (payment == Payment.CASH ? -8 : -5);
                    }
                } else {
                    // The payment mode or price sent by the client is invalid
                    result = (short) (payment == Payment.CASH ? -2 : -3);
                }
            } else {
                // Invalid level
                result = -9;
            }
        } else {
            // System detected a problem
            // May be due to an invalid learn id
            result = -1;
        }

        try (Connection con = MySqlManager.getConnection()) {
            session.send(MessageBuilder.purchaseLearn(session, learn, result, con));
        } catch (SQLException ignored) {}
    }

    public static void purchaseItem(Session session, ClientMessage msg) {
        Payment payment = Payment.fromInt(msg.readByte());
        int price = msg.readInt();
        int itemId = msg.readInt();
        Expiration expiration = Expiration.fromInt(msg.readInt());
        int bonusOne = msg.readInt();
        int bonusTwo = msg.readInt();

        // If the payment mode is invalid, ignore the request
        if (payment == null || payment == Payment.BOTH) return;
        if (payment == Payment.POINTS && expiration == Expiration.DAYS_PERM) return;

        int playerId = session.getPlayerId();
        int money = getMoneyFromPaymentMode(payment, session);
        short level = PlayerInfo.getLevel(playerId);
        byte skillSlots = PlayerInfo.getSkillSlots(session.getCache().getItems());

        // Get the information about the item with the requested id
        ItemInfo itemInfo = TableManager.getItemInfo(c -> c.getId() == itemId);

        // Avoid purchasing club items
        if (itemInfo != null && (itemInfo.getType() <= 209 && itemInfo.getType() >= 205)) return;

        OptionInfo optionInfoOne = TableManager.getOptionInfo(c -> c.getId() == bonusOne);
        OptionInfo optionInfoTwo = TableManager.getOptionInfo(c -> c.getId() == bonusTwo);

        boolean invalidBonus = (optionInfoOne == null && bonusOne != 0) ||
                (optionInfoTwo == null && bonusTwo != 0);

        boolean validBonusLevel =
                (optionInfoOne == null || optionInfoOne.isValidLevel(level, payment)) &&
                (optionInfoTwo == null || optionInfoTwo.isValidLevel(level, payment));

        boolean validGender = itemInfo != null && (itemInfo.getGender() == 0 ||
                itemInfo.getGender() == session.getCache().getAnimation());

        short result = 0;

        // If the item is a face, set the expiration mode to permanent
        if (itemInfo != null && (itemInfo.getType() == 100 || itemInfo.getType() == 204)) {
            expiration = Expiration.DAYS_PERM;
        }

        // If there is a item with this id and the player position is valid for this item
        if (itemInfo != null && expiration != null && !invalidBonus && validGender) {
            BonusInfo bonusInfo = TableManager.getBonusInfo(c ->
                    c.getType() == itemInfo.getType());

            // Check if the expiration type is allowed for this item
            if (expiration.getType() != bonusInfo.getExpirationType()) return;

            // Ignore message if the stats bonus are not valid for this item type
            int bonusOneType = optionInfoOne == null ? 0 : optionInfoOne.getType();
            int bonusTwoType = optionInfoTwo == null ? 0 : optionInfoTwo.getType();

            if (!bonusInfo.getBonusOne().contains(bonusOneType) ||
                    !bonusInfo.getBonusTwo().contains(bonusTwoType)) return;

            // If the player meets the level requirements for this item
            if (level >= itemInfo.getLevel() && validBonusLevel) {
                int itemPrice = InventoryUtils.getItemPrice(itemInfo, expiration,
                        payment, optionInfoOne, optionInfoTwo);

                // Player has enough space for purchasing more slots
                boolean canPurchaseSlots =  optionInfoOne != null &&
                        optionInfoOne.getValue() <= SLOTS_LIMIT - skillSlots;

                Map<Integer, Item> items = session.getCache().getItems();

                // If the price sent by the client is valid
                if (itemPrice != -1 && itemPrice == price &&
                        itemInfo.getPayment().accepts(payment)) {
                    // If the player has enough money
                    if (price > money) {
                        // Not enough money
                        result = (short) (payment == Payment.CASH ? -8 : -5);
                    } else if (items.size() >= InventoryManager.MAX_INVENTORY_ITEMS) {
                        // Inventory is full
                        result = -10;
                    } else if (canPurchaseSlots ||
                            itemInfo.getType() != ItemType.SKILL_SLOT.toInt()) {
                        if (!SpecialItem.isSpecialItem(itemInfo.getType())) {
                            // Initialize item with the requested data
                            int id = InventoryUtils.getSmallestMissingId(items.values());

                            Item item = new Item(itemId, id, expiration.toInt(),
                                    bonusOne, bonusTwo, expiration.getUsages(),
                                    InventoryUtils.expirationToTimestamp(expiration),
                                    false, true);

                            // Add it to the player's inventory
                            items.put(id, item);
                            // Activate item
                            CharacterUtils.updateItemsInUse(item, session);
                            // Update player's inventory
                            PlayerInfo.addInventoryItem(item, playerId);
                        } else {
                            SpecialItem.handle(itemId, itemInfo.getType(), session);
                        }

                        // Deduct the price from the player's money
                        sumMoneyToPaymentMode(payment, session, -price);
                    } else {
                        // Skill slots limit
                        result = -12;
                    }
                } else {
                    // The payment mode or price sent by the client is invalid
                    result = (short) (payment == Payment.CASH ? -2 : -3);
                }
            } else {
                // Invalid level
                result = -9;
            }
        } else {
            // System detected a problem
            // May be due to an invalid item id
            result = -1;
        }

        try (Connection con = MySqlManager.getConnection()) {
            session.send(MessageBuilder.purchaseItem(session, result, con));

            if (result == 0) {
                CharacterManager.sendItemList(session);
                CharacterManager.sendItemsInUse(session);
            }
        } catch (SQLException ignored) {}
    }

    private static boolean notAlreadyPurchased(int id, Collection<? extends Product> product) {
        return !product.stream().filter(p -> p.getId() == id).findFirst().isPresent();
    }

    private static int getMoneyFromPaymentMode(Payment payment, Session session) {
        switch (payment) {
            case CASH:
                return UserInfo.getCash(session.getCache().getOwner());
            case POINTS:
                return PlayerInfo.getPoints(session.getPlayerId());
            default:
                return 0;
        }
    }

    private static void sumMoneyToPaymentMode(Payment payment, Session session, int value) {
        switch (payment) {
            case CASH:
                UserInfo.sumCash(value, session.getCache().getOwner());
                break;
            case POINTS:
                PlayerInfo.sumPoints(value, session.getPlayerId());
                break;
            default:
        }
    }
}
