package com.neikeq.kicksemu.game.inventory;

import java.util.HashMap;
import java.util.Map;

public class Item {

    private int id;
    private int inventoryId;
    private int expiration;
    private int statsBonusOne;
    private int statsBonusTwo;

    private short remainUsages;

    private long timestampExpire;

    private boolean selected;
    private boolean visible;

    public Item() {
        id = 0;
        inventoryId = 0;
        expiration = 0;
        statsBonusOne = 0;
        statsBonusTwo = 0;
        remainUsages = 0;
        timestampExpire = 0;
        selected = false;
        visible = false;
    }

    private Item(String item) {
        String[] data = item.split(",");

        id = Integer.valueOf(data[0]);
        inventoryId = Integer.valueOf(data[1]);
        expiration = Integer.valueOf(data[2]);
        statsBonusOne = Integer.valueOf(data[3]);
        statsBonusTwo = Integer.valueOf(data[4]);
        remainUsages = Short.valueOf(data[5]);
        timestampExpire = Long.valueOf(data[6]);
        selected = Boolean.valueOf(data[7]);
        visible = Boolean.valueOf(data[8]);
    }

    public static Map<Integer, Item> mapFromString(String str) {
        Map<Integer, Item> items = new HashMap<>();

        if (!str.isEmpty()) {
            String[] rows = str.split(";");

            for (String row : rows) {
                Item item = new Item(row);
                items.put(item.getInventoryId(), item);
            }
        }

        return items;
    }

    public int getId() {
        return id;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public int getExpiration() {
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

    public long getTimestampExpire() {
        return timestampExpire;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isVisible() {
        return visible;
    }
}
