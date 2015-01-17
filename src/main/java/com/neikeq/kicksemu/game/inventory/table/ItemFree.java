package com.neikeq.kicksemu.game.inventory.table;

import com.neikeq.kicksemu.utils.table.Row;

public class ItemFree {

    private final int id;
    private final int itemClass;

    public ItemFree(Row row) {
        row.nextColumn();
        id = Integer.valueOf(row.nextColumn());
        itemClass = Integer.valueOf(row.nextColumn());
    }

    public int getId() {
        return id;
    }

    public int getItemClass() {
        return itemClass;
    }
}
