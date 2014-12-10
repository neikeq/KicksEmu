package com.neikeq.kicksemu.game.chat;

public enum ChatMessageType {
    NORMAL,
    TEAM,
    MODERATOR,
    WHISPER_FROM,
    WHISPER_TO,
    CLUB,
    INVALID_PLAYER,
    SERVER_NOTICE,
    SERVER_MESSAGE,
    TOURNAMENT_START,
    TOURNAMENT_WINNER,
    TOURNAMENT_MATCH_MESSAGE,
    TOURNAMENT_MATCH_WINNER_DEFAULT,
    CANNOT_SELF_WHISPER,;

    public static ChatMessageType fromInt(int value) {
        switch (value) {
            case 0:
                return NORMAL;
            case 1:
                return TEAM;
            case 4:
                return MODERATOR;
            case 5:
                return WHISPER_FROM;
            case 6:
                return WHISPER_TO;
            case 7:
                return CLUB;
            case 8:
                return INVALID_PLAYER;
            case 9:
                return SERVER_NOTICE;
            case 10:
            case 15:
                return SERVER_MESSAGE;
            case 11:
                return TOURNAMENT_START;
            case 12:
                return TOURNAMENT_WINNER;
            case 13:
                return TOURNAMENT_MATCH_MESSAGE;
            case 14:
                return TOURNAMENT_MATCH_WINNER_DEFAULT;
            case 250:
                return CANNOT_SELF_WHISPER;
            default:
                return null;
        }
    }

    public int toInt() {
        switch (this) {
            case NORMAL:
                return 0;
            case TEAM:
                return 1;
            case MODERATOR:
                return 4;
            case WHISPER_FROM:
                return 5;
            case WHISPER_TO:
                return 6;
            case CLUB:
                return 7;
            case INVALID_PLAYER:
                return 8;
            case SERVER_NOTICE:
                return 9;
            case SERVER_MESSAGE:
                return 10;
            case TOURNAMENT_START:
                return 11;
            case TOURNAMENT_WINNER:
                return 12;
            case TOURNAMENT_MATCH_MESSAGE:
                return 13;
            case TOURNAMENT_MATCH_WINNER_DEFAULT:
                return 14;
            case CANNOT_SELF_WHISPER:
                return 250;
            default:
                return 0;
        }
    }
}
