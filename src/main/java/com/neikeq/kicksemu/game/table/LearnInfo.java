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

    public LearnInfo(Row row) {
        row.ignoreColumn();
        id = Integer.valueOf(row.nextColumn());
        statIndex = Integer.valueOf(row.nextColumn());
        level = Short.valueOf(row.nextColumn());
        statPoints = Short.valueOf(row.nextColumn());
        payment = Payment.fromInt(Integer.valueOf(row.nextColumn()));
        cash = Integer.valueOf(row.nextColumn());
        points = Integer.valueOf(row.nextColumn());
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
