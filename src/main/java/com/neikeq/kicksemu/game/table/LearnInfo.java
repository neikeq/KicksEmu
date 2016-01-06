package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.utils.table.Row;

public class LearnInfo {

    private final int id;
    private final int statIndex;
    private final short level;
    private final short statPoints;
    private final Payment payment;
    private final int points;
    private final int cash;

    public boolean isIncompatibleLevel(short suspiciousLevel) {
        return suspiciousLevel < level;
    }

    public boolean isInvalidPaymentMode(Payment suspiciousPayment) {
        return payment.isIncompatibleWith(suspiciousPayment);
    }

    public boolean isInvalidPrice(int price, Payment payment) {
        if (price >= 0) {
            if (payment == Payment.POINTS) {
                return points != price;
            } else if (payment == Payment.CASH) {
                return cash != price;
            }
        }
        return true;
    }

    public LearnInfo(Row row) throws ParseRowException {
        row.ignoreColumn();
        id = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        statIndex = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        level = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        statPoints = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        payment = Payment.fromInt(Integer.valueOf(row.nextColumn()
                .orElseThrow(ParseRowException::new)));
        cash = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        points = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
    }

    public int getId() {
        return id;
    }

    public int getStatIndex() {
        return statIndex;
    }

    public short getStatPoints() {
        return statPoints;
    }

    public int getPoints() {
        return points;
    }

    public int getCash() {
        return cash;
    }
}
