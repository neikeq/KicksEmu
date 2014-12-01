package com.neikeq.kicksemu.io.logging;

/** Logging level enumeration to filter output. */
public enum Level {
    DEBUG,
    INFO,
    WARNING,
    CRITICAL;
    
    /**
     * Returns an {@code Verbosity} enumeration object holding the value of the specified {@code Integer}.
     * 
     * @param value the {@code Integer} to be parsed.
     */
    public static Level fromInt(int value) {
        switch (value) {
            case 0:
                return DEBUG;
            case 1:
                return INFO;
            case 2:
                return WARNING;
            case 3:
                return CRITICAL;
            default:
                return null;
        }
    }

    /** Returns the {@code Integer} representing this enumeration object. */
    public int toInt() {
        switch (this) {
            case DEBUG:
                return 0;
            case INFO:
                return 1;
            case WARNING:
                return 2;
            case CRITICAL:
                return 3;
            default:
                return -1;
        }
    }

    /**
     * Returns an {@code Verbosity} enumeration object holding the value of the specified {@code String}.
     * 
     * @param value the {@code String} to be parsed.
     */
    public static Level fromString(String value) {
        return fromInt(Integer.valueOf(value));
    }
}
