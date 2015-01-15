package com.neikeq.kicksemu.game.inventory.table;

import com.neikeq.kicksemu.utils.table.Row;
import com.neikeq.kicksemu.utils.table.TableReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class InventoryTable {

    private static final Map<Integer, SkillInfo> skillsTable = new HashMap<>();

    public static SkillInfo getSkillInfo(Predicate<SkillInfo> filter) {
        Optional<SkillInfo> result = skillsTable.values().stream().filter(filter).findFirst();

        if (!result.isPresent()) {
            return null;
        }

        return result.get();
    }

    public static void initializeSkillsTable(String path) {
        TableReader reader = new TableReader(path);

        // Ignore first row
        reader.nextRow();

        Row line;
        while ((line = reader.nextRow()) != null) {
            SkillInfo row = new SkillInfo(line);
            skillsTable.put(row.getId(), row);
        }
    }
}
