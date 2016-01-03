package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.game.table.OptionInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;

class ItemRequest implements PurchaseRequest {

    private final int productId;
    private final int price;
    private final Payment payment;
    private final Expiration expiration;
    private final int bonusOne;
    private final int bonusTwo;

    public boolean isInvalidExpirationMode() {
        return getExpiration() == null;
    }

    public boolean hasInvalidBonus(OptionInfo one, OptionInfo two) {
        return isInvalidBonus(one, getBonusOne()) || isInvalidBonus(two, getBonusTwo());
    }

    public boolean hasIncompatibleBonusLevel(OptionInfo one, OptionInfo two, short level) {
        return isIncompatibleBonusLevel(one, getBonusOne(), level) ||
                isIncompatibleBonusLevel(two, getBonusTwo(), level);
    }

    private boolean isInvalidBonus(OptionInfo optionInfo, int bonusId) {
        return (optionInfo == null) && (bonusId != 0);
    }

    private boolean isIncompatibleBonusLevel(OptionInfo info, int bonusId, short level) {
        return (bonusId != 0) && info.isIncompatibleLevel(level, getPayment());
    }

    public ItemRequest(ClientMessage msg) {
        payment = Payment.fromInt(msg.readByte());
        price = msg.readInt();
        productId = msg.readInt();
        Expiration specifiedExpiration = Expiration.fromInt(msg.readInt());
        expiration = (specifiedExpiration == null) ? Expiration.DAYS_PERM : specifiedExpiration;
        bonusOne = msg.readInt();
        bonusTwo = msg.readInt();
    }

    @Override
    public int getProductId() {
        return productId;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public Payment getPayment() {
        return payment;
    }

    public Expiration getExpiration() {
        return expiration;
    }

    public int getBonusOne() {
        return bonusOne;
    }

    public int getBonusTwo() {
        return bonusTwo;
    }
}
