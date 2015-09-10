package com.neikeq.kicksemu.game.sessions;

public class AuthenticationCode {

    public static final byte SUCCESS = 0;
    public static final byte ACCOUNT_BLOCKED = -9;
    public static final byte CLIENT_VERSION = -8;
    public static final byte SERVER_FULL = -7; // TODO make use of this when checking
    public static final byte AUTH_FAILURE = -6;
    public static final byte ACCESS_FAILURE = -5;
    public static final byte ALREADY_CONNECTED = -4;
    public static final byte INVALID_PASSWORD = -3;
    public static final byte ACCOUNT_NOT_FOUND = -2;
    public static final byte SYSTEM_PROBLEM = -1;
}