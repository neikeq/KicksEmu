package com.neikeq.kicksemu.game.inventory;

public enum Payment {
    KASH,
    POINTS;

    public static Payment fromInt(int value) {
        switch (value) {
            case 1:
                return KASH;
            case 2:
                return POINTS;
            default:
                return null;
        }
    }
}
