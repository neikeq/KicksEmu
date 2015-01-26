package com.neikeq.kicksemu.game.inventory;

public enum ItemType {

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
    PACKAGE;

    public int toInt() {
        switch (this) {
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
            default:
                return -1;
        }
    }

    public static ItemType fromInt(int code) {
        switch (code) {
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
            default:
                return null;
        }
    }
}
