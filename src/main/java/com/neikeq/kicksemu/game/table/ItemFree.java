package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.inventory.types.ItemType;
import com.neikeq.kicksemu.utils.table.Row;

public class ItemFree {

    private final int id;
    private final ItemType type;

    public ItemFree(Row row) throws ParseRowException {
        row.ignoreColumn();
        id = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        type = ItemType.fromInt(
                Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new))
        ).orElseThrow(IllegalStateException::new);
    }

    public int getId() {
        return id;
    }

    public ItemType getType() {
        return type;
    }
}
