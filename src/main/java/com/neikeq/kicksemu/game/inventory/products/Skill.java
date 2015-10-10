package com.neikeq.kicksemu.game.inventory.products;

import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Timestamp;

public class Skill implements Product, IndexedProduct {

    private final int id;
    private final int inventoryId;
    private final Expiration expiration;
    private byte selectionIndex;
    private final Timestamp timestampExpire;
    private final boolean visible;

    public Skill() {
        this(0, 0, 0, (byte) 0, DateUtils.getTimestamp(), false);
    }

    public Skill(int id, int inventoryId, int expiration,
                 byte selectionIndex, Timestamp timestampExpire, boolean visible) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.expiration = Expiration.fromInt(expiration);
        this.setSelectionIndex(selectionIndex);
        this.timestampExpire = timestampExpire;
        this.visible = visible;
    }

    public int getId() {
        return id;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public byte getSelectionIndex() {
        return selectionIndex;
    }

    public Expiration getExpiration() {
        return expiration;
    }

    public Timestamp getTimestampExpire() {
        return timestampExpire;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setSelectionIndex(byte selectionIndex) {
        this.selectionIndex = selectionIndex;
    }
}
