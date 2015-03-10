package com.neikeq.kicksemu.game.inventory;

public enum Payment {
    KASH,
    POINTS,
    BOTH;

    public static Payment fromInt(int value) {
        switch (value) {
            case 1:
                return KASH;
            case 2:
                return POINTS;
            case 3:
                return BOTH;
            default:
                return null;
        }
    }

    public boolean accepts(Payment payment) {
        if (payment == BOTH) return false;

        switch (this) {
            case BOTH:
                return payment != null;
            default:
                return payment == this;
        }
    }
}
