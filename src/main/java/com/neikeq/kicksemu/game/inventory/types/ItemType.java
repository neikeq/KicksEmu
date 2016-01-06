package com.neikeq.kicksemu.game.inventory.types;

import java.util.Optional;

public enum ItemType {

    FACE,
    HEAD,
    GLASSES,
    SHIRTS,
    PANTS,
    GLOVES,
    SHOES,
    SOCKS,
    WRIST,
    ARM,
    KNEE,
    EAR,
    NECK,
    MASK,
    MUFFLER,
    PACKAGE,
    SODA,
    SKILL_SLOT,
    CHARACTER_SLOT,
    STATS_RESET,
    CASH_TICKET,
    POINTS_TICKET,
    CLUB_SPONSORSHIP,
    RANDOM_BOX,
    STATS_211,
    STATS_212,
    STATS_213,
    STATS_214,
    STATS_215,
    STATS_216,
    STATS_217,
    STATS_218,
    STATS_219,
    STATS_220,
    STATS_221,
    STATS_222,
    STATS_223,
    CLUB_UNIFORM,
    EMBLEM,
    BACK_NUMBER,
    CLUB_RENAME,
    MEMBERS_SLOTS,
    CLUB_BROADCAST,
    BACK_NUMBER_COLOR;

    public int toInt() {
        switch (this) {
            case FACE:
                return 100;
            case HEAD:
                return 101;
            case GLASSES:
                return 102;
            case SHIRTS:
                 return 103;
            case PANTS:
                 return 104;
            case GLOVES:
                 return 105;
            case SHOES:
                 return 106;
            case SOCKS:
                 return 107;
            case WRIST:
                 return 111;
            case ARM:
                 return 112;
            case KNEE:
                 return 113;
            case EAR:
                 return 121;
            case NECK:
                 return 122;
            case MASK:
                 return 124;
            case MUFFLER:
                 return 125;
            case PACKAGE:
                 return 126;
            case SODA:
                return 201;
            case SKILL_SLOT:
                return 202;
            case CHARACTER_SLOT:
                return 203;
            case STATS_RESET:
                return 204;
            case CASH_TICKET:
                return 205;
            case POINTS_TICKET:
                return 206;
            case CLUB_SPONSORSHIP:
                return 209;
            case RANDOM_BOX:
                return 210;
            case STATS_211:
                return 211;
            case STATS_212:
                return 212;
            case STATS_213:
                return 213;
            case STATS_214:
                return 214;
            case STATS_215:
                return 215;
            case STATS_216:
                return 216;
            case STATS_217:
                return 217;
            case STATS_218:
                return 218;
            case STATS_219:
                return 219;
            case STATS_220:
                return 220;
            case STATS_221:
                return 221;
            case STATS_222:
                return 222;
            case STATS_223:
                return 223;
            case CLUB_UNIFORM:
                return 301;
            case EMBLEM:
                return 302;
            case BACK_NUMBER:
                return 303;
            case CLUB_RENAME:
                return 304;
            case MEMBERS_SLOTS:
                return 305;
            case CLUB_BROADCAST:
                return 306;
            case BACK_NUMBER_COLOR:
                return 309;
            default:
                return -1;
        }
    }

    public static Optional<ItemType> fromInt(int code) {
        return Optional.ofNullable(nullableFromInt(code));
    }

    private static ItemType nullableFromInt(int code) {
        switch (code) {
            case 100:
                return FACE;
            case 101:
                return HEAD;
            case 102:
                return GLASSES;
            case 103:
                return SHIRTS;
            case 104:
                return PANTS;
            case 105:
                return GLOVES;
            case 106:
                return SHOES;
            case 107:
                return SOCKS;
            case 111:
                return WRIST;
            case 112:
                return ARM;
            case 113:
                return KNEE;
            case 121:
                return EAR;
            case 122:
                return NECK;
            case 124:
                return MASK;
            case 125:
                return MUFFLER;
            case 126:
                return PACKAGE;
            case 201:
                return SODA;
            case 202:
                return SKILL_SLOT;
            case 203:
                return CHARACTER_SLOT;
            case 204:
                return STATS_RESET;
            case 205:
                return CASH_TICKET;
            case 206:
                return POINTS_TICKET;
            case 209:
                return CLUB_SPONSORSHIP;
            case 210:
                return RANDOM_BOX;
            case 211:
                return STATS_211;
            case 212:
                return STATS_212;
            case 213:
                return STATS_213;
            case 214:
                return STATS_214;
            case 215:
                return STATS_215;
            case 216:
                return STATS_216;
            case 217:
                return STATS_217;
            case 218:
                return STATS_218;
            case 219:
                return STATS_219;
            case 220:
                return STATS_220;
            case 221:
                return STATS_221;
            case 222:
                return STATS_222;
            case 223:
                return STATS_223;
            case 301:
                return CLUB_UNIFORM;
            case 302:
                return EMBLEM;
            case 303:
                return BACK_NUMBER;
            case 304:
                return CLUB_RENAME;
            case 305:
                return MEMBERS_SLOTS;
            case 306:
                return CLUB_BROADCAST;
            case 309:
                return BACK_NUMBER_COLOR;
            default:
                return null;
        }
    }
}
