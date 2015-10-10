package com.neikeq.kicksemu.game.inventory.products;

public class Training implements Product {

    private final int id;
    private final int inventoryId;
    private final boolean visible;

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
