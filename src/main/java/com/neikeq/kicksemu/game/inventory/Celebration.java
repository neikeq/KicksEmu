package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Celebration implements Product, IndexedProduct {

    private int id;
    private int inventoryId;
    private Expiration expiration;
    private byte selectionIndex;
    private Timestamp timestampExpire;
    private boolean visible;

    public Celebration() {
        this(0, 0, 0, (byte)0, new Timestamp(0), false);
    }

    public Celebration(int id, int inventoryId, int expiration, byte selectionIndex,
                       Timestamp timestamp, boolean visible) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.expiration = Expiration.fromInt(expiration);
        this.setSelectionIndex(selectionIndex);
        this.timestampExpire = timestamp;
        this.visible = visible;
    }

    private Celebration(String item) {
        String[] data = item.split(",");

        id = Integer.valueOf(data[0]);
        inventoryId = Integer.valueOf(data[1]);
        setSelectionIndex(Byte.valueOf(data[2]));
        expiration = Expiration.fromInt(Integer.valueOf(data[3]));
        timestampExpire = new Timestamp(Long.valueOf(data[4]));
        visible = Boolean.valueOf(data[5]);
    }

    public static Map<Integer, Celebration> mapFromString(String str, int playerId) {
        Map<Integer, Celebration> celebrations = new HashMap<>();

        if (!str.isEmpty()) {
            String[] rows = str.split(";");

            boolean expired = false;

            for (String row : rows) {
                if (!row.isEmpty()) {
                    Celebration celebration = new Celebration(row);

                    if (celebration.getTimestampExpire().after(DateUtils.getTimestamp()) ||
                            celebration.getExpiration().isPermanent()) {
                        celebrations.put(celebration.getInventoryId(), celebration);
                    } else {
                        expired = true;
                    }
                }
            }

            if (expired) {
                PlayerInfo.setInventoryCelebration(celebrations, playerId);
            }
        }

        return celebrations;
    }

    public static String mapToString(Map<Integer, Celebration> map) {
        String celes = "";

        for (Celebration c : map.values()) {
            celes += c.getId() + "," + c.getInventoryId() + "," + c.getSelectionIndex() + "," +
                    c.getExpiration().toInt() + "," + c.getTimestampExpire().getTime() +
                    "," + c.isVisible() + ";";
        }

        return celes;
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
