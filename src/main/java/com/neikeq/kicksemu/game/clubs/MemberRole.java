package com.neikeq.kicksemu.game.clubs;

public enum MemberRole {

    MEMBER,
    CAPTAIN,
    MANAGER;

    public int toInt() {
        switch (this) {
            case MEMBER:
                return 0;
            case MANAGER:
                return 1;
            case CAPTAIN:
                return 2;
            default:
                return 0;
        }
    }
}
