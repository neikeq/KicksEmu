package com.neikeq.kicksemu.game.servers;

import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;

// TODO make PRIVATE a flag variable instead of a constant of this enum
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

    public static ServerType fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            Output.println("Invalid server type string: " + e.getMessage(), Level.WARNING);
            return null;
        }
    }
}
