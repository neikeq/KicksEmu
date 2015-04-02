package com.neikeq.kicksemu.game.inventory;

public class DefaultClothes {

    private int head;
    private int shirts;
    private int pants;
    private int shoes;

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
