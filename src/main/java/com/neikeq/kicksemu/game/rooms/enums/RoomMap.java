package com.neikeq.kicksemu.game.rooms.enums;

public enum RoomMap {

    A_BACK_STREET,
    A_MAIN_STREET,
    A_SLUM,
    ROOF_ARENA,
    E_PARK,
    CONSTRUCTION,
    TRAINING_CAMP,
    FACTORY,
    COUNTRY_SUMMER,
    COUNTRY_WINTER,
    RESERVOIR,
    OLD_TOWN;

    public static RoomMap fromInt(int type) {
        switch (type) {
            case 1:
                return A_BACK_STREET;
            case 2:
                return A_MAIN_STREET;
            case 3:
                return A_SLUM;
            case 4:
                return ROOF_ARENA;
            case 5:
                return E_PARK;
            case 6:
                return CONSTRUCTION;
            case 7:
                return TRAINING_CAMP;
            case 8:
                return FACTORY;
            case 9:
                return COUNTRY_SUMMER;
            case 10:
                return COUNTRY_WINTER;
            case 11:
                return RESERVOIR;
            case 12:
                return OLD_TOWN;

            default:
                return null;
        }
    }

    public int toInt() {
        switch (this) {
            case A_BACK_STREET:
                return 1;
            case A_MAIN_STREET:
                return 2;
            case A_SLUM:
                return 3;
            case ROOF_ARENA:
                return 4;
            case E_PARK:
                return 5;
            case CONSTRUCTION:
                return 6;
            case TRAINING_CAMP:
                return 7;
            case FACTORY:
                return 8;
            case COUNTRY_SUMMER:
                return 9;
            case COUNTRY_WINTER:
                return 10;
            case RESERVOIR:
                return 11;
            case OLD_TOWN:
                return 12;
            default:
                return -1;
        }
    }
}
