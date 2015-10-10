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

    public boolean isIncompatibleLevel(short level) {
        return level < getLevel();
    }

    public boolean isInvalidPaymentMode(Payment payment) {
        return getPayment().isIncompatibleWith(payment);
    }

    public boolean isInvalidPrice(int price) {
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

    public short getLevel() {
        return level;
    }

    public short getStatPoints() {
        return statPoints;
    }

    public Payment getPayment() {
        return payment;
    }

    public int getPoints() {
        return points;
    }

    public int getCash() {
        return cash;
    }
}
