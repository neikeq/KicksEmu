package com.neikeq.kicksemu.game.inventory;

import java.util.HashMap;
import java.util.Map;

public class Celebration {

    private int id;
    private int inventoryId;
    private int expiration;

    private byte selectionIndex;

    private long timestampExpire;

    private boolean visible;

    public Celebration() {
        id = 0;
        inventoryId = 0;
        expiration = 0;
        selectionIndex = 0;
        timestampExpire = 0;
        visible = false;
    }

    public Celebration(String item) {
        String[] data = item.split(",");

        id = Integer.valueOf(data[0]);
        inventoryId = Integer.valueOf(data[1]);
        expiration = Integer.valueOf(data[2]);
        selectionIndex = Byte.valueOf(data[3]);
        timestampExpire = Long.valueOf(data[4]);
        visible = Boolean.valueOf(data[5]);
    }

    public static Map<Integer, Celebration> mapFromString(String str) {
        Map<Integer, Celebration> celebrations = new HashMap<>();

        if (!str.isEmpty()) {
            String[] rows = str.split(";");

            for (String row : rows) {
                Celebration celebration = new Celebration(row);
                celebrations.put(celebration.getInventoryId(), celebration);
            }
        }

        return celebrations;
    }

    public static Map<Integer, Celebration> inUseFromString(String str) {
        Map<Integer, Celebration> celebrations = new HashMap<>();

        if (!str.isEmpty()) {
            String[] rows = str.split(";");

            for (String row : rows) {
                Celebration celebration = new Celebration(row);
                celebrations.put((int)celebration.getSelectionIndex(), celebration);
            }
        }

        return celebrations;
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

    public int getExpiration() {
        return expiration;
    }

    public long getTimestampExpire() {
        return timestampExpire;
    }

    public boolean isVisible() {
        return visible;
    }
}
