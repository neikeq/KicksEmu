package com.neikeq.kicksemu.config;

import com.neikeq.kicksemu.game.inventory.table.InventoryTable;

public class Table {

    public static void initializeGameTables() {
        InventoryTable.initializeSkillsTable(Constants.TABLE_DIR + "skills.csv");
    }
}
