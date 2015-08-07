package com.neikeq.kicksemu.game.sessions;

public class AuthResult {

    public static final byte SUCCESS = 0;
    public static final byte ACCOUNT_BLOCKED = (byte) -9;
    public static final byte CLIENT_VERSION = (byte) -8;
    public static final byte SERVER_FULL = (byte) -7; // TODO make use of this when checking
    public static final byte AUTH_FAILURE = (byte) -6;
    public static final byte ACCESS_FAILURE = (byte) -5;
    public static final byte ALREADY_CONNECTED = (byte) -4;
    public static final byte INVALID_PASSWORD = (byte) -3;
    public static final byte ACCOUNT_NOT_FOUND = (byte) -2;
    public static final byte SYSTEM_PROBLEM = (byte) -1;
}