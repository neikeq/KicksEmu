package com.neikeq.kicksemu.config;

import com.neikeq.kicksemu.game.table.TableManager;

public class Table {

    public static void initializeTables() {
        TableManager.initializeItemFreeTable();
        TableManager.initializeSkillTable();
        TableManager.initializeCeleTable();
        TableManager.initializeLearnTable();
        TableManager.initializeItemTable();
        TableManager.initializeBonusTable();
        TableManager.initializeOptionTable();
        TableManager.initializeLevelTable();
    }
}
