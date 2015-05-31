package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.inventory.Price;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.utils.table.Row;

public class CeleInfo {

    private final int id;
    private final short level;
    private final Payment payment;
    private final Price price;

    public CeleInfo(Row row) {
        row.nextColumn();
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
