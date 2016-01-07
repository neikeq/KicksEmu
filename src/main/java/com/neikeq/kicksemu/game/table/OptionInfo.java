package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.inventory.Price;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.utils.table.Row;

public class OptionInfo {

    private final int id;
    private final int type;
    private final short value;
    private final short cashLevel;
    private final short pointsLevel;
    private final Price price;

    public boolean isIncompatibleLevel(short level, Payment payment) {
        return (payment == Payment.CASH) ? (level < cashLevel) : (level < pointsLevel);
    }

    public OptionInfo(Row row) throws ParseRowException {
        row.ignoreColumn();
        id = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        type = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        row.ignoreColumn();
        value = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        cashLevel = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        pointsLevel = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
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
