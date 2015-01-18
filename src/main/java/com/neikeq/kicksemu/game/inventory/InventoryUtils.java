package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryUtils {

    public static Timestamp expirationToTimestamp(Expiration expiration) {
        if (expiration == Expiration.DAYS_PERM) {
            return new Timestamp(0);
        }

        int days = expiration.toInt() % 1000;

        return DateUtils.toTimestamp(DateUtils.addDays(DateUtils.getSqlDate(), days));
    }

    public static byte getSmallestMissingIndex(Collection<? extends IndexedProduct> products) {
        List<Byte> indexes = new ArrayList<>();

        indexes.addAll(products.stream().map(IndexedProduct::getSelectionIndex)
                .collect(Collectors.toList()));

        for (byte i = 1; i <= products.size() + 1; i++) {
            if (!indexes.contains(i)) {
                return i;
            }
        }

        return 1;
    }

    public static int getSmallestMissingId(Collection<? extends Product> products) {
        List<Integer> ids = new ArrayList<>();

        ids.addAll(products.stream().map(Product::getInventoryId).collect(Collectors.toList()));

        for (int i = 0; i < products.size() + 1; i++) {
            if (!ids.contains(i)) {
                return i;
            }
        }

        return 1;
    }

    public static Product getByIdFromMap(Map<Integer, ? extends Product> products, int id) {
        Optional<? extends Product> product = products.values().stream()
                .filter(p -> p.getId() == id).findFirst();
        return product.isPresent() ? product.get() : null;
    }
}
