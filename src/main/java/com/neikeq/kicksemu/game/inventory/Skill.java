package com.neikeq.kicksemu.game.inventory;

import java.util.HashMap;
import java.util.Map;

public class Skill {

    private int id;
    private int inventoryId;
    private int expiration;

    private byte selectionIndex;

    private long timestampExpire;

    private boolean visible;

    public Skill() {
        id = 0;
        inventoryId = 0;
        selectionIndex = 0;
        expiration = 0;
        timestampExpire = 0;
        visible = false;
    }

    private Skill(String item) {
        String[] data = item.split(",");

        id = Integer.valueOf(data[0]);
        inventoryId = Integer.valueOf(data[1]);
        selectionIndex = Byte.valueOf(data[2]);
        expiration = Integer.valueOf(data[3]);
        timestampExpire = Long.valueOf(data[4]);
        visible = Boolean.valueOf(data[5]);
    }

    public static Map<Integer, Skill> mapFromString(String str) {
        Map<Integer, Skill> skills = new HashMap<>();

        if (!str.isEmpty()) {
            String[] rows = str.split(";");

            for (String row : rows) {
                Skill skill = new Skill(row);
                skills.put(skill.getInventoryId(), skill);
            }
        }

        return skills;
    }

    public static Map<Integer, Skill> inUseFromString(String str) {
        Map<Integer, Skill> skills = new HashMap<>();

        if (!str.isEmpty()) {
            String[] rows = str.split(";");

            for (String row : rows) {
                Skill skill = new Skill(row);
                skills.put((int)skill.getSelectionIndex(), skill);
            }
        }

        return skills;
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
