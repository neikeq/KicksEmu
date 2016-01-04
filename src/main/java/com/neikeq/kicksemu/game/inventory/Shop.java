package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.types.Animation;
import com.neikeq.kicksemu.game.clubs.ClubInfo;
import com.neikeq.kicksemu.game.clubs.MemberInfo;
import com.neikeq.kicksemu.game.clubs.MemberRole;
import com.neikeq.kicksemu.game.clubs.Uniform;
import com.neikeq.kicksemu.game.clubs.UniformType;
import com.neikeq.kicksemu.game.inventory.products.Celebration;
import com.neikeq.kicksemu.game.inventory.products.Item;
import com.neikeq.kicksemu.game.inventory.products.Product;
import com.neikeq.kicksemu.game.inventory.products.Skill;
import com.neikeq.kicksemu.game.inventory.products.Training;
import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.game.inventory.types.ItemType;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.network.packets.in.MessageException;
import com.neikeq.kicksemu.game.table.CeleInfo;
import com.neikeq.kicksemu.game.table.BonusInfo;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.game.table.ItemInfo;
import com.neikeq.kicksemu.game.table.LearnInfo;
import com.neikeq.kicksemu.game.table.OptionInfo;
import com.neikeq.kicksemu.game.table.SkillInfo;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.storage.ConnectionRef;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public class Shop {

    private static final byte SKILL_SLOTS_LIMIT = 12;

    public static void purchaseSkill(Session session, ClientMessage msg) {
        try (ConnectionRef con = ConnectionRef.ref()) {
            int playerId = session.getPlayerId();
            SkillRequest request = new SkillRequest(msg);

            if (isInvalidPaymentMode(request)) {
                return;
            }

            if (request.isInvalidExpirationMode()) {
                throw new MessageException("Invalid expiration mode.", -1);
            }

            short position = session.getCache().getPosition();
            SkillInfo skillInfo = TableManager.getSkillInfo(s ->
                    (s.getId() == request.getProductId()) && s.isCompatiblePosition(position));

            if (skillInfo == null) {
                throw new MessageException("Skill does not exist or incompatible position.", -1);
            }

            if (skillInfo.isIncompatibleLevel(PlayerInfo.getLevel(playerId))) {
                throw new MessageException("Incompatible level.", -9);
            }

            if (skillInfo.isInvalidPaymentMode(request.getPayment())) {
                throw new MessageException("Incompatible payment mode.",
                        (request.getPayment() == Payment.CASH) ? -2 : -3);
            }

            if (skillInfo.isInvalidPrice(request.getPrice(),
                    request.getExpiration(), request.getPayment())) {
                throw new MessageException("Invalid price.",
                        (request.getPayment() == Payment.CASH) ? -2 : -3);
            }

            if (request.getPrice() > getMoneyForPaymentMode(request.getPayment(), session)) {
                throw new MessageException("Not enough money.",
                        (request.getPayment() == Payment.CASH) ? -8 : -5);
            }

            Map<Integer, Skill> skills = session.getCache().getSkills(con);
            if (alreadyPurchased(request.getProductId(), skills.values())) {
                throw new MessageException("Skill already purchased.", -10);
            }

            Skill skill = doSkillTransaction(session, request);
            session.send(MessageBuilder.purchaseSkill(playerId, skill, (short) 0, con));

        } catch (MessageException e) {
            session.send(MessageBuilder.purchaseSkill(0, null, (short) e.getErrorCode()));
        } catch (SQLException e) {
            Output.println("Exception when purchasing Skill: " + e.getMessage(), Level.DEBUG);
        }
    }

    public static void purchaseCele(Session session, ClientMessage msg) {
        try (ConnectionRef con = ConnectionRef.ref()) {
            int playerId = session.getPlayerId();
            CelebrationRequest request = new CelebrationRequest(msg);

            if (isInvalidPaymentMode(request)) {
                return;
            }

            if (request.isInvalidExpirationMode()) {
                throw new MessageException("Invalid expiration mode.", -1);
            }

            CeleInfo celeInfo = TableManager.getCeleInfo(c ->
                    c.getId() == request.getProductId());

            if (celeInfo == null) {
                throw new MessageException("Celebration does not exist.", -1);
            }

            if (celeInfo.isIncompatibleLevel(PlayerInfo.getLevel(playerId))) {
                throw new MessageException("Incompatible level.", -9);
            }

            if (celeInfo.isInvalidPaymentMode(request.getPayment())) {
                throw new MessageException("Incompatible payment mode.",
                        (request.getPayment() == Payment.CASH) ? -2 : -3);
            }

            if (celeInfo.isInvalidPrice(request.getPrice(),
                    request.getExpiration(), request.getPayment())) {
                throw new MessageException("Invalid price.",
                        (request.getPayment() == Payment.CASH) ? -2 : -3);
            }

            if (request.getPrice() > getMoneyForPaymentMode(request.getPayment(), session)) {
                throw new MessageException("Not enough money.",
                        (request.getPayment() == Payment.CASH) ? -8 : -5);
            }

            Map<Integer, Celebration> celebrations = session.getCache().getCelebrations(con);
            if (alreadyPurchased(request.getProductId(), celebrations.values())) {
                throw new MessageException("Celebration already purchased.", -10);
            }

            Celebration cele = doCelebrationTransaction(session, request);
            session.send(MessageBuilder.purchaseCele(playerId, cele, (short) 0, con));

        } catch (MessageException e) {
            session.send(MessageBuilder.purchaseCele(0, null, (short) e.getErrorCode()));
        } catch (SQLException e) {
            Output.println("Exception when purchasing Celebration: " +
                    e.getMessage(), Level.DEBUG);
        }
    }

    public static void purchaseLearn(Session session, ClientMessage msg) {
        try (ConnectionRef con = ConnectionRef.ref()) {
            int playerId = session.getPlayerId();
            LearnRequest request = new LearnRequest(msg);

            if (isInvalidPaymentMode(request)) {
                return;
            }

            LearnInfo learnInfo = TableManager.getLearnInfo(c ->
                    c.getId() == request.getProductId());

            if (learnInfo == null) {
                throw new MessageException("Training does not exist.", -1);
            }

            if (learnInfo.isIncompatibleLevel(PlayerInfo.getLevel(playerId))) {
                throw new MessageException("Incompatible level.", -9);
            }

            if (learnInfo.isInvalidPaymentMode(request.getPayment())) {
                throw new MessageException("Incompatible payment mode.",
                        (request.getPayment() == Payment.CASH) ? -2 : -3);
            }

            if (learnInfo.isInvalidPrice(request.getPrice(), request.getPayment())) {
                throw new MessageException("Invalid price.",
                        (request.getPayment() == Payment.CASH) ? -2 : -3);
            }

            if (request.getPrice() > getMoneyForPaymentMode(request.getPayment(), session)) {
                throw new MessageException("Not enough money.",
                        (request.getPayment() == Payment.CASH) ? -8 : -5);
            }

            Map<Integer, Training> learns = session.getCache().getLearns();
            if (alreadyPurchased(request.getProductId(), learns.values())) {
                throw new MessageException("Training already purchased.", -10);
            }

            Training learn = doLearnTransaction(session, request);
            session.send(MessageBuilder.purchaseLearn(session, learn, (short) 0, con));

        } catch (MessageException e) {
            session.send(MessageBuilder.purchaseLearn(session, null, (short) e.getErrorCode()));
        } catch (SQLException e) {
            Output.println("Exception when purchasing Training: " + e.getMessage(), Level.DEBUG);
        }
    }

    public static void purchaseItem(Session session, ClientMessage msg) {
        try (ConnectionRef con = ConnectionRef.ref()) {
            int playerId = session.getPlayerId();
            ItemRequest request = new ItemRequest(msg);

            if (isInvalidPaymentMode(request)) {
                return;
            }

            if (request.isInvalidExpirationMode()) {
                throw new MessageException("Invalid expiration mode.", -1);
            }

            ItemInfo itemInfo = TableManager.getItemInfo(c -> c.getId() == request.getProductId());

            if (itemInfo == null) {
                throw new MessageException("Item does not exist.", -1);
            }

            boolean isSpecialItem = SpecialItem.isSpecialItem(itemInfo.getType());

            // TODO There should be a cleaner and more flexible way to do this.
            if (!isSpecialItem && (request.getPayment() == Payment.POINTS) &&
                    (request.getExpiration() == Expiration.DAYS_PERM)) {
                return;
            }

            if (isClubSpecialItem(itemInfo)) {
                throw new MessageException("Club special items are not yet implemented.", -11);
            }

            if (isClubItem(itemInfo)) {
                throw new MessageException("Club items are not available in this shop.", -12);
            }

            if (itemInfo.isIncompatibleGender(session.getCache().getAnimation())) {
                throw new MessageException("Incompatible gender.", -1);
            }

            short level = PlayerInfo.getLevel(playerId);

            validateItemOptions(session, request, itemInfo, level);

            if (itemInfo.isIncompatibleLevel(level)) {
                throw new MessageException("Incompatible level.", -9);
            }

            if (itemInfo.isInvalidPaymentMode(request.getPayment())) {
                throw new MessageException("Incompatible payment mode.",
                        (request.getPayment() == Payment.CASH) ? -2 : -3);
            }

            if (request.getPrice() > getMoneyForPaymentMode(request.getPayment(), session)) {
                throw new MessageException("Not enough money.",
                        (request.getPayment() == Payment.CASH) ? -8 : -5);
            }

            if (isInventoryFull(session, con)) {
                throw new MessageException("Inventory is full.", -10);
            }

            if (isSpecialItem) {
                SpecialItem.applyEffect(itemInfo, session);
                chargePlayer(session, request);
            } else {
                doItemTransaction(session, request);
            }

            session.send(MessageBuilder.purchaseItem(session, (short) 0, con));
            CharacterManager.sendItemList(session);
            CharacterManager.sendItemsInUseForcedUpdate(session);

        } catch (MessageException e) {
            session.send(MessageBuilder.purchaseItem(session, (short) e.getErrorCode()));
        } catch (SQLException e) {
            Output.println("Exception when purchasing Item: " + e.getMessage(), Level.DEBUG);
        }
    }

    private static void validateItemOptions(Session session, ItemRequest request,
                                            ItemInfo itemInfo, short level)
            throws MessageException {

        OptionInfo optionInfoOne = TableManager.getOptionInfo(c ->
                c.getId() == request.getBonusOne());
        OptionInfo optionInfoTwo = TableManager.getOptionInfo(c ->
                c.getId() == request.getBonusTwo());

        if (request.hasInvalidBonus(optionInfoOne, optionInfoTwo)) {
            throw new MessageException("Invalid bonus.", -1);
        }

        if (request.hasIncompatibleBonusLevel(optionInfoOne, optionInfoTwo, level)) {
            throw new MessageException("Incompatible bonus level.", -9);
        }

        validateItemBonuses(request, itemInfo, optionInfoOne, optionInfoTwo);

        if (isInvalidItemPrice(request, itemInfo, optionInfoOne, optionInfoTwo)) {
            throw new MessageException("Invalid price.",
                    (request.getPayment() == Payment.CASH) ? -2 : -3);
        }

        if (itemInfo.getType() == ItemType.SKILL_SLOT.toInt()) {
            byte skillSlots = PlayerInfo.getSkillSlots(session.getCache().getItems());
            int purchasableSkillSlots = SKILL_SLOTS_LIMIT - skillSlots;
            if ((optionInfoOne == null) || (optionInfoOne.getValue() > purchasableSkillSlots)) {
                throw new MessageException("Cannot purchase more skill slots.", -12);
            }
        }
    }

    private static void validateItemBonuses(ItemRequest request, ItemInfo itemInfo,
                                            OptionInfo one, OptionInfo two)
            throws MessageException {

        BonusInfo itemBonusInfo = TableManager.getBonusInfo(bi ->
                bi.getType() == itemInfo.getType());

        if (itemBonusInfo.isIncompatibleWithExpiration(request.getExpiration())) {
            throw new MessageException("Item type is incompatible with expiration mode.", -1);
        }

        if (itemBonusInfo.isIncompatibleWithBonuses(one, two)) {
            throw new MessageException("Item is incompatible with the specified bonus.", -1);
        }
    }

    private static Skill doSkillTransaction(Session session, SkillRequest request) {
        Map<Integer, Skill> skills = session.getCache().getSkills();

        int inventoryId = InventoryUtils.getSmallestMissingId(skills.values());
        byte index = InventoryUtils.skillSlotsAreFull(session) ? 0 :
                InventoryUtils.getSmallestMissingIndex(skills.values());

        Skill skill = new Skill(request.getProductId(), inventoryId,
                request.getExpiration().toInt(), index,
                InventoryUtils.expirationToTimestamp(request.getExpiration()), true);

        PlayerInfo.addInventorySkill(skill, session.getPlayerId());
        session.getCache().addSkill(inventoryId, skill);

        chargePlayer(session, request);

        return skill;
    }

    private static Celebration doCelebrationTransaction(Session session, CelebrationRequest request) {
        Map<Integer, Celebration> celebrations = session.getCache().getCelebrations();

        int inventoryId = InventoryUtils.getSmallestMissingId(celebrations.values());
        byte index = InventoryUtils.getSmallestMissingIndex(celebrations.values());
        index = (index > 5) ? 0 : index;

        Celebration cele = new Celebration(request.getProductId(), inventoryId,
                request.getExpiration().toInt(), index,
                InventoryUtils.expirationToTimestamp(request.getExpiration()), true);

        PlayerInfo.addInventoryCele(cele, session.getPlayerId());
        session.getCache().addCele(inventoryId, cele);

        chargePlayer(session, request);

        return cele;
    }

    private static Training doLearnTransaction(Session session, LearnRequest request) {
        Map<Integer, Training> learns = session.getCache().getLearns();

        int inventoryId = InventoryUtils.getSmallestMissingId(learns.values());

        Training learn = new Training(request.getProductId(), inventoryId, true);

        PlayerInfo.addInventoryTraining(learn, session.getPlayerId());
        session.getCache().addLearn(inventoryId, learn);

        chargePlayer(session, request);

        return learn;
    }

    private static Item doItemTransaction(Session session, ItemRequest request) {
        Map<Integer, Item> items = session.getCache().getItems();

        int inventoryId = InventoryUtils.getSmallestMissingId(items.values());

        Item item = new Item(request.getProductId(), inventoryId,
                request.getExpiration().toInt(), request.getBonusOne(),
                request.getBonusTwo(), request.getExpiration().getUsages(),
                InventoryUtils.expirationToTimestamp(request.getExpiration()), false, true);

        PlayerInfo.addInventoryItem(item, session.getPlayerId());
        session.getCache().addItem(inventoryId, item);
        CharacterUtils.updateItemsInUse(item, session);

        chargePlayer(session, request);

        return item;
    }

    private static void chargePlayer(Session session, PurchaseRequest request) {
        switch (request.getPayment()) {
            case CASH:
                UserInfo.sumCash(-request.getPrice(), session.getCache().getOwner());
                break;
            case POINTS:
                PlayerInfo.sumPoints(-request.getPrice(), session.getPlayerId());
                break;
            default:
        }
    }

    private static boolean alreadyPurchased(int id, Collection<? extends Product> product) {
        return product.stream().filter(p -> p.getId() == id).findFirst().isPresent();
    }

    private static int getMoneyForPaymentMode(Payment payment, Session session) {
        switch (payment) {
            case CASH:
                return UserInfo.getCash(session.getCache().getOwner());
            case POINTS:
                return PlayerInfo.getPoints(session.getPlayerId());
            default:
                return 0;
        }
    }

    private static boolean isInvalidItemPrice(ItemRequest request, ItemInfo itemInfo,
                                              OptionInfo one, OptionInfo two) {
        int itemPrice = InventoryUtils.getItemPrice(itemInfo, request.getExpiration(),
                request.getPayment(), one, two);

        return (itemPrice < 0) || (itemPrice != request.getPrice());
    }

    private static boolean isInvalidPaymentMode(PurchaseRequest request) {
        return (request.getPayment() == null) || (request.getPayment() == Payment.BOTH);
    }

    private static boolean isInventoryFull(Session session, ConnectionRef... con) {
        return session.getCache().getItems(con).size() >= InventoryManager.MAX_INVENTORY_ITEMS;
    }

    /* TODO Should be removed after implementing tournament tickets and club sponsorship items */
    private static boolean isClubSpecialItem(ItemInfo itemInfo) {
        return (itemInfo.getType() <= 209) && (itemInfo.getType() >= 205);
    }

    private static boolean isClubItem(ItemInfo itemInfo) {
        return (itemInfo.getType() / 100) == 3;
    }

    public static void setClubUniform(Session session, ClientMessage msg) {
        try (ConnectionRef con = ConnectionRef.ref()) {
            int playerId = session.getPlayerId();
            int clubId = MemberInfo.getClubId(playerId);

            if (clubId <= 0) {
                throw new MessageException("Player is not a club member.", -4);
            }

            if (MemberInfo.getRole(playerId, con) != MemberRole.MANAGER) {
                throw new MessageException("Player is not the club manager.", -5);
            }

            if (!ClubInfo.isUniformActive(clubId, con)) {
                throw new MessageException("Club Uniform item is required.", -3);
            }

            byte typeCode = msg.readByte();
            UniformType uniformType = UniformType.fromByte(typeCode);

            if (uniformType == UniformType.NONE) {
                Output.println("Received SetClubUniform message with invalid UniformType: " +
                        typeCode, Level.DEBUG);
                return;
            }

            Uniform uniform = new Uniform(msg.readInt(), msg.readInt(),
                    msg.readInt(), msg.readInt());

            validateUniform(uniform);

            if (uniformType == UniformType.HOME) {
                ClubInfo.setHomeUniform(uniform, clubId, con);
            } else {
                ClubInfo.setAwayUniform(uniform, clubId, con);
            }

            session.send(MessageBuilder.setClubUniform(uniformType, uniform, (short) 0));

        } catch (MessageException e) {
            session.send(MessageBuilder.setClubUniform(null, null, (short) e.getErrorCode()));
        } catch (SQLException e) {
            Output.println("Exception when updating Club Uniform: " +
                    e.getMessage(), Level.DEBUG);
        }
    }

    private static void validateUniform(Uniform uniform) throws MessageException {
        if (isInvalidUniformItem(uniform.getShirts(), ItemType.SHIRTS)) {
            throw new MessageException("Invalid shirt.", -6);
        }

        if (isInvalidUniformItem(uniform.getPants(), ItemType.PANTS)) {
            throw new MessageException("Invalid pants.", -7);
        }

        if (isInvalidUniformItem(uniform.getSocks(), ItemType.SOCKS)) {
            throw new MessageException("Invalid socks.", -8);
        }

        if (isInvalidUniformItem(uniform.getWrist(), ItemType.WRIST)) {
            throw new MessageException("Invalid wrist.", -9);
        }
    }

    private static boolean isInvalidUniformItem(int itemId, ItemType requiredType) {
        ItemInfo itemInfo = TableManager.getItemInfo(item -> item.getId() == itemId);
        return (itemInfo == null) || (itemInfo.getGender() != Animation.ANY) ||
                (itemInfo.getType() != requiredType.toInt());
    }
}
