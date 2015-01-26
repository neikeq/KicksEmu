package com.neikeq.kicksemu.game.inventory;

public class Training implements Product {

    private int id;
    private int inventoryId;
    private boolean visible;

    public Training() {
        this(0, 0, false);
    }

    public Training(int id, int inventoryId, boolean visible) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.visible = visible;
    }

    public int getId() {
        return id;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public boolean isVisible() {
        return visible;
    }
}
