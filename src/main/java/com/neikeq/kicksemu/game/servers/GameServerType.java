package com.neikeq.kicksemu.game.servers;

public enum GameServerType {
    NORMAL,
    PRACTICE,
    CLUB,
    TOURNAMENT,
    PRIVATE;

    public short toShort() {
        switch (this) {
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
