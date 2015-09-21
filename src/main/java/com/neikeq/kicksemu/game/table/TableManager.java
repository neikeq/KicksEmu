package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.config.Constants;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.utils.SeasonRange;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.utils.table.Row;
import com.neikeq.kicksemu.utils.table.TableReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private static final Map<Integer, SkillInfo> skillsTable = new HashMap<>();
    private static final Map<Integer, CeleInfo> celesTable = new HashMap<>();
    private static final Map<Integer, LearnInfo> learnTable = new HashMap<>();
    private static final Map<Integer, ItemFree> itemFreeTable = new HashMap<>();
    private static final Map<Integer, ItemInfo> itemTable = new HashMap<>();
    private static final Map<Integer, BonusInfo> bonusTable = new HashMap<>();
    private static final Map<Integer, OptionInfo> optionTable = new HashMap<>();
    private static final Map<Short, LevelInfo> levelTable = new HashMap<>();
    private static final Map<Short, MissionInfo> missionTable = new HashMap<>();

    private static final List<Short> missionsList = new ArrayList<>();
    private static final Map<Short, SeasonRange> seasonMissions = new HashMap<>();

    public static void initialize() {
        initializeItemFreeTable();
        initializeSkillTable();
        initializeCeleTable();
        initializeLearnTable();
        initializeItemTable();
        initializeBonusTable();
        initializeOptionTable();
        initializeLevelTable();
        initializeMissionTable();
    }

    public static SkillInfo getSkillInfo(Predicate<SkillInfo> filter) {
        Optional<SkillInfo> result = skillsTable.values().stream().filter(filter).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    public static LevelInfo getLevelInfo(Predicate<LevelInfo> filter) {
        Optional<LevelInfo> result = levelTable.values().stream().filter(filter)
                .reduce((previous, current) -> current);

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

    public static BonusInfo getBonusInfo(Predicate<BonusInfo> filter) {
        Optional<BonusInfo> result = bonusTable.values().stream()
                .filter(filter).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    public static OptionInfo getOptionInfo(Predicate<OptionInfo> filter) {
        Optional<OptionInfo> result = optionTable.values().stream().filter(filter).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    public static MissionInfo getMissionInfo(Predicate<MissionInfo> filter) {
        Optional<MissionInfo> result = missionTable.values().stream().filter(filter).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    private static void initializeSkillTable() {
        TableReader reader = new TableReader(Constants.TABLE_SKILL_PATH);

        Row line;
        while ((line = reader.nextRow()) != null) {
            SkillInfo row = new SkillInfo(line);
            skillsTable.put(row.getId(), row);
        }
    }

    private static void initializeCeleTable() {
        TableReader reader = new TableReader(Constants.TABLE_CELE_PATH);

        Row line;
        while ((line = reader.nextRow()) != null) {
            CeleInfo row = new CeleInfo(line);
            celesTable.put(row.getId(), row);
        }
    }

    private static void initializeLearnTable() {
        TableReader reader = new TableReader(Constants.TABLE_LEARN_PATH);

        Row line;
        while ((line = reader.nextRow()) != null) {
            LearnInfo row = new LearnInfo(line);
            learnTable.put(row.getId(), row);
        }
    }

    private static void initializeItemFreeTable() {
        TableReader reader = new TableReader(Constants.TABLE_ITEM_FREE_PATH);

        Row line;
        while ((line = reader.nextRow()) != null) {
            ItemFree row = new ItemFree(line);
            itemFreeTable.put(row.getId(), row);
        }
    }

    private static void initializeItemTable() {
        TableReader reader = new TableReader(Constants.TABLE_ITEM_PATH);

        Row line;
        while ((line = reader.nextRow()) != null) {
            ItemInfo row = new ItemInfo(line);
            itemTable.put(row.getId(), row);
        }
    }

    private static void initializeBonusTable() {
        TableReader reader = new TableReader(Constants.TABLE_BONUS_PATH);

        Row line;
        while ((line = reader.nextRow()) != null) {
            BonusInfo row = new BonusInfo(line);
            bonusTable.put(row.getType(), row);
        }
    }

    private static void initializeOptionTable() {
        TableReader reader = new TableReader(Constants.TABLE_OPTION_PATH);

        Row line;
        while ((line = reader.nextRow()) != null) {
            OptionInfo row = new OptionInfo(line);
            optionTable.put(row.getId(), row);
        }
    }

    private static void initializeLevelTable() {
        TableReader reader = new TableReader(Constants.TABLE_LEVEL_FILE);

        Row line;
        while ((line = reader.nextRow()) != null) {
            LevelInfo row = new LevelInfo(line);
            levelTable.put(row.getLevel(), row);
        }

        LevelInfo lastLevel = levelTable.get(Collections.max(levelTable.keySet()));

        EXPERIENCE_LIMIT = lastLevel.getExperience() + lastLevel.getExperienceGap();
    }

    private static void initializeMissionTable() {
        TableReader reader = new TableReader(Constants.TABLE_MISSION_FILE);

        Row line;
        while ((line = reader.nextRow()) != null) {
            MissionInfo row = new MissionInfo(line);
            missionTable.put(row.getId(), row);

            String season = line.nextColumn();
            if (!season.isEmpty()) {
                try {
                    seasonMissions.put(row.getId(), columnToSeasonRange(season));
                } catch (IndexOutOfBoundsException | ParseException ignored) {
                    Output.println("Invalid season range in mission table. Index: " +
                            line.columnAt(0), Level.WARNING);
                }
            }
        }

        missionsList.addAll(missionTable.keySet().stream().collect(Collectors.toList()));
    }

    private static SeasonRange columnToSeasonRange(String col)
            throws IndexOutOfBoundsException, ParseException {
        String[] seasons = col.split("-");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        return new SeasonRange(formatter.parse(seasons[0]), formatter.parse(seasons[1]));
    }

    private static boolean isMissionEnabled(short mission, Date date) {
        SeasonRange eventDate = seasonMissions.get(mission);
        return eventDate == null || eventDate.isWithinRange(date);
    }

    public static List<Short> getMissionsList() {
        return missionsList.stream()
                .filter(m -> isMissionEnabled(m, DateUtils.getDate()))
                .collect(Collectors.toList());
    }
}
