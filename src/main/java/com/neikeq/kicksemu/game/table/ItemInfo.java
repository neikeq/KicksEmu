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
        return (getGender() != gender) && (getGender() != Animation.ANY);
    }

    public boolean isIncompatibleLevel(short suspiciousLevel) {
        return suspiciousLevel < level;
    }

    public boolean isInvalidPaymentMode(Payment suspiciousPayment) {
        return payment.isIncompatibleWith(suspiciousPayment);
    }

    public ItemInfo(Row row) throws ParseRowException {
        try {
            row.ignoreColumn();
            id = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
            type = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
            row.ignoreColumns(2);
            gender = Animation.fromShort(Short.valueOf(row.nextColumn()
                    .orElseThrow(ParseRowException::new)));
            level = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
            row.ignoreColumns(4);
            payment = Payment.fromInt(Integer.valueOf(row.nextColumn()
                    .orElseThrow(ParseRowException::new)));
            row.ignoreColumn();
            price = new Price(row);
        } catch (NumberFormatException e) {
            throw new ParseRowException();
        }
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

    public Price getPrice() {
        return price;
    }
}
