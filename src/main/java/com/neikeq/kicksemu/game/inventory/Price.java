package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.utils.table.Row;

public class Price {

    private int kash7;
    private int kash30;
    private int kashPerm;
    private int points7;
    private int points30;
    private int pointsPerm;

    public Price(Row row) {
        kash7 = Integer.valueOf(row.nextColumn());
        kash30 = Integer.valueOf(row.nextColumn());
        kashPerm = Integer.valueOf(row.nextColumn());
        points7 = Integer.valueOf(row.nextColumn());
        points30 = Integer.valueOf(row.nextColumn());
        pointsPerm = Integer.valueOf(row.nextColumn());
    }

    public int getPriceFor(Expiration expiration, Payment payment) {
        switch (expiration) {
            case DAYS_7:
                return payment == Payment.KASH ? kash7 : points7;
            case DAYS_30:
                return payment == Payment.KASH ? kash30 : points30;
            case DAYS_PERM:
                return payment == Payment.KASH ? kashPerm : pointsPerm;
            default:
                return -1;
        }
    }
}
