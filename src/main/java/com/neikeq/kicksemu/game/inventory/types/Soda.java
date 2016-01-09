package com.neikeq.kicksemu.game.inventory.types;

import org.apache.commons.lang3.mutable.MutableInt;

public enum Soda {

    EXP_100,
    EXP_150,
    EXP_200,
    EXP_250,
    POINTS_100,
    POINTS_150,
    POINTS_200;

    public static void applyBonus(Soda soda, int base,
                                  MutableInt experience, MutableInt points) {
        if (soda != null) {
            soda.applyBonus(base, experience, points);
        }
    }

    private void applyBonus(int base, MutableInt experience, MutableInt points) {
        if (isPointsBonus()) {
            points.add((base * getPercentage()) / 100);
        } else {
            experience.add((base * getPercentage()) / 100);
        }
    }

    private boolean isPointsBonus() {
        switch (this) {
            case POINTS_100:
            case POINTS_150:
            case POINTS_200:
                return true;
            default:
                return false;
        }
    }

    private int getPercentage() {
        switch (this) {
            case EXP_100:
                return 100;
            case EXP_150:
                return 150;
            case EXP_200:
                return 200;
            case EXP_250:
                return 250;
            case POINTS_100:
                return 100;
            case POINTS_150:
                return 150;
            case POINTS_200:
                return 200;
            default:
                return 0;
        }
    }

    public static Soda fromId(int bonusId) {
        switch (bonusId) {
            case 3101100:
                return EXP_100;
            case 3101150:
                return EXP_150;
            case 3101200:
                return EXP_200;
            case 3101250:
                return EXP_250;
            case 3201100:
                return POINTS_100;
            case 3201150:
                return POINTS_150;
            case 3201200:
                return POINTS_200;
            default:
                return null;
        }
    }
}
