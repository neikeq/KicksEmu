package com.neikeq.kicksemu.game.sessions;

class AuthenticationException extends Exception {

    private final int errorCode;

    public AuthenticationException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
