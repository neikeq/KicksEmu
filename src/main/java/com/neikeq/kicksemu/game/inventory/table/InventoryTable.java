package com.neikeq.kicksemu.game.inventory.table;

import com.neikeq.kicksemu.utils.table.Row;
import com.neikeq.kicksemu.utils.table.TableReader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class InventoryTable {

    private static final Map<Integer, SkillInfo> skillsTable = new HashMap<>();
    private static final Map<Integer, CeleInfo> celesTable = new HashMap<>();
    private static final Map<Integer, LearnInfo> learnTable = new HashMap<>();
    private static final Map<Integer, ItemFree> itemFreeTable = new HashMap<>();

    public static SkillInfo getSkillInfo(Predicate<SkillInfo> filter) {
        Optional<SkillInfo> result = skillsTable.values().stream().filter(filter).findFirst();

        if (!result.isPresent()) {
            return null;
        }

        return result.get();
    }

    public static CeleInfo getCeleInfo(Predicate<CeleInfo> filter) {
        Optional<CeleInfo> result = celesTable.values().stream().filter(filter).findFirst();

        if (!result.isPresent()) {
            return null;
        }

        return result.get();
    }

    public static LearnInfo getLearnInfo(Predicate<LearnInfo> filter) {
        Optional<LearnInfo> result = learnTable.values().stream().filter(filter).findFirst();

        if (!result.isPresent()) {
            return null;
        }

        return result.get();
    }

    public static ItemFree getItemFree(Predicate<ItemFree> filter) {
        Optional<ItemFree> result = itemFreeTable.values().stream().filter(filter).findFirst();

        if (!result.isPresent()) {
            return null;
        }

        return result.get();
    }

    public static void initializeSkillTable(String path) {
        TableReader reader = new TableReader(path);

        Row line;
        while ((line = reader.nextRow()) != null) {
            SkillInfo row = new SkillInfo(line);
            skillsTable.put(row.getId(), row);
        }
    }

    public static void initializeCeleTable(String path) {
        TableReader reader = new TableReader(path);

        Row line;
        while ((line = reader.nextRow()) != null) {
            CeleInfo row = new CeleInfo(line);
            celesTable.put(row.getId(), row);
        }
    }

    public static void initializeLearnTable(String path) {
        TableReader reader = new TableReader(path);

        Row line;
        while ((line = reader.nextRow()) != null) {
            LearnInfo row = new LearnInfo(line);
            learnTable.put(row.getId(), row);
        }
    }

    public static void initializeItemFreeTable(String path) {
        TableReader reader = new TableReader(path);

        Row line;
        while ((line = reader.nextRow()) != null) {
            ItemFree row = new ItemFree(line);
            itemFreeTable.put(row.getId(), row);
        }
    }
}
