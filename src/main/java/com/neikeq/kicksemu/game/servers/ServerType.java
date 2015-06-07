package com.neikeq.kicksemu.game.servers;

public enum ServerType {
    MAIN,
    NORMAL,
    PRACTICE,
    CLUB,
    TOURNAMENT,
    PRIVATE;

    public short toShort() {
        switch (this) {
            case MAIN:
                return 0;
            case NORMAL:
                return 1;
            case PRACTICE:
                return 8;
            case CLUB:
                return 769;
            case TOURNAMENT:
                return 1025;
            case PRIVATE:
            default:
                return -1;
        }
    }
}
