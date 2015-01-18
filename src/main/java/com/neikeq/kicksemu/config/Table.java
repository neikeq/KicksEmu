package com.neikeq.kicksemu.config;

import com.neikeq.kicksemu.game.inventory.table.InventoryTable;

public class Table {

    public static void initializeMainTables() {
        InventoryTable.initializeItemFreeTable(Constants.TABLE_ITEM_FREE_PATH);
    }

    public static void initializeGameTables() {
        InventoryTable.initializeSkillTable(Constants.TABLE_SKILL_PATH);
        InventoryTable.initializeCeleTable(Constants.TABLE_CELE_PATH);
        InventoryTable.initializeLearnTable(Constants.TABLE_LEARN_PATH);
    }
}
