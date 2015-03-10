package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.inventory.Price;
import com.neikeq.kicksemu.game.inventory.Payment;
import com.neikeq.kicksemu.utils.table.Row;

public class ItemInfo {

    private final int id;
    private final int type;
    private final short gender;
    private final short level;
    private final Payment payment;
    private final Price price;

    public ItemInfo(Row row) {
        row.nextColumn();
        id = Integer.valueOf(row.nextColumn());
        type = Integer.valueOf(row.nextColumn());
        row.nextColumn();
        row.nextColumn();
        gender = Short.valueOf(row.nextColumn());
        level = Short.valueOf(row.nextColumn());
        row.nextColumn();
        row.nextColumn();
        row.nextColumn();
        row.nextColumn();
        payment = Payment.fromInt(Integer.valueOf(row.nextColumn()));
        row.nextColumn();
        price = new Price(row);
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public short getGender() {
        return gender;
    }

    public short getLevel() {
        return level;
    }

    public Price getPrice() {
        return price;
    }

    public Payment getPayment() {
        return payment;
    }
}
