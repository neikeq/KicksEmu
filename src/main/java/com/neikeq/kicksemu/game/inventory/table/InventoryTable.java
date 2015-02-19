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
    private static final Map<Integer, ItemInfo> itemTable = new HashMap<>();
    private static final Map<Integer, OptionInfo> optionTable = new HashMap<>();
    private static final Map<Integer, LevelExpInfo> levelExpTable = new HashMap<>();

    public static SkillInfo getSkillInfo(Predicate<SkillInfo> filter) {
        Optional<SkillInfo> result = skillsTable.values().stream().filter(filter).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    public static LevelExpInfo getLevelExp(Predicate<LevelExpInfo> filter) {
        Optional<LevelExpInfo> result = levelExpTable.values().stream().filter(filter).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    public static CeleInfo getCeleInfo(Predicate<CeleInfo> filter) {
        Optional<CeleInfo> result = celesTable.values().stream().filter(filter).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    public static LearnInfo getLearnInfo(Predicate<LearnInfo> filter) {
        Optional<LearnInfo> result = learnTable.values().stream().filter(filter).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    public static ItemFree getItemFree(Predicate<ItemFree> filter) {
        Optional<ItemFree> result = itemFreeTable.values().stream().filter(filter).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    public static ItemInfo getItemInfo(Predicate<ItemInfo> filter) {
        Optional<ItemInfo> result = itemTable.values().stream().filter(filter).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    public static OptionInfo getOptionInfo(Predicate<OptionInfo> filter) {
        Optional<OptionInfo> result = optionTable.values().stream().filter(filter).findFirst();

        return result.isPresent() ? result.get() : null;
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

    public static void initializeItemTable(String path) {
        TableReader reader = new TableReader(path);

        Row line;
        while ((line = reader.nextRow()) != null) {
            ItemInfo row = new ItemInfo(line);
            itemTable.put(row.getId(), row);
        }
    }

    public static void initializeOptionTable(String path) {
        TableReader reader = new TableReader(path);

        Row line;
        while ((line = reader.nextRow()) != null) {
            OptionInfo row = new OptionInfo(line);
            optionTable.put(row.getId(), row);
        }
    }

    public static void initializeLevelExpTable(String path) {
        TableReader reader = new TableReader(path);

        Row line;
        while ((line = reader.nextRow()) != null) {
            LevelExpInfo row = new LevelExpInfo(line);
            levelExpTable.put(row.getLvl(), row);
        }
    }
}
