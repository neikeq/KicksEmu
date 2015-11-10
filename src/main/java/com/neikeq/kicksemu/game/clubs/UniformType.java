package com.neikeq.kicksemu.game.clubs;

public enum UniformType {

    NONE,
    HOME,
    AWAY;

    public byte toByte() {
        switch (this) {
            case NONE:
                return 0;
            case HOME:
                return 1;
            case AWAY:
                return 2;
            default:
                return -1;
        }
    }

    public static UniformType fromByte(int code) {
        switch (code) {
            case 1:
                return HOME;
            case 2:
                return AWAY;
            case 0:
            default:
                return NONE;
        }
    }
}
