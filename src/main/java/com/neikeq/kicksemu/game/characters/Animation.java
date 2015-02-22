package com.neikeq.kicksemu.game.characters;

public enum Animation {
    MALE,
    FEMALE;

    public static Animation fromShort(short value) {
        switch (value) {
            case 1:
                return MALE;
            case 2:
                return FEMALE;
            default:
                return null;
        }
    }

    public short toShort() {
        switch (this) {
            case MALE:
                return 1;
            case FEMALE:
                return 2;
            default:
                return -1;
        }
    }
}
