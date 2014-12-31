package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Timestamp;
import java.util.Calendar;

public class InventoryUtils {
    // Expiration Types
    private static final int matches = 91;
    private static final int days = 92;

    // Bonus Types
    private static final int stats = 10; // Plus the stat id
    private static final int experience = 31;
    private static final int points = 32;
    private static final int skillSlot = 33;

    public static int getExpirationId(int type, int value) {
        return type * 100000 + 1000 + value;
    }

    public static int getBonusId(int type, int value) {
        return type * 100000 + 1000 + value;
    }

    public static Timestamp expirationToTimestamp(int expiration) {
        int days = expiration % 1000;

        return DateUtils.toTimestamp(DateUtils.addDays(DateUtils.getSqlDate(), days));
    }
}
