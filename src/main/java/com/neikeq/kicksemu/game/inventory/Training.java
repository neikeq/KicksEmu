package com.neikeq.kicksemu.game.inventory;

import java.util.HashMap;
import java.util.Map;

public class Training {

    private int id;
    private int inventoryId;
    private long timestampExpire;
    private boolean visible;

    public Training() {
        id = 0;
        inventoryId = 0;
        timestampExpire = 0;
        visible = false;
    }

    private Training(String item) {
        String[] data = item.split(",");

        id = Integer.valueOf(data[0]);
        inventoryId = Integer.valueOf(data[1]);
        timestampExpire = Long.valueOf(data[2]);
        visible = Boolean.valueOf(data[3]);
    }

    public static Map<Integer, Training> mapFromString(String str) {
        Map<Integer, Training> trainings = new HashMap<>();

        if (!str.isEmpty()) {
            String[] rows = str.split(";");

            for (String row : rows) {
                Training training = new Training(row);
                trainings.put(training.getInventoryId(), training);
            }
        }

        return trainings;
    }

    public int getId() {
        return id;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public long getTimestampExpire() {
        return timestampExpire;
    }

    public boolean isVisible() {
        return visible;
    }
}
