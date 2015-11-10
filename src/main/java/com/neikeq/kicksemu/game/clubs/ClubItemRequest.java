package com.neikeq.kicksemu.game.clubs;

import com.neikeq.kicksemu.network.packets.in.ClientMessage;

class ClubItemRequest {

    private final int price;
    private final int productId;
    private final int optionOne;
    private final int optionTwo;

    public ClubItemRequest(ClientMessage msg) {
        price = msg.readInt();
        productId = msg.readInt();
        optionOne = msg.readInt();
        optionTwo = msg.readInt();
    }

    public int getPrice() {
        return price;
    }

    public int getProductId() {
        return productId;
    }

    public int getOptionOne() {
        return optionOne;
    }

    public int getOptionTwo() {
        return optionTwo;
    }
}
