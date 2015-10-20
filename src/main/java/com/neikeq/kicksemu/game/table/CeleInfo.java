package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.inventory.Price;
import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.utils.table.Row;

public class CeleInfo {

    private final int id;
    private final short level;
    private final Payment payment;
    private final Price price;

    public boolean isIncompatibleLevel(short level) {
        return level < getLevel();
    }

    public boolean isInvalidPaymentMode(Payment payment) {
        return getPayment().isIncompatibleWith(payment);
    }

    public boolean isInvalidPrice(int price, Expiration expiration, Payment payment) {
        int celePrice = getPrice().getPriceFor(expiration, payment);
        return celePrice == -1 || celePrice != price;
    }

    public CeleInfo(Row row) {
        row.ignoreColumn();
        id = Integer.valueOf(row.nextColumn());
        level = Short.valueOf(row.nextColumn());
        payment = Payment.fromInt(Integer.valueOf(row.nextColumn()));
        price = new Price(row);
    }

    public int getId() {
        return id;
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
