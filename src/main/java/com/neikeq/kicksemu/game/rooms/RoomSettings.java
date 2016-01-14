package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.game.rooms.enums.RoomAccessType;
import com.neikeq.kicksemu.game.rooms.enums.RoomMode;
import com.neikeq.kicksemu.game.rooms.enums.RoomSize;

public class RoomSettings {

    public static final int MAX_ROOM_NAME_LENGTH = 30;
    public static final int MAX_ROOM_PASSWORD_LENGTH = 4;
    public static final byte MAX_ROOM_LEVEL = 60;
    public static final byte MIN_ROOM_LEVEL = 1;

    private String name = "Welcome";
    private String password = "";
    private RoomAccessType accessType = RoomAccessType.FREE;
    private RoomMode roomMode = RoomMode.AI_GOALKEEPER;
    private RoomSize maxSize = RoomSize.SIZE_4V4;
    private byte minLevel = MIN_ROOM_LEVEL;
    private byte maxLevel = MAX_ROOM_LEVEL;

    public boolean areInvalidLevelSettings() {
        return minLevel > maxLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name.length() > MAX_ROOM_NAME_LENGTH) ?
                name.substring(0, MAX_ROOM_NAME_LENGTH) : name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = (password.length() > MAX_ROOM_PASSWORD_LENGTH) ?
                password.substring(0, MAX_ROOM_PASSWORD_LENGTH) : password;
    }

    public RoomAccessType getAccessType() {
        return ((accessType == RoomAccessType.PASSWORD) && password.isEmpty()) ?
                RoomAccessType.FREE : accessType;
    }

    public void setAccessType(RoomAccessType accessType) {
        this.accessType = accessType;
    }

    public RoomMode getRoomMode() {
        return roomMode;
    }

    public void setRoomMode(RoomMode roomMode) {
        this.roomMode = roomMode;
    }

    public RoomSize getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(RoomSize maxSize) {
        this.maxSize = maxSize;
    }

    public byte getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(byte minLevel) {
        this.minLevel = (byte) Math.max(MIN_ROOM_LEVEL, minLevel);
    }

    public byte getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(byte maxLevel) {
        this.maxLevel = (byte) Math.min(MAX_ROOM_LEVEL, maxLevel);
    }
}
