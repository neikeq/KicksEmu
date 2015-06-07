package com.neikeq.kicksemu.game.characters.creation;

class CreationResult {

    static final byte SUCCESS = 0;
    static final byte INVALID_CHARACTER = (byte) 252;
    static final byte CHARACTERS_LIMIT = (byte) 253;
    static final byte NAME_ALREADY_IN_USE = (byte) 254;
    static final byte SYSTEM_PROBLEM = (byte) 255;
}
