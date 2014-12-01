package com.neikeq.kicksemu.game.sessions;

public class AuthenticationResult {

    public static final byte SUCCESS = (byte)0;
    public static final byte ACCOUNT_BLOCKED = (byte)247;
    public static final byte CLIENT_VERSION = (byte)248;
    public static final byte SERVER_FULL = (byte)249;
    public static final byte AUTH_FAILURE = (byte)250;
    public static final byte ACCESS_FAILURE = (byte)251;
    public static final byte ALREADY_CONNECTED = (byte)252;
    public static final byte INVALID_PASSWORD = (byte)253;
    public static final byte ACCOUNT_NOT_FOUND = (byte)254;
    public static final byte SYSTEM_PROBLEM = (byte)255;
}