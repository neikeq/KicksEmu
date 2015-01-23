package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.inventory.table.InventoryTable;
import com.neikeq.kicksemu.game.inventory.table.OptionInfo;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

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
        this(0, 0, 0, 0, 0, (short)0, new Timestamp(0), false, false);
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

    private Item(String item) {
        String[] data = item.split(",");

        id = Integer.valueOf(data[0]);
        inventoryId = Integer.valueOf(data[1]);
        expiration = Expiration.fromInt(Integer.valueOf(data[2]));
        statsBonusOne = Integer.valueOf(data[3]);
        statsBonusTwo = Integer.valueOf(data[4]);
        remainUsages = Short.valueOf(data[5]);
        timestampExpire = new Timestamp(Long.valueOf(data[6]));
        setSelected(Boolean.valueOf(data[7]));
        visible = Boolean.valueOf(data[8]);
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

            CharacterUtils.setItemInUseByType(InventoryTable.getItemInfo(i ->
                    i.getId() == getId()).getType(), -1, playerId);
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

            CharacterUtils.setItemInUseByType(InventoryTable.getItemInfo(i ->
                            i.getId() == getId()).getType(), getInventoryId(), playerId);
        }
    }

    public static Map<Integer, Item> mapFromString(String str, int playerId) {
        Map<Integer, Item> items = new HashMap<>();

        if (!str.isEmpty()) {
            String[] rows = str.split(";");

            boolean expired = false;

            for (String row : rows) {
                if (!row.isEmpty()) {
                    Item item = new Item(row);

                    if (item.getTimestampExpire().after(DateUtils.getTimestamp()) ||
                            item.getExpiration().isPermanent()) {
                        items.put(item.getInventoryId(), item);
                    } else {
                        item.deactivateGracefully(playerId);
                        expired = true;
                    }
                }
            }

            if (expired) {
                PlayerInfo.setInventoryItems(items, playerId);
            }
        }

        return items;
    }

    public static String mapToString(Map<Integer, Item> map) {
        String items = "";

        for (Item t : map.values()) {
            items += t.getId() + "," + t.getInventoryId() + "," + t.getExpiration().toInt() + "," +
                    t.getStatsBonusOne() + "," + t.getStatsBonusTwo() + "," +
                    t.getRemainUsages() + "," + t.getTimestampExpire().getTime() + "," +
                    t.isSelected() + "," + t.isVisible() + ";";
        }

        return items;
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
