package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.inventory.types.Payment;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;

class LearnRequest implements PurchaseRequest {

    private final int productId;
    private final int price;
    private final Payment payment;

    public LearnRequest(ClientMessage msg) {
        payment = Payment.fromInt(msg.readByte());
        price = msg.readInt();
        productId = msg.readInt();
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
}
