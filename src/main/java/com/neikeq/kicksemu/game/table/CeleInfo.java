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

    public boolean isIncompatibleLevel(short suspiciousLevel) {
        return suspiciousLevel < level;
    }

    public boolean isInvalidPaymentMode(Payment suspiciousPayment) {
        return payment.isIncompatibleWith(suspiciousPayment);
    }

    public boolean isInvalidPrice(int suspiciousPrice, Expiration expiration, Payment payment) {
        int celePrice = price.getPriceFor(expiration, payment);
        return (celePrice == -1) || (celePrice != suspiciousPrice);
    }

    public CeleInfo(Row row) throws ParseRowException {
        row.ignoreColumn();
        id = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        level = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        payment = Payment.fromInt(Integer.valueOf(row.nextColumn()
                .orElseThrow(ParseRowException::new)));
        price = new Price(row);
    }

    public int getId() {
        return id;
    }
}
