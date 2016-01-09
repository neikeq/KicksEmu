package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.clubs.ClubInfo;
import com.neikeq.kicksemu.game.clubs.MemberInfo;
import com.neikeq.kicksemu.game.clubs.Uniform;
import com.neikeq.kicksemu.game.clubs.UniformType;
import com.neikeq.kicksemu.game.inventory.products.Celebration;
import com.neikeq.kicksemu.game.inventory.products.Item;
import com.neikeq.kicksemu.game.inventory.products.Skill;
import com.neikeq.kicksemu.game.inventory.types.ItemType;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.table.OptionInfo;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.in.MessageException;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.storage.ConnectionRef;
import org.apache.commons.lang3.mutable.MutableInt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InventoryManager {

    public static final short MAX_INVENTORY_ITEMS = 70;

    public static void activateSkill(Session session, ClientMessage msg) {
        int playerId = session.getPlayerId();
        int skillId = msg.readInt();

        short result = 0;
        byte newIndex = 0;

        Map<Integer, Skill> skills = session.getCache().getSkills();
        Skill skill = (Skill) InventoryUtils.getByIdFromMap(skills, skillId);

        byte slots = PlayerInfo.getSkillSlots(session.getCache().getItems());
        byte skillsInUse = (byte) skills.values().stream()
                .filter(s -> s.getSelectionIndex() > 0).count();

        // If skill exists and skill is not yet activated
        if ((skill != null) && (skill.getSelectionIndex() <= 0)) {
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
        short result = deactivateSkill(session, skillId, session.getCache().getSkills());

        if (result != 0) {
            session.send(MessageBuilder.deactivateSkill(skillId, result));
        }
    }

    private static short deactivateSkill(Session s, int skillId, Map<Integer, Skill> skills) {
        short result = 0;

        int playerId = s.getPlayerId();
        Skill skill = (Skill) InventoryUtils.getByIdFromMap(skills, skillId);

        // If skill exists and skill is activated
        if ((skill != null) && (skill.getSelectionIndex() > 0)) {
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

        short result = 0;
        byte newIndex = 0;

        Map<Integer, Celebration> celebrations = session.getCache().getCelebrations();
        Celebration cele = (Celebration) InventoryUtils.getByIdFromMap(celebrations, celeId);

        // If cele exists and cele is not yet activated
        if ((cele != null) && (cele.getSelectionIndex() <= 0)) {
            // Activate skill
            newIndex = InventoryUtils.getSmallestMissingIndex(celebrations.values());

            // If there is at least a free slot for the celebration
            // The maximum celebration that can be used are 5
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

        short result = deactivateCele(session, celeId, session.getCache().getCelebrations());

        if (result != 0) {
            session.send(MessageBuilder.deactivateCele(celeId, result));
        }
    }

    private static short deactivateCele(Session s, int celeId, Map<Integer, Celebration> celebrations) {
        short result = 0;

        int playerId = s.getPlayerId();
        Celebration cele = (Celebration) InventoryUtils.getByIdFromMap(celebrations, celeId);

        // If cele exists and cele is activated
        if ((cele != null) && (cele.getSelectionIndex() > 0)) {
            // Deactivate cele
            cele.setSelectionIndex((byte) 0);
            s.send(MessageBuilder.deactivateCele(celeId, result));

            PlayerInfo.setInventoryCele(cele, playerId);
        } else {
            result = -2; // Cele does not exists
        }

        return result;
    }

    public static void activateItem(Session session, ClientMessage msg) {
        int playerId = session.getPlayerId();
        int inventoryId = msg.readInt();

        short result = 0;

        Item item = session.getCache().getItems().get(inventoryId);

        // If item exists
        if ((item != null) && !item.isSelected()) {
            CharacterUtils.updateItemsInUse(item, session);
            PlayerInfo.setInventoryItem(item, playerId);
        } else {
            result = -2; // Skill does not exists
        }

        session.send(MessageBuilder.activateItem(inventoryId, session, result));
    }

    public static void deactivateItem(Session session, ClientMessage msg) {
        int inventoryId = msg.readInt();

        short result = deactivateItem(session,
                session.getCache().getItems().get(inventoryId));

        if (result != 0) {
            session.send(MessageBuilder.deactivateItem(inventoryId, session, result));
        }
    }

    private static short deactivateItem(Session session, Item item) {
        return (item != null) ?
                TableManager.getItemInfo(o -> o.getId() == item.getId()).map(itemInfo -> {
                    // Deactivate item
                    ItemType itemType = ItemType.fromInt(itemInfo.getType())
                            .orElseThrow(IllegalStateException::new);
                    item.deactivateGracefully(itemType, session);
                    session.send(MessageBuilder.deactivateItem(item.getInventoryId(),
                            session, (short) 0));

                    PlayerInfo.setInventoryItem(item, session.getPlayerId());

                    return 0;
                }).orElse(-2).shortValue() : -2; // Item does not exists;
    }

    public static void resellItem(Session session, ClientMessage msg) {
        int playerId = session.getPlayerId();
        int inventoryId = msg.readInt();
        int refund = msg.readInt();

        Map<Integer, Item> items = session.getCache().getItems();

        short result;

        if (items.containsKey(inventoryId)) {
            Item item = items.get(inventoryId);

            result = item.getExpiration().isPermanent() ?
                    TableManager.getItemInfo(ii -> ii.getId() == item.getId())
                            .map(itemInfo -> {
                                Optional<OptionInfo> bonusOne = TableManager.getOptionInfo(oi ->
                                        oi.getId() == item.getBonusOne());
                                Optional<OptionInfo> bonusTwo = TableManager.getOptionInfo(oi ->
                                        oi.getId() == item.getBonusTwo());

                                int itemPrice = InventoryUtils.getItemPrice(itemInfo,
                                        item.getExpiration(), Payment.POINTS, bonusOne, bonusTwo);

                                // If the specified refund is valid
                                if (refund == (itemPrice / 10)) {
                                    PlayerInfo.removeInventoryItem(item, playerId);
                                    items.remove(item.getInventoryId());

                                    PlayerInfo.sumPoints(refund, playerId);
                                } else {
                                    return -3; // Invalid refund
                                }

                                return 0;
                            }).orElse(-2).shortValue() :
                    -4; // The item is not permanent
        } else {
            result = -2; // Item does not exists
        }

        session.send(MessageBuilder.resellItem(session, inventoryId, refund, result));
    }

    public static void mergeItem(Session session, ClientMessage msg) {
        int playerId = session.getPlayerId();
        int inventoryId = msg.readInt();

        // If the player is not in a room
        if (session.getRoomId() <= 0) {
            short result = 0;
            short usages = 0;
            boolean selected = false;

            try (ConnectionRef con = ConnectionRef.ref()) {
                Map<Integer, Item> items = session.getCache().getItems(con);
                Item item = items.get(inventoryId);

                final MutableInt usagesToAdd = new MutableInt(0);
                final List<Integer> toRemove = new ArrayList<>();

                // If the item exists
                if (item != null) {
                    // If this is an usage item
                    if (item.getExpiration().isUsage()) {
                        // Loop over all usage items with the same id and bonuses
                        items.values().stream().filter(i ->
                                (i.getInventoryId() != item.getInventoryId()) &&
                                        i.getExpiration().isUsage() && (i.getId() == item.getId()) &&
                                        (i.getBonusOne() == item.getBonusOne()) &&
                                        (i.getBonusTwo() == item.getBonusTwo())).forEach(i -> {
                            // Add the item's usages to the stacked amount
                            usagesToAdd.add(i.getUsages());

                            // Remove the item
                            toRemove.add(i.getInventoryId());
                            PlayerInfo.removeInventoryItem(i, playerId, con);
                        });

                        if (usagesToAdd.getValue() > 0) {
                            // Remove the items from the cache map
                            toRemove.forEach(items::remove);

                            // Add the stacked usages to the specified item and update it
                            item.sumUsages(usagesToAdd.shortValue());
                            PlayerInfo.setInventoryItem(item, playerId, con);

                            usages = item.getUsages();
                            selected = item.isSelected();
                        } else {
                            result = -4; // Cannot find items to merge with
                        }
                    } else {
                        result = -3; // This item cannot be merged
                    }
                } else {
                    result = -2; // The item does not exists
                }
            } catch (SQLException e) {
                result = -1; // System problem
                Output.println(e.getMessage(), Level.DEBUG);
            }

            session.send(MessageBuilder.mergeItem(session, inventoryId, usages, result));

            if ((result == 0) && selected) {
                session.send(MessageBuilder.activateItem(inventoryId, session, result));
            }
        }
    }

    public static void wearUniform(Session session, ClientMessage msg) {
        UniformType uniformType = UniformType.fromByte(msg.readByte());

        short result = 0;

        try {
            if (uniformType != UniformType.NONE) {
                int clubId = MemberInfo.getClubId(session.getPlayerId());

                if (clubId <= 0) {
                    throw new MessageException("Not a club member.", -3);
                }

                Uniform uniform = ClubInfo.getUniform(clubId).getUniformByType(uniformType);

                if (uniform.isNotEstablished()) {
                    throw new MessageException("Uniform is not established.", -4);
                }
            }

            session.setEquippedUniform(uniformType);
        } catch (MessageException e) {
            result = (short) e.getErrorCode();
        }

        session.send(MessageBuilder.wearUniform(uniformType, result));
    }
}
