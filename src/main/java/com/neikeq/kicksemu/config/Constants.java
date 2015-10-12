package com.neikeq.kicksemu.config;

import java.io.File;

public class Constants {
    private static final String DATA_DIR = "data" + File.separator;
    public static final String CONFIG_DIR = DATA_DIR + "config" + File.separator;
    public static final String LANG_DIR = DATA_DIR + "lang" + File.separator;
    public static final String TABLE_DIR = DATA_DIR + "table" + File.separator;
    public static final String LOGS_DIR = "logs" + File.separator;

    public static final String TABLE_SKILL_DEFAULT = TABLE_DIR + "Table_Skill_File.csv";
    public static final String TABLE_CELE_DEFAULT = TABLE_DIR + "Table_Cele_File.csv";
    public static final String TABLE_LEARN_DEFAULT = TABLE_DIR + "Table_Learn_File.csv";
    public static final String TABLE_ITEM_FREE_DEFAULT = TABLE_DIR + "Table_ItemFree_File.csv";
    public static final String TABLE_ITEM_DEFAULT = TABLE_DIR + "Table_Item_File.csv";
    public static final String TABLE_BONUS_DEFAULT = TABLE_DIR + "Table_Bonus_File.csv";
    public static final String TABLE_OPTION_DEFAULT = TABLE_DIR + "Table_Option_File.csv";
    public static final String TABLE_LEVEL_DEFAULT = TABLE_DIR + "Table_Level_File.csv";
    public static final String TABLE_MISSION_DEFAULT = TABLE_DIR + "Table_Mission_File.csv";
    public static final String TABLE_GOLDEN_TIME_DEFAULT = TABLE_DIR + "Table_Golden_Time.csv";
    public static final String TABLE_CLUB_TIME_DEFAULT = TABLE_DIR + "Table_Club_Time.csv";
    public static final String TABLE_TIPS_DEFAULT = TABLE_DIR + "table_tips_";

    public static final String PROPERTY_TABLE_SKILL = "table.skill";
    public static final String PROPERTY_TABLE_CELE = "table.cele";
    public static final String PROPERTY_TABLE_LEARN = "table.learn";
    public static final String PROPERTY_TABLE_ITEM_FREE = "table.item.free";
    public static final String PROPERTY_TABLE_ITEM = "table.item";
    public static final String PROPERTY_TABLE_BONUS = "table.bonus";
    public static final String PROPERTY_TABLE_OPTION = "table.option";
    public static final String PROPERTY_TABLE_LEVEL = "table.level";
    public static final String PROPERTY_TABLE_MISSION = "table.mission";
    public static final String PROPERTY_TABLE_GOLDEN_TIME = "table.golden.time";
    public static final String PROPERTY_TABLE_CLUB_TIME = "table.club.time";
    public static final String PROPERTY_TABLE_TIPS = "table.tips";

    public static final int SESSION_ID_INDEX = 2;
    public static final int TARGET_ID_INDEX = 6;
    public static final int BODY_SIZE_INDEX = 10;
    public static final int HEADER_SIZE = 12;
    
    public static final int REQUIRED_CLIENT_VERSION = 3071500;

    private Constants() {}
}
