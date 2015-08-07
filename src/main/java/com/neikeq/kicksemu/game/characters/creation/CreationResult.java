package com.neikeq.kicksemu.game.characters.creation;

class CreationResult {

    static final byte SUCCESS = 0;
    static final byte INVALID_CHARACTER = (byte) -4;
    static final byte CHARACTERS_LIMIT = (byte) -3;
    static final byte NAME_ALREADY_IN_USE = (byte) -2;
    static final byte SYSTEM_PROBLEM = (byte) -1;
}
