package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.game.rooms.enums.RoomSize;

public class ClubRoomSettings extends RoomSettings {

    public static final int MAX_ROOM_NAME_LENGTH = 14;
    public static final byte MIN_ROOM_LEVEL = 5;

    public RoomSize getMaxSize() {
        return RoomSize.SIZE_2V2;
    }

    public byte getMinLevel() {
        return MIN_ROOM_LEVEL;
    }

    public byte getMaxLevel() {
        return MAX_ROOM_LEVEL;
    }
}
