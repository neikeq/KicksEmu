package com.neikeq.kicksemu.game.characters.types;

public enum Animation {

    ANY,
    MALE,
    FEMALE;

    public static Animation fromShort(short value) {
        switch (value) {
            case 0:
                return ANY;
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
            case ANY:
                return 0;
            case MALE:
                return 1;
            case FEMALE:
                return 2;
            default:
                return -1;
        }
    }
}
