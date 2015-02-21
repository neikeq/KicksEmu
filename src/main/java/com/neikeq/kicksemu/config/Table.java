package com.neikeq.kicksemu.config;

import com.neikeq.kicksemu.game.table.TableManager;

public class Table {

    public static void initializeTables() {
        TableManager.initializeItemFreeTable(Constants.TABLE_ITEM_FREE_PATH);
        TableManager.initializeSkillTable(Constants.TABLE_SKILL_PATH);
        TableManager.initializeCeleTable(Constants.TABLE_CELE_PATH);
        TableManager.initializeLearnTable(Constants.TABLE_LEARN_PATH);
        TableManager.initializeItemTable(Constants.TABLE_ITEM_PATH);
        TableManager.initializeOptionTable(Constants.TABLE_OPTION_PATH);
        TableManager.initializeLevelTable(Constants.TABLE_LEVEL_FILE);
    }
}
