package com.neikeq.kicksemu.game.inventory.products;

public class DefaultClothes {

    private final int head;
    private final int shirts;
    private final int pants;
    private final int shoes;

    public DefaultClothes(int head, int shirts, int pants, int shoes) {
        this.head = head;
        this.shirts = shirts;
        this.pants = pants;
        this.shoes = shoes;
    }

    public int getHead() {
        return head;
    }

    public int getShirts() {
        return shirts;
    }

    public int getPants() {
        return pants;
    }

    public int getShoes() {
        return shoes;
    }
}
