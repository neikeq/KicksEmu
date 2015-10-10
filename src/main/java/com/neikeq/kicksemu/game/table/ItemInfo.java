package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.characters.types.Animation;
import com.neikeq.kicksemu.game.inventory.Price;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.utils.table.Row;

public class ItemInfo {

    private final int id;
    private final int type;
    private final Animation gender;
    private final short level;
    private final Payment payment;
    private final Price price;

    public boolean isIncompatibleGender(Animation gender) {
        return getGender() != gender && getGender() != Animation.ANY;
    }

    public boolean isIncompatibleLevel(short level) {
        return level < getLevel();
    }

    public boolean isInvalidPaymentMode(Payment payment) {
        return getPayment().isIncompatibleWith(payment);
    }

    public ItemInfo(Row row) {
        row.ignoreColumn();
        id = Integer.valueOf(row.nextColumn());
        type = Integer.valueOf(row.nextColumn());
        row.ignoreColumns(2);
        gender = Animation.fromShort(Short.valueOf(row.nextColumn()));
        level = Short.valueOf(row.nextColumn());
        row.ignoreColumns(4);
        payment = Payment.fromInt(Integer.valueOf(row.nextColumn()));
        row.ignoreColumn();
        price = new Price(row);
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public Animation getGender() {
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
