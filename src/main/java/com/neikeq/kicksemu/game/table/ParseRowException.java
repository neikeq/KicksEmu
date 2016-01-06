package com.neikeq.kicksemu.game.table;

public class ParseRowException extends Exception {

    public ParseRowException() {
        super("Error parsing table.");
    }

    public ParseRowException(String message) {
        super("Error parsing table. " + message);
    }
}
