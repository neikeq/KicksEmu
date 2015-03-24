package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Timestamp;

public class Item implements Product {

    private int id;
    private int inventoryId;
    private Expiration expiration;
    private int bonusOne;
    private int bonusTwo;
    private short usages;
    private Timestamp timestampExpire;
    private boolean selected;
    private boolean visible;

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
        this.setSelected(selected);
        this.visible = visible;
    }

    public void deactivateGracefully(int playerId) {
        if (isSelected()) {
            setSelected(false);
            PlayerInfo.setInventoryItem(this, playerId);
        }
    }

    public void activateGracefully(int playerId) {
        if (!isSelected()) {
            setSelected(true);
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

    public void setTimestampExpire(Timestamp timestampExpire) {
        this.timestampExpire = timestampExpire;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
