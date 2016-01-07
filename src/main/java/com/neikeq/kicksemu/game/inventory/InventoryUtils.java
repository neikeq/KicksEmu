package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.inventory.products.IndexedProduct;
import com.neikeq.kicksemu.game.inventory.products.Product;
import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.table.ItemInfo;
import com.neikeq.kicksemu.game.table.OptionInfo;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class InventoryUtils {

    public static int getItemPrice(ItemInfo itemInfo,
                                   Expiration expiration, Payment payment,
                                   Optional<OptionInfo> maybeBonusOne,
                                   Optional<OptionInfo> maybeBonusTwo) {
        // Basic price without bonuses
        int price = itemInfo.getPrice().getPriceFor(expiration, payment);

        // Add the price for the first bonus
        price += maybeBonusOne.map(bonusOne ->
                bonusOne.getPrice().getPriceFor(expiration, payment))
                .orElse(0);

        // Add the price for the second bonus
        price += maybeBonusTwo.map(bonusTwo ->
                bonusTwo.getPrice().getPriceFor(expiration, payment))
                .orElse(0);

        return price;
    }

    public static Timestamp expirationToTimestamp(Expiration expiration) {
        if (expiration == Expiration.DAYS_PERM) {
            return DateUtils.getTimestamp();
        }

        int days = expiration.toInt() % 1000;

        return DateUtils.toTimestamp(DateUtils.addDays(DateUtils.getSqlDate(), days));
    }

    public static byte getSmallestMissingIndex(Collection<? extends IndexedProduct> products) {
        List<Byte> indexes = new ArrayList<>();

        indexes.addAll(products.stream().map(IndexedProduct::getSelectionIndex)
                .collect(Collectors.toList()));

        for (byte i = 1; i <= (products.size() + 1); i++) {
            if (!indexes.contains(i)) {
                return i;
            }
        }

        return 1;
    }

    public static int getSmallestMissingId(Collection<? extends Product> products) {
        List<Integer> ids = new ArrayList<>();

        ids.addAll(products.stream().map(Product::getInventoryId).collect(Collectors.toList()));

        for (int i = 0; i < (products.size() + 1); i++) {
            if (!ids.contains(i)) {
                return i;
            }
        }

        return -1;
    }

    public static Product getByIdFromMap(Map<Integer, ? extends Product> products, int id) {
        Optional<? extends Product> product = products.values().stream()
                .filter(p -> p.getId() == id).findFirst();
        return product.isPresent() ? product.get() : null;
    }

    public static boolean skillSlotsAreFull(Session session) {
        return getSkillsInUse(session) >= PlayerInfo.getSkillSlots(session.getCache().getItems());
    }

    private static long getSkillsInUse(Session session) {
        return session.getCache().getSkills().values().stream()
                .filter(s -> s.getSelectionIndex() > 0).count();
    }
}
