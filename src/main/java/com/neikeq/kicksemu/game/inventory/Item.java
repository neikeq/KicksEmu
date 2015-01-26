package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.inventory.table.InventoryTable;
import com.neikeq.kicksemu.game.inventory.table.OptionInfo;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Timestamp;

public class Item implements Product {

    private int id;
    private int inventoryId;
    private Expiration expiration;
    private int statsBonusOne;
    private int statsBonusTwo;
    private short remainUsages;
    private Timestamp timestampExpire;
    private boolean selected;
    private boolean visible;

    public Item() {
        this(0, 0, 0, 0, 0, (short)0, DateUtils.getTimestamp(), false, false);
    }

    public Item(int id, int inventoryId, int expiration, int statsBonusOne, int statsBonusTwo,
                short remainUsages, Timestamp expire, boolean selected, boolean visible) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.expiration = Expiration.fromInt(expiration);
        this.statsBonusOne = statsBonusOne;
        this.statsBonusTwo = statsBonusTwo;
        this.remainUsages = remainUsages;
        this.timestampExpire = expire;
        this.setSelected(selected);
        this.visible = visible;
    }

    public void deactivateGracefully(int playerId) {
        if (isSelected()) {
            OptionInfo optionInfo = InventoryTable.getOptionInfo(c ->
                    c.getId() == getStatsBonusOne());

            if (optionInfo != null) {
                CharacterUtils.setBonusStatsByIndex(optionInfo.getType() - 10,
                        (short) -optionInfo.getValue(), playerId);
            }

            OptionInfo optionInfoTwo = InventoryTable.getOptionInfo(c ->
                    c.getId() == getStatsBonusTwo());

            if (optionInfoTwo != null) {
                CharacterUtils.setBonusStatsByIndex(optionInfoTwo.getType() - 10,
                        (short) -optionInfoTwo.getValue(), playerId);
            }

            setSelected(false);
            PlayerInfo.setInventoryItem(this, playerId);
        }
    }

    public void activateGracefully(int playerId) {
        if (!isSelected()) {
            OptionInfo optionInfoOne = InventoryTable.getOptionInfo(c ->
                    c.getId() == getStatsBonusOne());
            OptionInfo optionInfoTwo = InventoryTable.getOptionInfo(c ->
                    c.getId() == getStatsBonusTwo());

            if (optionInfoOne != null) {
                CharacterUtils.setBonusStatsByIndex(optionInfoOne.getType() - 10,
                        optionInfoOne.getValue(), playerId);
            }

            if (optionInfoTwo != null) {
                CharacterUtils.setBonusStatsByIndex(optionInfoTwo.getType() - 10,
                        optionInfoTwo.getValue(), playerId);
            }

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

    public int getStatsBonusOne() {
        return statsBonusOne;
    }

    public int getStatsBonusTwo() {
        return statsBonusTwo;
    }

    public short getRemainUsages() {
        return remainUsages;
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

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
