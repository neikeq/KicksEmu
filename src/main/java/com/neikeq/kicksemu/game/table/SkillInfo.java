package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.characters.types.Position;
import com.neikeq.kicksemu.game.inventory.Price;
import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.utils.table.Row;

public class SkillInfo {

    private final int id;
    private final short position;
    private final short level;
    private final Payment payment;
    private final Price price;

    public boolean isCompatiblePosition(short position) {
        return (getPosition() == position) || (getPosition() == Position.basePosition(position));
    }

    public boolean isIncompatibleLevel(short level) {
        return level < getLevel();
    }

    public boolean isInvalidPaymentMode(Payment payment) {
        return getPayment().isIncompatibleWith(payment);
    }

    public boolean isInvalidPrice(int price, Expiration expiration, Payment payment) {
        int skillPrice = getPrice().getPriceFor(expiration, payment);
        return (skillPrice == -1) || (skillPrice != price);
    }

    public SkillInfo(Row row) throws ParseRowException {
        row.ignoreColumn();
        id = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        position = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        row.ignoreColumn();
        level = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        payment = Payment.fromInt(Integer.valueOf(row.nextColumn()
                .orElseThrow(ParseRowException::new)));
        price = new Price(row);
    }

    public int getId() {
        return id;
    }

    public short getPosition() {
        return position;
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
