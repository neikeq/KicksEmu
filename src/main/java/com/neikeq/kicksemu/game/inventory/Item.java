package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Timestamp;

public class Item implements Product {

    private final int id;
    private final int inventoryId;
    private final Expiration expiration;
    private final int bonusOne;
    private final int bonusTwo;
    private short usages;
    private final Timestamp timestampExpire;
    private boolean selected;
    private final boolean visible;

    public Item() {
        this(0, 0, 0, 0, 0, (short)0, DateUtils.getTimestamp(), false, false);
    }

    public Item(int id, int inventoryId, int expiration, int bonusOne, int bonusTwo,
                short usages, Timestamp expire, boolean selected, boolean visible) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.expiration = Expiration.fromInt(expiration);
        this.bonusOne = bonusOne;
        this.bonusTwo = bonusTwo;
        this.usages = usages;
        this.timestampExpire = expire;
        this.selected = selected;
        this.visible = visible;
    }

    public void deactivateGracefully(int playerId) {
        if (isSelected()) {
            this.selected = false;
            PlayerInfo.setInventoryItem(this, playerId);
        }
    }

    public void activateGracefully(int playerId) {
        if (!isSelected()) {
            this.selected = true;
            PlayerInfo.setInventoryItem(this, playerId);
        }
    }

    public int getId() {
        return id;
    }

    public int getInventoryId() {
        return inventoryId;
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

    public short getUsages() {
        return usages;
    }

    public void sumUsages(short value) {
        usages += value;
    }

    public Timestamp getTimestampExpire() {
        return timestampExpire;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isVisible() {
        return visible;
    }
}
