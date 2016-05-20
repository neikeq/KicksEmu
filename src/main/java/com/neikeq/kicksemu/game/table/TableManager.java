package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.config.Constants;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.utils.SeasonRange;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.utils.table.Row;
import com.neikeq.kicksemu.utils.table.TableReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TableManager {

    public static int EXPERIENCE_LIMIT;
    public static byte LEVEL_LIMIT;

    private static final Map<String, String> tables = new HashMap<>();

    private static final Map<Integer, SkillInfo> skillsTable = new HashMap<>();
    private static final Map<Integer, CeleInfo> celebrationsTable = new HashMap<>();
    private static final Map<Integer, LearnInfo> learnTable = new HashMap<>();
    private static final Map<Integer, ItemFree> itemFreeTable = new HashMap<>();
    private static final Map<Integer, ItemInfo> itemTable = new HashMap<>();
    private static final Map<Integer, BonusInfo> bonusTable = new HashMap<>();
    private static final Map<Integer, OptionInfo> optionTable = new HashMap<>();
    private static final Map<Short, LevelInfo> levelTable = new HashMap<>();
    private static final Map<Short, MissionInfo> missionTable = new HashMap<>();

    private static final List<Short> missionsList = new ArrayList<>();

    public static void initialize() {
        tables.put(Constants.PROPERTY_TABLE_SKILL, Constants.TABLE_SKILL_DEFAULT);
        tables.put(Constants.PROPERTY_TABLE_CELE, Constants.TABLE_CELE_DEFAULT);
        tables.put(Constants.PROPERTY_TABLE_LEARN, Constants.TABLE_LEARN_DEFAULT);
        tables.put(Constants.PROPERTY_TABLE_ITEM_FREE, Constants.TABLE_ITEM_FREE_DEFAULT);
        tables.put(Constants.PROPERTY_TABLE_ITEM, Constants.TABLE_ITEM_DEFAULT);
        tables.put(Constants.PROPERTY_TABLE_BONUS, Constants.TABLE_BONUS_DEFAULT);
        tables.put(Constants.PROPERTY_TABLE_OPTION, Constants.TABLE_OPTION_DEFAULT);
        tables.put(Constants.PROPERTY_TABLE_LEVEL, Constants.TABLE_LEVEL_DEFAULT);
        tables.put(Constants.PROPERTY_TABLE_MISSION, Constants.TABLE_MISSION_DEFAULT);
        tables.put(Constants.PROPERTY_TABLE_GOLDEN_TIME, Constants.TABLE_GOLDEN_TIME_DEFAULT);
        tables.put(Constants.PROPERTY_TABLE_CLUB_TIME, Constants.TABLE_CLUB_TIME_DEFAULT);
        tables.put(Constants.PROPERTY_TABLE_TIPS, Constants.TABLE_TIPS_DEFAULT);

        updateOverriddenTables();

        initializeItemFreeTable();
        initializeSkillTable();
        initializeCeleTable();
        initializeLearnTable();
        initializeItemTable();
        initializeBonusTable();
        initializeOptionTable();
        initializeLevelTable();
        initializeMissionTable();

        // After table initialization
        missionsList.addAll(missionTable.keySet().stream().collect(Collectors.toList()));

        LevelInfo lastLevel = levelTable.get(Collections.max(levelTable.keySet()));
        EXPERIENCE_LIMIT = lastLevel.getExperience() + lastLevel.getExperienceGap();
        LEVEL_LIMIT = (byte) lastLevel.getLevel();
    }

    public static Optional<SkillInfo> getSkillInfo(Predicate<SkillInfo> filter) {
        return skillsTable.values().stream().filter(filter).findFirst();
    }

    public static Optional<LevelInfo> getLevelInfo(Predicate<LevelInfo> filter) {
        return levelTable.values().stream().filter(filter).reduce((previous, current) -> current);
    }

    public static Optional<CeleInfo> getCeleInfo(Predicate<CeleInfo> filter) {
        return celebrationsTable.values().stream().filter(filter).findFirst();
    }

    public static Optional<LearnInfo> getLearnInfo(Predicate<LearnInfo> filter) {
        return learnTable.values().stream().filter(filter).findFirst();
    }

    public static Optional<ItemFree> getItemFree(Predicate<ItemFree> filter) {
        return itemFreeTable.values().stream().filter(filter).findFirst();
    }

    public static Optional<ItemInfo> getItemInfo(Predicate<ItemInfo> filter) {
        return itemTable.values().stream().filter(filter).findFirst();
    }

    public static Optional<BonusInfo> getBonusInfo(Predicate<BonusInfo> filter) {
        return bonusTable.values().stream().filter(filter).findFirst();
    }

    public static Optional<OptionInfo> getOptionInfo(Predicate<OptionInfo> filter) {
        return optionTable.values().stream().filter(filter).findFirst();
    }

    public static Optional<MissionInfo> getMissionInfo(Predicate<MissionInfo> filter) {
        return missionTable.values().stream().filter(filter).findFirst();
    }

    private static void initializeSkillTable() {
        TableReader reader = new TableReader(getTablePath(Constants.PROPERTY_TABLE_SKILL));

        Optional<Row> maybeRow;
        while ((maybeRow = reader.nextRow()).isPresent()) {
            try {
                SkillInfo element = new SkillInfo(maybeRow.get());
                skillsTable.put(element.getId(), element);
            } catch (ParseRowException e) {
                printParseWarning(e, reader.getTablePath(), maybeRow.get(), reader.getIndex());
            }
        }
    }

    private static void initializeCeleTable() {
        TableReader reader = new TableReader(getTablePath(Constants.PROPERTY_TABLE_CELE));

        Optional<Row> maybeRow;
        while ((maybeRow = reader.nextRow()).isPresent()) {
            try {
                CeleInfo element = new CeleInfo(maybeRow.get());
                celebrationsTable.put(element.getId(), element);
            } catch (ParseRowException e) {
                printParseWarning(e, reader.getTablePath(), maybeRow.get(), reader.getIndex());
            }
        }
    }

    private static void initializeLearnTable() {
        TableReader reader = new TableReader(getTablePath(Constants.PROPERTY_TABLE_LEARN));

        Optional<Row> maybeRow;
        while ((maybeRow = reader.nextRow()).isPresent()) {
            try {
                LearnInfo element = new LearnInfo(maybeRow.get());
                learnTable.put(element.getId(), element);
            } catch (ParseRowException e) {
                printParseWarning(e, reader.getTablePath(), maybeRow.get(), reader.getIndex());
            }
        }
    }

    private static void initializeItemFreeTable() {
        TableReader reader = new TableReader(getTablePath(Constants.PROPERTY_TABLE_ITEM_FREE));

        Optional<Row> maybeRow;
        while ((maybeRow = reader.nextRow()).isPresent()) {
            try {
                ItemFree element = new ItemFree(maybeRow.get());
                itemFreeTable.put(element.getId(), element);
            } catch (ParseRowException e) {
                printParseWarning(e, reader.getTablePath(), maybeRow.get(), reader.getIndex());
            }
        }
    }

    private static void initializeItemTable() {
        TableReader reader = new TableReader(getTablePath(Constants.PROPERTY_TABLE_ITEM));

        Optional<Row> maybeRow;
        while ((maybeRow = reader.nextRow()).isPresent()) {
            try {
                ItemInfo element = new ItemInfo(maybeRow.get());
                itemTable.put(element.getId(), element);
            } catch (ParseRowException e) {
                printParseWarning(e, reader.getTablePath(), maybeRow.get(), reader.getIndex());
            }
        }
    }

    private static void initializeBonusTable() {
        TableReader reader = new TableReader(getTablePath(Constants.PROPERTY_TABLE_BONUS));

        Optional<Row> maybeRow;
        while ((maybeRow = reader.nextRow()).isPresent()) {
            try {
                BonusInfo element = new BonusInfo(maybeRow.get());
                bonusTable.put(element.getType(), element);
            } catch (ParseRowException e) {
                printParseWarning(e, reader.getTablePath(), maybeRow.get(), reader.getIndex());
            }
        }
    }

    private static void initializeOptionTable() {
        TableReader reader = new TableReader(getTablePath(Constants.PROPERTY_TABLE_OPTION));

        Optional<Row> maybeRow;
        while ((maybeRow = reader.nextRow()).isPresent()) {
            try {
                OptionInfo element = new OptionInfo(maybeRow.get());
                optionTable.put(element.getId(), element);
            } catch (ParseRowException e) {
                printParseWarning(e, reader.getTablePath(), maybeRow.get(), reader.getIndex());
            }
        }
    }

    private static void initializeLevelTable() {
        TableReader reader = new TableReader(getTablePath(Constants.PROPERTY_TABLE_LEVEL));

        Optional<Row> maybeRow;
        while ((maybeRow = reader.nextRow()).isPresent()) {
            try {
                LevelInfo element = new LevelInfo(maybeRow.get());
                levelTable.put(element.getLevel(), element);
            } catch (ParseRowException e) {
                printParseWarning(e, reader.getTablePath(), maybeRow.get(), reader.getIndex());
            }
        }
    }

    private static void initializeMissionTable() {
        TableReader reader = new TableReader(getTablePath(Constants.PROPERTY_TABLE_MISSION));

        Optional<Row> maybeRow;
        while ((maybeRow = reader.nextRow()).isPresent()) {
            Row row = maybeRow.get();
            try {
                boolean enabled = Integer.valueOf(row.columnAt(5)
                        .orElseThrow(ParseRowException::new)) == 1;
                if (enabled) {
                    MissionInfo element = new MissionInfo(row);
                    missionTable.put(element.getId(), element);
                }
            } catch (ParseRowException e) {
                printParseWarning(e, reader.getTablePath(), row, reader.getIndex());
            }
        }
    }

    private static void printParseWarning(ParseRowException e, String path, Row row, int index) {
        Output.prints(Level.WARNING, e.getMessage(),
                "Table:", path,
                "Index:", String.valueOf(index),
                "Column:", row.lastColumnIndex());
    }

    private static boolean isMissionUsable(short mission, Date date) {
        SeasonRange eventDate = missionTable.get(mission).getSeason();
        return (eventDate == null) || eventDate.isWithinRange(date);
    }

    public static List<Short> getUsableMissionsList() {
        return missionsList.stream()
                .filter(m -> isMissionUsable(m, DateUtils.getDate()))
                .collect(Collectors.toList());
    }

    private static void updateOverriddenTables() {
        tables.keySet().forEach(property -> {
            String overriddenTable = Configuration.get(property);

            if (!overriddenTable.isEmpty()) {
                tables.put(property, Constants.TABLE_DIR + overriddenTable);
            }
        });
    }

    public static String getTablePath(String property) {
        return tables.get(property);
    }
}
