package com.neikeq.kicksemu.network.packets.in;

public class MessageException extends Exception {

    private final int errorCode;

    public MessageException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
