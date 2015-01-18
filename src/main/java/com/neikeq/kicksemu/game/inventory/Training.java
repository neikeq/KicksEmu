package com.neikeq.kicksemu.game.inventory;

import java.util.HashMap;
import java.util.Map;

public class Training implements Product {

    private int id;
    private int inventoryId;
    private boolean visible;

    public Training() {
        this(0, 0, false);
    }

    public Training(int id, int inventoryId, boolean visible) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.visible = visible;
    }

    private Training(String item) {
        String[] data = item.split(",");

        id = Integer.valueOf(data[0]);
        inventoryId = Integer.valueOf(data[1]);
        visible = Boolean.valueOf(data[2]);
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

    public static String mapToString(Map<Integer, Training> map) {
        String learns = "";

        for (Training t : map.values()) {
            learns += t.getId() + "," + t.getInventoryId() + "," + t.isVisible() + ";";
        }

        return learns;
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
