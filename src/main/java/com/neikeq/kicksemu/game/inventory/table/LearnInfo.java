package com.neikeq.kicksemu.game.inventory.table;

import com.neikeq.kicksemu.game.inventory.shop.Payment;
import com.neikeq.kicksemu.utils.table.Row;

public class LearnInfo {

    private final int id;
    private final int statIndex;
    private final short level;
    private final short statPoints;
    private final Payment payment;
    private final int points;
    private final int kash;

    public LearnInfo(Row row) {
        row.nextColumn();
        id = Integer.valueOf(row.nextColumn());
        statIndex = Integer.valueOf(row.nextColumn());
        level = Short.valueOf(row.nextColumn());
        statPoints = Short.valueOf(row.nextColumn());
        payment = Payment.fromInt(Integer.valueOf(row.nextColumn()));
        kash = Integer.valueOf(row.nextColumn());
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

    public int getKash() {
        return kash;
    }
}
