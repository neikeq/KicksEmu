package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.table.ItemInfo;
import com.neikeq.kicksemu.game.table.OptionInfo;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;

import java.util.Map;

public class InventoryManager {

    public static final short MAX_INVENTORY_ITEMS = 70;

    public static void activateSkill(Session session, ClientMessage msg) {
        int playerId = session.getPlayerId();
        int skillId = msg.readInt();

        byte result = 0;
        byte newIndex = 0;

        Map<Integer, Skill> skills = PlayerInfo.getInventorySkills(playerId);
        Skill skill = (Skill) InventoryUtils.getByIdFromMap(skills, skillId);

        byte slots = PlayerInfo.getSkillSlots(playerId);
        byte skillsInUse = (byte) skills.values().stream()
                .filter(s -> s.getSelectionIndex() > 0).count();

        // If skill exists and skill is not yet activated
        if (skill != null && skill.getSelectionIndex() <= 0) {
            if (slots > skillsInUse) {
                // Activate skill
                newIndex = InventoryUtils.getSmallestMissingIndex(skills.values());
                skill.setSelectionIndex(newIndex);

                PlayerInfo.setInventorySkill(skill, playerId);
            } else {
                result = -3; // Skill slots are full
            }
        } else {
            result = -2; // Skill does not exists
        }

        session.send(MessageBuilder.activateSkill(skillId, newIndex, result));
    }

    public static void deactivateSkill(Session session, ClientMessage msg) {
        int skillId = msg.readInt();

        byte result = deactivateSkill(session, skillId,
                PlayerInfo.getInventorySkills(session.getPlayerId()));

        if (result != 0) {
            session.send(MessageBuilder.deactivateSkill(skillId, result));
        }
    }

    public static byte deactivateSkill(Session s, int skillId, Map<Integer, Skill> skills) {
        byte result = 0;

        int playerId = s.getPlayerId();
        Skill skill = (Skill) InventoryUtils.getByIdFromMap(skills, skillId);

        // If skill exists and skill is activated
        if (skill != null && skill.getSelectionIndex() > 0) {
            // Deactivate skill
            skill.setSelectionIndex((byte) 0);
            s.send(MessageBuilder.deactivateSkill(skillId, result));

            PlayerInfo.setInventorySkill(skill, playerId);
        } else {
            result = -2; // Skill does not exists
        }

        return result;
    }

    public static void activateCele(Session session, ClientMessage msg) {
        int playerId = session.getPlayerId();
        int celeId = msg.readInt();

        byte result = 0;
        byte newIndex = 0;

        Map<Integer, Celebration> celes = PlayerInfo.getInventoryCelebration(playerId);
        Celebration cele = (Celebration) InventoryUtils.getByIdFromMap(celes, celeId);

        // If cele exists and cele is not yet activated
        if (cele != null && cele.getSelectionIndex() <= 0) {
            // Activate skill
            newIndex = InventoryUtils.getSmallestMissingIndex(celes.values());

            if (newIndex <= 5) {
                cele.setSelectionIndex(newIndex);

                PlayerInfo.setInventoryCele(cele, playerId);
            } else {
                result = -3;
            }
        } else {
            result = -2; // Cele does not exists
        }

        session.send(MessageBuilder.activateCele(celeId, newIndex, result));
    }

    public static void deactivateCele(Session session, ClientMessage msg) {
        int celeId = msg.readInt();

        byte result = deactivateCele(session, celeId,
                PlayerInfo.getInventoryCelebration(session.getPlayerId()));

        if (result != 0) {
            session.send(MessageBuilder.deactivateCele(celeId, result));
        }
    }

    public static byte deactivateCele(Session s, int celeId, Map<Integer, Celebration> celes) {
        byte result = 0;

        int playerId = s.getPlayerId();
        Celebration cele = (Celebration) InventoryUtils.getByIdFromMap(celes, celeId);

        // If cele exists and cele is activated
        if (cele != null && cele.getSelectionIndex() > 0) {
            // Deactivate cele
            cele.setSelectionIndex((byte) 0);
            s.send(MessageBuilder.deactivateCele(celeId, result));

            PlayerInfo.setInventoryCele(cele, playerId);
        } else {
            result = -2; // Cele does not exists
        }

        return result;
    }

    public static void deactivateItem(Session session, ClientMessage msg) {
        int inventoryId = msg.readInt();
        int playerId = session.getPlayerId();

        byte result = deactivateItem(session, PlayerInfo.getInventoryItems(playerId)
                .get(inventoryId));

        if (result != 0) {
            session.send(MessageBuilder.deactivateItem(inventoryId, playerId, result));
        }
    }

    public static void activateItem(Session session, ClientMessage msg) {
        int playerId = session.getPlayerId();
        int inventoryId = msg.readInt();

        byte result = 0;

        Map<Integer, Item> items = PlayerInfo.getInventoryItems(playerId);
        Item item = items.get(inventoryId);

        // If item exists
        if (item != null && !item.isSelected()) {
            CharacterUtils.updateItemsInUse(item, playerId);
            PlayerInfo.setInventoryItem(item, playerId);
        } else {
            result = -2; // Skill does not exists
        }

        session.send(MessageBuilder.activateItem(inventoryId, playerId, result));
    }

    public static byte deactivateItem(Session s, Item item) {
        byte result = 0;

        int playerId = s.getPlayerId();

        // If item exists
        if (item != null) {
            // Deactivate item
            item.deactivateGracefully(playerId);
            s.send(MessageBuilder.deactivateItem(item.getInventoryId(), playerId,  result));

            PlayerInfo.setInventoryItem(item, playerId);
        } else {
            result = -2; // Item does not exists
        }

        return result;
    }

    public static void resellItem(Session session, ClientMessage msg) {
        int playerId = session.getPlayerId();
        int inventoryId = msg.readInt();
        int refund = msg.readInt();

        Map<Integer, Item> items = PlayerInfo.getInventoryItems(playerId);

        byte result = 0;

        if (items.containsKey(inventoryId)) {
            Item item = items.get(inventoryId);

            if (item.getExpiration().isPermanent()) {
                ItemInfo itemInfo = TableManager.getItemInfo(ii -> ii.getId() == item.getId());

                OptionInfo bonusOne = TableManager.getOptionInfo(oi ->
                        oi.getId() == item.getBonusOne());
                OptionInfo bonusTwo = TableManager.getOptionInfo(oi ->
                        oi.getId() == item.getBonusTwo());

                int itemPrice = InventoryUtils.getItemPrice(itemInfo, item.getExpiration(),
                        Payment.POINTS, bonusOne, bonusTwo);

                // If the specified refund is valid
                if (refund == itemPrice / 10) {
                    PlayerInfo.removeInventoryItem(item, playerId);
                    items.remove(item.getInventoryId());

                    PlayerInfo.sumPoints(refund, playerId);
                } else {
                    result = -3; // Invalid refund
                }
            } else {
                result = -4; // The item is not permanent
            }
        } else {
            result = -2; // Item does not exists
        }

        session.send(MessageBuilder.resellItem(playerId, inventoryId, refund, result));
    }
}
