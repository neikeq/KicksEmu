package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.inventory.Price;
import com.neikeq.kicksemu.game.inventory.Payment;
import com.neikeq.kicksemu.utils.table.Row;

public class OptionInfo {

    private final int id;
    private final int type;
    private final short value;
    private final short cashLevel;
    private final short pointsLevel;
    private final Price price;

    public boolean isValidLevel(short level, Payment payment) {
        return payment == Payment.CASH ? level >= cashLevel : level >= pointsLevel;
    }

    public OptionInfo(Row row) {
        row.nextColumn();
        id = Integer.valueOf(row.nextColumn());
        type = Integer.valueOf(row.nextColumn());
        row.nextColumn();
        value = Short.valueOf(row.nextColumn());
        cashLevel = Short.valueOf(row.nextColumn());
        pointsLevel = Short.valueOf(row.nextColumn());
        price = new Price(row);
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public short getValue() {
        return value;
    }

    public Price getPrice() {
        return price;
    }
}
