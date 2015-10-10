package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;

class CelebrationRequest implements PurchaseRequest {

    private final int productId;
    private final int price;
    private final Payment payment;
    private final Expiration expiration;

    public boolean isInvalidExpirationMode() {
        return getExpiration() == null || getExpiration().isInvalidForPurchaseRequest();
    }

    public CelebrationRequest(ClientMessage msg) {
        payment = Payment.fromInt(msg.readByte());
        price = msg.readInt();
        productId = msg.readShort();
        expiration = Expiration.fromInt(msg.readInt());
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
}
