package com.neikeq.kicksemu.game.characters.creation;

public class CreationResult {
    protected static final byte SUCCESS = 0;
    protected static final byte INVALID_CHARACTER = (byte)252;
    protected static final byte CHARACTERS_LIMIT = (byte)253;
    protected static final byte NAME_ALREADY_IN_USE = (byte)254;
    protected static final byte SYSTEM_PROBLEM = (byte)255;
}
