package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.game.table.ParseRowException;
import com.neikeq.kicksemu.utils.table.Row;

public class Price {

    private final int cash7;
    private final int cash30;
    private final int cashPerm;
    private final int points7;
    private final int points30;
    private final int pointsPerm;

    public Price(Row row) throws ParseRowException {
        cash7 = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        cash30 = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        cashPerm = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        points7 = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        points30 = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        pointsPerm = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
    }

    public int getPriceFor(Expiration expiration, Payment payment) {
        switch (expiration) {
            case USAGE_10:
            case DAYS_7:
                return (payment == Payment.CASH) ? cash7 : points7;
            case USAGE_50:
            case DAYS_30:
                return (payment == Payment.CASH) ? cash30 : points30;
            case USAGE_100:
            case DAYS_PERM:
                return (payment == Payment.CASH) ? cashPerm : pointsPerm;
            default:
                return -1;
        }
    }

    /**
     * Club items store its unique price in the points permanent row.
     * This method is used to leave that clear and avoid confusions.
     * */
    public int getClubItemPrice() {
        return pointsPerm;
    }
}
