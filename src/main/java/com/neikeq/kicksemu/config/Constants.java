package com.neikeq.kicksemu.config;

import java.io.File;

public class Constants {
    public static final String DATA_DIR = "data" + File.separator;
    public static final String LANG_DIR = "lang" + File.separator;
    public static final String LOGS_DIR = "logs" + File.separator;
    
    public static final int SESSION_ID_INDEX = 2;
    public static final int TARGET_ID_INDEX = 6;
    public static final int BODY_SIZE_INDEX = 10;
    public static final int HEADER_SIZE = 12;
    
    public static final int REQUIRED_CLIENT_VERSION = 3071500;

    private Constants() {}
}
