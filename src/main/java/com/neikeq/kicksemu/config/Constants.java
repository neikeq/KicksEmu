package com.neikeq.kicksemu.config;

import java.io.File;

public class Constants {
    public static final String DATA_DIR = "data" + File.separator;
    public static final String CONFIG_DIR = DATA_DIR + "config" + File.separator;
    public static final String LANG_DIR = DATA_DIR + "lang" + File.separator;
    public static final String TABLE_DIR = DATA_DIR + "table" + File.separator;
    public static final String LOGS_DIR = "logs" + File.separator;

    public static final String TABLE_SKILL_PATH = TABLE_DIR + "Table_Skill_File.csv";
    
    public static final int SESSION_ID_INDEX = 2;
    public static final int TARGET_ID_INDEX = 6;
    public static final int BODY_SIZE_INDEX = 10;
    public static final int HEADER_SIZE = 12;
    
    public static final int REQUIRED_CLIENT_VERSION = 3071500;

    private Constants() {}
}
