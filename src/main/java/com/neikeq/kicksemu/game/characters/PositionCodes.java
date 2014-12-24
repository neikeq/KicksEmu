package com.neikeq.kicksemu.game.characters;

public class PositionCodes {

    public static final short FW = 10;
    public static final short ST = 11;
    public static final short CF = 12;
    public static final short WF = 13;
    public static final short MF = 20;
    public static final short AMF = 21;
    public static final short SMF = 22;
    public static final short CMF = 23;
    public static final short DMF = 24;
    public static final short DF = 30;
    public static final short SB = 31;
    public static final short CB = 32;
    public static final short SW = 33;

    public static boolean isValidNewPosition(short currentPos, short newPos) {
        switch (currentPos) {
            case FW:
                return newPos == ST || newPos == CF || newPos == WF;
            case MF:
                return newPos == AMF || newPos == SMF || newPos == CMF || newPos == DMF;
            case DF:
                return newPos == SB || newPos == CB || newPos == SW;
            default:
                return false;
        }
    }
}
