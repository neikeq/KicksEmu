package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Skill {

    private int id;
    private int inventoryId;
    private Expiration expiration;
    private byte selectionIndex;
    private Timestamp timestampExpire;
    private boolean visible;

    public Skill() {
        this(0, 0, 0, (byte)0, new Timestamp(0), false);
    }

    public Skill(int id, int inventoryId, int expiration,
                 byte selectionIndex, Timestamp timestampExpire, boolean visible) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.expiration = Expiration.fromInt(expiration);
        this.setSelectionIndex(selectionIndex);
        this.setTimestampExpire(timestampExpire);
        this.visible = visible;
    }

    private Skill(String item) {
        String[] data = item.split(",");

        id = Integer.valueOf(data[0]);
        inventoryId = Integer.valueOf(data[1]);
        setSelectionIndex(Byte.valueOf(data[2]));
        expiration = Expiration.fromInt(Integer.valueOf(data[3]));
        setTimestampExpire(Long.valueOf(data[4]));
        visible = Boolean.valueOf(data[5]);
    }

    public static Map<Integer, Skill> mapFromString(String str, int playerId) {
        Map<Integer, Skill> skills = new HashMap<>();

        if (!str.isEmpty()) {
            boolean expired = false;

            String[] rows = str.split(";");

            for (String row : rows) {
                if (!row.isEmpty()) {
                    Skill skill = new Skill(row);

                    if (skill.getTimestampExpire().after(DateUtils.getTimestamp()) &&
                            !skill.getExpiration().isPermanent()) {
                        skills.put(skill.getInventoryId(), skill);
                    } else {
                        expired = true;
                    }
                }
            }

            if (expired) {
                PlayerInfo.setInventorySkills(skills, playerId);
            }
        }

        return skills;
    }

    public static String mapToString(Map<Integer, Skill> map) {
        String skills = "";

        for (Skill s : map.values()) {
            skills += s.getId() + "," + s.getInventoryId() + "," + s.getSelectionIndex() + "," +
                    s.getExpiration().toInt() + "," + s.getTimestampExpire().getTime() +
                    "," + s.isVisible() + ";";
        }

        return skills;
    }

    public static Map<Integer, Skill> getSkillsInUseFromString(String str) {
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

    public void setTimestampExpire(Timestamp timestampExpire) {
        this.timestampExpire = timestampExpire;
    }

    public void setTimestampExpire(long timestampExpire) {
        this.timestampExpire = new Timestamp(timestampExpire);
    }
}
