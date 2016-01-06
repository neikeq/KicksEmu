package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.game.table.OptionInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;

import java.util.Optional;

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

    public boolean hasInvalidBonus(Optional<OptionInfo> maybeOne, Optional<OptionInfo> maybeTwo) {
        return isInvalidBonus(maybeOne, getBonusOne()) || isInvalidBonus(maybeTwo, getBonusTwo());
    }

    public boolean hasIncompatibleBonusLevel(Optional<OptionInfo> maybeOne,
                                             Optional<OptionInfo> maybeTwo, short level) {
        return isIncompatibleBonusLevel(maybeOne, getBonusOne(), level) ||
                isIncompatibleBonusLevel(maybeTwo, getBonusTwo(), level);
    }

    private boolean isInvalidBonus(Optional<OptionInfo> maybeOptionInfo, int bonusId) {
        return !maybeOptionInfo.isPresent() && (bonusId != 0);
    }

    private boolean isIncompatibleBonusLevel(Optional<OptionInfo> maybeInfo,
                                             int bonusId, short level) {
        return maybeInfo
                .map(info -> (bonusId != 0) && info.isIncompatibleLevel(level, getPayment()))
                .orElse(false);
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
