package com.neikeq.kicksemu.config;

import java.io.File;

public class Constants {
    private static final String DATA_DIR = "data" + File.separator;
    public static final String CONFIG_DIR = DATA_DIR + "config" + File.separator;
    public static final String LANG_DIR = DATA_DIR + "lang" + File.separator;
    public static final String TABLE_DIR = DATA_DIR + "table" + File.separator;
    public static final String LOGS_DIR = "logs" + File.separator;

    public static final String TABLE_SKILL_PATH = TABLE_DIR + "Table_Skill_File.csv";
    public static final String TABLE_CELE_PATH = TABLE_DIR + "Table_Cele_File.csv";
    public static final String TABLE_LEARN_PATH = TABLE_DIR + "Table_Learn_File.csv";
    public static final String TABLE_ITEM_FREE_PATH = TABLE_DIR + "Table_ItemFree_File.csv";
    public static final String TABLE_ITEM_PATH = TABLE_DIR + "Table_Item_File.csv";
    public static final String TABLE_BONUS_PATH = TABLE_DIR + "Table_Bonus_File.csv";
    public static final String TABLE_OPTION_PATH = TABLE_DIR + "Table_Option_File.csv";
    public static final String TABLE_LEVEL_FILE = TABLE_DIR + "Table_Level_File.csv";
    public static final String TABLE_MISSION_FILE = TABLE_DIR + "Table_Mission_File.csv";
    public static final String TABLE_GOLDEN_TIME = TABLE_DIR + "Table_Golden_Time.csv";
    public static final String TABLE_CLUB_TIME = TABLE_DIR + "Table_Club_Time.csv";

    public static final int SESSION_ID_INDEX = 2;
    public static final int TARGET_ID_INDEX = 6;
    public static final int BODY_SIZE_INDEX = 10;
    public static final int HEADER_SIZE = 12;
    
    public static final int REQUIRED_CLIENT_VERSION = 3071500;

    private Constants() {}
}
