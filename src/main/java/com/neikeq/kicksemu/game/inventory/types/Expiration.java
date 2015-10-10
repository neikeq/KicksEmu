package com.neikeq.kicksemu.game.inventory.types;

public enum Expiration {

    DAYS_7,
    DAYS_30,
    DAYS_PERM,
    USAGE_10,
    USAGE_50,
    USAGE_100;

    public boolean isInvalidForPurchaseRequest() {
        return getType() != 1;
    }

    public byte getType() {
        switch (this) {
            case DAYS_7:
            case DAYS_30:
            case DAYS_PERM:
                return 1;
            case USAGE_10:
            case USAGE_50:
            case USAGE_100:
                return 2;
            default:
                return 0;
        }
    }

    public boolean isPermanent() {
        return this == DAYS_PERM;
    }

    public boolean isUsage() {
        switch (this) {
            case USAGE_10:
            case USAGE_50:
            case USAGE_100:
                return true;
            default:
                return false;
        }
    }

    public boolean isDays() {
        switch (this) {
            case DAYS_7:
            case DAYS_30:
            case DAYS_PERM:
                return true;
            default:
                return false;
        }
    }

    public short getUsages() {
        switch (this) {
            case USAGE_10:
                return 10;
            case USAGE_50:
                return 50;
            case USAGE_100:
                return 100;
            default:
                return 0;
        }
    }

    public static Expiration fromInt(int value) {
        switch (value) {
            case 9200007:
            case 9201007:
                return DAYS_7;
            case 9200030:
            case 9201030:
                return  DAYS_30;
            case 9200999:
            case 9201999:
                return DAYS_PERM;
            case 9101010:
                return USAGE_10;
            case 9101050:
                return USAGE_50;
            case 9101100:
                return USAGE_100;
            default:
                return null;
        }
    }

    public int toInt() {
        switch (this) {
            case DAYS_7:
                return 9201007;
            case DAYS_30:
                return 9201030;
            case DAYS_PERM:
                return 9201999;
            case USAGE_10:
                return 9101010;
            case USAGE_50:
                return 9101050;
            case USAGE_100:
                return 9101100;
            default:
                return -1;
        }
    }
}
