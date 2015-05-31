package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.utils.table.Row;

public class Price {

    private final int cash7;
    private final int cash30;
    private final int cashPerm;
    private final int points7;
    private final int points30;
    private final int pointsPerm;

    public Price(Row row) {
        cash7 = Integer.valueOf(row.nextColumn());
        cash30 = Integer.valueOf(row.nextColumn());
        cashPerm = Integer.valueOf(row.nextColumn());
        points7 = Integer.valueOf(row.nextColumn());
        points30 = Integer.valueOf(row.nextColumn());
        pointsPerm = Integer.valueOf(row.nextColumn());
    }

    public int getPriceFor(Expiration expiration, Payment payment) {
        switch (expiration) {
            case USAGE_10:
            case DAYS_7:
                return payment == Payment.CASH ? cash7 : points7;
            case USAGE_50:
            case DAYS_30:
                return payment == Payment.CASH ? cash30 : points30;
            case USAGE_100:
            case DAYS_PERM:
                return payment == Payment.CASH ? cashPerm : pointsPerm;
            default:
                return -1;
        }
    }
}
