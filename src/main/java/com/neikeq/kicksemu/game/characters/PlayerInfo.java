package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Expiration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.ItemType;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.game.table.ItemInfo;
import com.neikeq.kicksemu.game.table.LearnInfo;
import com.neikeq.kicksemu.game.table.OptionInfo;
import com.neikeq.kicksemu.game.misc.friendship.FriendsList;
import com.neikeq.kicksemu.game.misc.ignored.IgnoredList;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.storage.MySqlManager;
import com.neikeq.kicksemu.storage.SqlUtils;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class PlayerInfo {

    private static final String table = "characters";

    // Sql getters

    public static int getOwner(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);
        int owner = s != null && s.getPlayerCache().getOwner() != null ?
                s.getPlayerCache().getOwner() : SqlUtils.getInt("owner", table, id, con);

        if (s != null) {
            s.getPlayerCache().setOwner(owner);
        }

        return owner;
    }

    public static String getName(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);
        String name = s != null && s.getPlayerCache().getName() != null ?
                s.getPlayerCache().getName() : SqlUtils.getString("name", table, id, con);

        if (s != null) {
            s.getPlayerCache().setName(name);
        }

        return name;
    }

    public static int getClubId(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);
        int clubId = s != null && s.getPlayerCache().getClubId() != null ?
                s.getPlayerCache().getClubId() : SqlUtils.getInt("club_id", table, id, con);

        if (s != null) {
            s.getPlayerCache().setClubId(clubId);
        }

        return clubId;
    }

    public static boolean isBlocked(int id, Connection ... con) {
        return SqlUtils.getBoolean("blocked", table, id, con);
    }

    public static boolean isModerator(int id, Connection ... con) {
        return SqlUtils.getBoolean("moderator", table, id, con);
    }

    public static boolean isVisible(int id, Connection ... con) {
        return SqlUtils.getBoolean("visible", table, id, con);
    }

    public static short getLevel(int id, Connection ... con) {
        return SqlUtils.getShort("level", table, id, con);
    }

    public static short getPosition(int id, Connection ... con) {
        return SqlUtils.getShort("position", table, id, con);
    }

    public static short getCurrentQuest(int id, Connection ... con) {
        return SqlUtils.getShort("quest_current", table, id, con);
    }

    public static short getRemainingQuestMatches(int id, Connection ... con) {
        return SqlUtils.getShort("quest_matches_left", table, id, con);
    }

    public static byte getTutorialDribbling(int id, Connection ... con) {
        return SqlUtils.getByte("tutorial_dribbling", table, id, con);
    }

    public static byte getTutorialPassing(int id, Connection ... con) {
        return SqlUtils.getByte("tutorial_passing", table, id, con);
    }

    public static byte getTutorialShooting(int id, Connection ... con) {
        return SqlUtils.getByte("tutorial_shooting", table, id, con);
    }

    public static byte getTutorialDefense(int id, Connection ... con) {
        return SqlUtils.getByte("tutorial_defense", table, id, con);
    }

    public static boolean getReceivedReward(int id, Connection ... con) {
        return SqlUtils.getBoolean("received_reward", table, id, con);
    }

    public static int getExperience(int id, Connection ... con) {
        return SqlUtils.getInt("experience", table, id, con);
    }

    public static int getPoints(int id, Connection ... con) {
        return SqlUtils.getInt("points", table, id, con);
    }

    public static short getTicketsKash(int id, Connection ... con) {
        return SqlUtils.getShort("tickets_kash", table, id, con);
    }

    public static short getTicketsPoints(int id, Connection ... con) {
        return SqlUtils.getShort("tickets_points", table, id, con);
    }

    public static short getAnimation(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);
        short animation = s != null && s.getPlayerCache().getAnimation() != null ?
                s.getPlayerCache().getAnimation() : SqlUtils.getShort("animation", table, id, con);

        if (s != null) {
            s.getPlayerCache().setAnimation(animation);
        }

        return animation;
    }

    public static short getFace(int id, Connection ... con) {
        return SqlUtils.getShort("face", table, id, con);
    }

    public static int getDefaultHead(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);
        int defaultHead = s != null && s.getPlayerCache().getDefaultHead() != null ?
                s.getPlayerCache().getDefaultHead() : SqlUtils.getInt("default_head", table, id, con);

        if (s != null) {
            s.getPlayerCache().setDefaultHead(defaultHead);
        }

        return defaultHead;
    }

    public static int getDefaultShirts(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);
        int defaultShirts = s != null && s.getPlayerCache().getDefaultShirts() != null ?
                s.getPlayerCache().getDefaultShirts() : SqlUtils.getInt("default_shirts", table, id, con);

        if (s != null) {
            s.getPlayerCache().setDefaultShirts(defaultShirts);
        }

        return defaultShirts;
    }

    public static int getDefaultPants(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);
        int defaultPants = s != null && s.getPlayerCache().getDefaultPants() != null ?
                s.getPlayerCache().getDefaultPants() : SqlUtils.getInt("default_pants", table, id, con);

        if (s != null) {
            s.getPlayerCache().setDefaultPants(defaultPants);
        }

        return defaultPants;
    }

    public static int getDefaultShoes(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);
        int defaultShoes = s != null && s.getPlayerCache().getDefaultShoes() != null ?
                s.getPlayerCache().getDefaultShoes() : SqlUtils.getInt("default_shoes", table, id, con);

        if (s != null) {
            s.getPlayerCache().setDefaultShoes(defaultShoes);
        }

        return defaultShoes;
    }

    public static byte getSkillSlots(int id, Connection ... con) {
        Iterator<Item> items = getInventoryItems(id, con).values().stream()
                .filter(i -> i.getId() == 2021010 && i.isSelected()).iterator();

        // Default and minimum skill slots is 6
        byte slots = 6;

        while (items.hasNext()) {
            OptionInfo optionInfo = TableManager.getOptionInfo(oi ->
                    oi.getId() == items.next().getBonusOne());

            slots += optionInfo.getValue();
        }

        return slots;
    }

    public static Item getItemInUseByType(ItemType type, int id, Connection ... con) {
        Optional<Item> result = getInventoryItems(id, con).values().stream().filter(item -> {
            ItemInfo itemInfo = TableManager.getItemInfo(i -> i.getId() == item.getId());
            return itemInfo != null && itemInfo.getType() == type.toInt() && item.isSelected();
        }).findFirst();

        return result.isPresent() ? result.get() : null;
    }

    public static Item getItemHead(int id, Connection ... con) {
        return getItemInUseByType(ItemType.HEAD, id, con);
    }

    public static Item getItemGlasses(int id, Connection ... con) {
        return getItemInUseByType(ItemType.GLASSES, id, con);
    }

    public static Item getItemShirts(int id, Connection ... con) {
        return getItemInUseByType(ItemType.SHIRTS, id, con);
    }

    public static Item getItemPants(int id, Connection ... con) {
        return getItemInUseByType(ItemType.PANTS, id, con);
    }

    public static Item getItemGlove(int id, Connection ... con) {
        return getItemInUseByType(ItemType.GLOVES, id, con);
    }

    public static Item getItemShoes(int id, Connection ... con) {
        return getItemInUseByType(ItemType.SHOES, id, con);
    }

    public static Item getItemSocks(int id, Connection ... con) {
        return getItemInUseByType(ItemType.SOCKS, id, con);
    }

    public static Item getItemWrist(int id, Connection ... con) {
        return getItemInUseByType(ItemType.WRIST, id, con);
    }

    public static Item getItemArm(int id, Connection ... con) {
        return getItemInUseByType(ItemType.ARM, id, con);
    }

    public static Item getItemKnee(int id, Connection ... con) {
        return getItemInUseByType(ItemType.KNEE, id, con);
    }

    public static Item getItemEar(int id, Connection ... con) {
        return getItemInUseByType(ItemType.EAR, id, con);
    }

    public static Item getItemNeck(int id, Connection ... con) {
        return getItemInUseByType(ItemType.NECK, id, con);
    }

    public static Item getItemMask(int id, Connection ... con) {
        return getItemInUseByType(ItemType.MASK, id, con);
    }

    public static Item getItemMuffler(int id, Connection ... con) {
        return getItemInUseByType(ItemType.MUFFLER, id, con);
    }

    public static Item getItemPackage(int id, Connection ... con) {
        return getItemInUseByType(ItemType.PACKAGE, id, con);
    }

    public static short getStatsPoints(int id, Connection ... con) {
        return SqlUtils.getShort("stats_points", table, id, con);
    }

    // Stats

    public static short getStatsRunning(int id, Connection ... con) {
        return SqlUtils.getShort("stats_running", table, id, con);
    }

    public static short getStatsEndurance(int id, Connection ... con) {
        return SqlUtils.getShort("stats_endurance", table, id, con);
    }

    public static short getStatsAgility(int id, Connection ... con) {
        return SqlUtils.getShort("stats_agility", table, id, con);
    }

    public static short getStatsBallControl(int id, Connection ... con) {
        return SqlUtils.getShort("stats_ball_control", table, id, con);
    }

    public static short getStatsDribbling(int id, Connection ... con) {
        return SqlUtils.getShort("stats_dribbling", table, id, con);
    }

    public static short getStatsStealing(int id, Connection ... con) {
        return SqlUtils.getShort("stats_stealing", table, id, con);
    }

    public static short getStatsTackling(int id, Connection ... con) {
        return SqlUtils.getShort("stats_tackling", table, id, con);
    }

    public static short getStatsHeading(int id, Connection ... con) {
        return SqlUtils.getShort("stats_heading", table, id, con);
    }

    public static short getStatsShortShots(int id, Connection ... con) {
        return SqlUtils.getShort("stats_short_shots", table, id, con);
    }

    public static short getStatsLongShots(int id, Connection ... con) {
        return SqlUtils.getShort("stats_long_shots", table, id, con);
    }

    public static short getStatsCrossing(int id, Connection ... con) {
        return SqlUtils.getShort("stats_crossing", table, id, con);
    }

    public static short getStatsShortPasses(int id, Connection ... con) {
        return SqlUtils.getShort("stats_short_passes", table, id, con);
    }

    public static short getStatsLongPasses(int id, Connection ... con) {
        return SqlUtils.getShort("stats_long_passes", table, id, con);
    }

    public static short getStatsMarking(int id, Connection ... con) {
        return SqlUtils.getShort("stats_marking", table, id, con);
    }

    public static short getStatsGoalkeeping(int id, Connection ... con) {
        return SqlUtils.getShort("stats_goalkeeping", table, id, con);
    }

    public static short getStatsPunching(int id, Connection ... con) {
        return SqlUtils.getShort("stats_punching", table, id, con);
    }

    public static short getStatsDefense(int id, Connection ... con) {
        return SqlUtils.getShort("stats_defense", table, id, con);
    }

    public static PlayerStats getTrainingStats(int id, Connection ... con) {
        PlayerStats learnStats = new PlayerStats();

        getInventoryTraining(id, con).values().stream().forEach(learn -> {
            LearnInfo learnInfo = TableManager.getLearnInfo(l -> l.getId() == learn.getId());

            if (learnInfo != null) {
                CharacterUtils.sumStatsByIndex(learnInfo.getStatIndex(),
                        learnInfo.getStatPoints(), learnStats);
            }
        });

        return learnStats;
    }

    public static PlayerStats getBonusStats(int id, Connection ... con) {
        PlayerStats bonusStats = new PlayerStats();

        getInventoryItems(id, con).values().stream().filter(Item::isSelected).forEach(item -> {
            OptionInfo optionInfoOne = TableManager.getOptionInfo(of ->
                    of.getId() == item.getBonusOne());
            OptionInfo optionInfoTwo = TableManager.getOptionInfo(of ->
                    of.getId() == item.getBonusTwo());

            if (optionInfoOne != null) {
                CharacterUtils.sumStatsByIndex(optionInfoOne.getType() - 10,
                        optionInfoOne.getValue(), bonusStats);
            }

            if (optionInfoTwo != null) {
                CharacterUtils.sumStatsByIndex(optionInfoTwo.getType() - 10,
                        optionInfoTwo.getValue(), bonusStats);
            }
        });

        return bonusStats;
    }

    // History

    public static int getHistoryMatches(int id, Connection ... con) {
        return SqlUtils.getInt("history_matches", table, id, con);
    }

    public static int getHistoryWins(int id, Connection ... con) {
        return SqlUtils.getInt("history_wins", table, id, con);
    }

    public static int getHistoryDraws(int id, Connection ... con) {
        return SqlUtils.getInt("history_draws", table, id, con);
    }

    public static int getHistoryMom(int id, Connection ... con) {
        return SqlUtils.getInt("history_MOM", table, id, con);
    }

    public static int getHistoryValidGoals(int id, Connection ... con) {
        return SqlUtils.getInt("history_valid_goals", table, id, con);
    }

    public static int getHistoryValidAssists(int id, Connection ... con) {
        return SqlUtils.getInt("history_valid_assists", table, id, con);
    }

    public static int getHistoryValidInterception(int id, Connection ... con) {
        return SqlUtils.getInt("history_valid_interception", table, id, con);
    }

    public static int getHistoryValidShooting(int id, Connection ... con) {
        return SqlUtils.getInt("history_valid_shooting", table, id, con);
    }

    public static int getHistoryValidStealing(int id, Connection ... con) {
        return SqlUtils.getInt("history_valid_stealing", table, id, con);
    }

    public static int getHistoryValidTackling(int id, Connection ... con) {
        return SqlUtils.getInt("history_valid_tackling", table, id, con);
    }

    public static int getHistoryShooting(int id, Connection ... con) {
        return SqlUtils.getInt("history_shooting", table, id, con);
    }

    public static int getHistoryStealing(int id, Connection ... con) {
        return SqlUtils.getInt("history_stealing", table, id, con);
    }

    public static int getHistoryTackling(int id, Connection ... con) {
        return SqlUtils.getInt("history_tackling", table, id, con);
    }

    public static int getHistoryTotalPoints(int id, Connection ... con) {
        return SqlUtils.getInt("history_total_points", table, id, con);
    }

    // History Last Month

    public static int getHistoryMonthMatches(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_matches", table, id, con);
    }

    public static int getHistoryMonthWins(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_wins", table, id, con);
    }

    public static int getHistoryMonthDraws(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_draws", table, id, con);
    }

    public static int getHistoryMonthMom(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_MOM", table, id, con);
    }

    public static int getHistoryMonthValidGoals(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_valid_goals", table, id, con);
    }

    public static int getHistoryMonthValidAssists(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_valid_assists", table, id, con);
    }

    public static int getHistoryMonthValidInterception(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_valid_interception", table, id, con);
    }

    public static int getHistoryMonthValidShooting(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_valid_shooting", table, id, con);
    }

    public static int getHistoryMonthValidStealing(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_valid_stealing", table, id, con);
    }

    public static int getHistoryMonthValidTackling(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_valid_tackling", table, id, con);
    }

    public static int getHistoryMonthShooting(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_shooting", table, id, con);
    }

    public static int getHistoryMonthStealing(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_stealing", table, id, con);
    }

    public static int getHistoryMonthTackling(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_tackling", table, id, con);
    }

    public static int getHistoryMonthTotalPoints(int id, Connection ... con) {
        return SqlUtils.getInt("history_month_total_points", table, id, con);
    }

    // Others

    public static String getStatusMessage(int id, Connection ... con) {
        return SqlUtils.getString("status_message", table, id, con);
    }

    public static Map<Integer, Item> getInventoryItems(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);

        if (s != null && s.getPlayerCache().getItems() != null) {
            return s.getPlayerCache().getItems();
        }

        Map<Integer, Item> items = new HashMap<>();

        String query = "SELECT * FROM items WHERE player_id = ? AND " +
                "(timestamp_expire > ? OR expiration = ?)";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setTimestamp(2, DateUtils.getTimestamp());
                stmt.setInt(3, Expiration.DAYS_PERM.toInt());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Item item = new Item(rs.getInt("item_id"), rs.getInt("inventory_id"),
                                rs.getInt("expiration"), rs.getInt("bonus_one"),
                                rs.getInt("bonus_two"), rs.getShort("usages"),
                                rs.getTimestamp("timestamp_expire"),
                                rs.getBoolean("selected"), rs.getBoolean("visible"));

                        items.put(item.getInventoryId(), item);
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}

        if (s != null) {
            s.getPlayerCache().setItems(items);
        }

        return items;
    }

    public static Map<Integer, Training> getInventoryTraining(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);

        if (s != null && s.getPlayerCache().getLearns() != null) {
            return s.getPlayerCache().getLearns();
        }

        Map<Integer, Training> learns = new HashMap<>();

        String query = "SELECT * FROM learns WHERE player_id = ?";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Training skill = new Training(rs.getInt("learn_id"),
                                rs.getInt("inventory_id"), rs.getBoolean("visible"));

                        learns.put(skill.getInventoryId(), skill);
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}

        if (s != null) {
            s.getPlayerCache().setLearns(learns);
        }

        return learns;
    }

    public static Map<Integer, Skill> getInventorySkills(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);

        if (s != null && s.getPlayerCache().getSkills() != null) {
            return s.getPlayerCache().getSkills();
        }

        Map<Integer, Skill> skills = new HashMap<>();

        String query = "SELECT * FROM skills WHERE player_id = ? AND " +
                "(timestamp_expire > ? OR expiration = ?)";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setTimestamp(2, DateUtils.getTimestamp());
                stmt.setInt(3, Expiration.DAYS_PERM.toInt());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Skill skill = new Skill(
                                rs.getInt("skill_id"), rs.getInt("inventory_id"),
                                rs.getInt("expiration"), rs.getByte("selection_index"),
                                rs.getTimestamp("timestamp_expire"), rs.getBoolean("visible"));

                        skills.put(skill.getInventoryId(), skill);
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}

        if (s != null) {
            s.getPlayerCache().setSkills(skills);
        }

        return skills;
    }

    public static Map<Integer, Celebration> getInventoryCelebration(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);

        if (s != null && s.getPlayerCache().getCeles() != null) {
            return s.getPlayerCache().getCeles();
        }

        Map<Integer, Celebration> celes = new HashMap<>();

        String query = "SELECT * FROM ceres WHERE player_id = ? AND " +
                "(timestamp_expire > ? OR expiration = ?)";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setTimestamp(2, DateUtils.getTimestamp());
                stmt.setInt(3, Expiration.DAYS_PERM.toInt());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Celebration cele = new Celebration(
                                rs.getInt("cere_id"), rs.getInt("inventory_id"),
                                rs.getInt("expiration"), rs.getByte("selection_index"),
                                rs.getTimestamp("timestamp_expire"), rs.getBoolean("visible"));

                        celes.put(cele.getInventoryId(), cele);
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}

        if (s != null) {
            s.getPlayerCache().setCeles(celes);
        }

        return celes;
    }

    public static FriendsList getFriendsList(int id, Connection ... con) {
        return FriendsList.fromString(SqlUtils.getString("friends_list", table, id, con), id);
    }

    public static IgnoredList getIgnoredList(int id, Connection ... con) {
        return IgnoredList.fromString(SqlUtils.getString("ignored_list", table, id, con), id);
    }

    // Sql setters

    public static void setBlocked(boolean value, int id, Connection ... con) {
        SqlUtils.setBoolean("blocked", value, table, id, con);
    }

    public static void setVisible(boolean value, int id, Connection ... con) {
        SqlUtils.setBoolean("visible", value, table, id, con);
    }

    public static void setLevel(short value, int id, Connection ... con) {
        SqlUtils.setShort("level", value, table, id, con);
    }

    public static void setPosition(short value, int id, Connection ... con) {
        SqlUtils.setShort("position", value, table, id, con);
    }

    public static void setCurrentQuest(short value, int id, Connection ... con) {
        SqlUtils.setShort("quest_current", value, table, id, con);
    }

    public static void setRemainingQuestMatches(short value, int id, Connection ... con) {
        SqlUtils.setShort("quest_matches_left", value, table, id, con);
    }

    public static void setTutorialDribbling(byte value, int id, Connection ... con) {
        SqlUtils.setByte("tutorial_dribbling", value, table, id, con);
    }

    public static void setTutorialPassing(byte value, int id, Connection ... con) {
        SqlUtils.setByte("tutorial_passing", value, table, id, con);
    }

    public static void setTutorialShooting(byte value, int id, Connection ... con) {
        SqlUtils.setByte("tutorial_shooting", value, table, id, con);
    }

    public static void setTutorialDefense(byte value, int id, Connection ... con) {
        SqlUtils.setByte("tutorial_defense", value, table, id, con);
    }

    public static void setReceivedReward(boolean value, int id, Connection ... con) {
        SqlUtils.setBoolean("received_reward", value, table, id, con);
    }

    public static void sumExperience(int value, int id, Connection ... con) {
        SqlUtils.sumInt("experience", value, table, id, con);
    }

    public static void sumPoints(int value, int id, Connection ... con) {
        SqlUtils.sumInt("points", value, table, id, con);
    }

    public static void setTicketsKash(short value, int id, Connection ... con) {
        SqlUtils.setShort("tickets_kash", value, table, id, con);
    }

    public static void setTicketsPoints(short value, int id, Connection ... con) {
        SqlUtils.setShort("tickets_points", value, table, id, con);
    }

    public static void setFace(short value, int id, Connection ... con) {
        SqlUtils.setShort("face", value, table, id, con);
    }

    public static void setStatsPoints(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_points", value, table, id, con);
    }

    public static void setStatsRunning(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_running", value, table, id, con);
    }

    public static void setStatsEndurance(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_endurance", value, table, id, con);
    }

    public static void setStatsAgility(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_agility", value, table, id, con);
    }

    public static void setStatsBallControl(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_ball_control", value, table, id, con);
    }

    public static void setStatsDribbling(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_dribbling", value, table, id, con);
    }

    public static void setStatsStealing(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_stealing", value, table, id, con);
    }

    public static void setStatsTackling(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_tackling", value, table, id, con);
    }

    public static void setStatsHeading(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_heading", value, table, id, con);
    }

    public static void setStatsShortShots(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_short_shots", value, table, id, con);
    }

    public static void setStatsLongShots(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_long_shots", value, table, id, con);
    }

    public static void setStatsCrossing(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_crossing", value, table, id, con);
    }

    public static void setStatsShortPasses(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_short_passes", value, table, id, con);
    }

    public static void setStatsLongPasses(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_long_passes", value, table, id, con);
    }

    public static void setStatsMarking(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_marking", value, table, id, con);
    }

    public static void setStatsGoalkeeping(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_goalkeeping", value, table, id, con);
    }

    public static void setStatsPunching(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_punching", value, table, id, con);
    }

    public static void setStatsDefense(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_defense", value, table, id, con);
    }

    public static void sumStatsPoints(short value, int id, Connection ... con) {
        SqlUtils.sumShort("stats_points", value, table, id, con);
    }

    public static short sumStatsRunning(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsRunning(id, con), value);
        SqlUtils.sumShort("stats_running", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsEndurance(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsEndurance(id, con), value);
        SqlUtils.sumShort("stats_endurance", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsAgility(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsAgility(id, con), value);
        SqlUtils.sumShort("stats_agility", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsBallControl(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsBallControl(id, con), value);
        SqlUtils.sumShort("stats_ball_control", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsDribbling(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsDribbling(id, con), value);
        SqlUtils.sumShort("stats_dribbling", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsStealing(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsStealing(id, con), value);
        SqlUtils.sumShort("stats_stealing", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsTackling(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsTackling(id, con), value);
        SqlUtils.sumShort("stats_tackling", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsHeading(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsHeading(id, con), value);
        SqlUtils.sumShort("stats_heading", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsShortShots(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsShortShots(id, con), value);
        SqlUtils.sumShort("stats_short_shots", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsLongShots(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsLongShots(id, con), value);
        SqlUtils.sumShort("stats_long_shots", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsCrossing(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsCrossing(id, con), value);
        SqlUtils.sumShort("stats_crossing", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsShortPasses(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsShortPasses(id, con), value);
        SqlUtils.sumShort("stats_short_passes", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsLongPasses(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsLongPasses(id, con), value);
        SqlUtils.sumShort("stats_long_passes", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsMarking(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsMarking(id, con), value);
        SqlUtils.sumShort("stats_marking", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsGoalkeeping(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsGoalkeeping(id, con), value);
        SqlUtils.sumShort("stats_goalkeeping", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsPunching(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsPunching(id, con), value);
        SqlUtils.sumShort("stats_punching", add, table, id, con);
        return (short)(value - add);
    }

    public static short sumStatsDefense(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsDefense(id, con), value);
        SqlUtils.sumShort("stats_defense", add, table, id, con);
        return (short)(value - add);
    }

    public static void sumHistoryMatches(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_matches", value, table, id, con);
        SqlUtils.sumInt("history_month_matches", value, table, id, con);
    }

    public static void sumHistoryWins(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_wins", value, table, id, con);
        SqlUtils.sumInt("history_month_wins", value, table, id, con);
    }

    public static void sumHistoryDraws(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_draws", value, table, id, con);
        SqlUtils.sumInt("history_month_draws", value, table, id, con);
    }

    public static void sumHistoryMom(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_MOM", value, table, id, con);
        SqlUtils.sumInt("history_month_MOM", value, table, id, con);
    }

    public static void sumHistoryValidGoals(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_valid_goals", value, table, id, con);
        SqlUtils.sumInt("history_month_valid_goals", value, table, id, con);
    }

    public static void sumHistoryValidAssists(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_valid_assists", value, table, id, con);
        SqlUtils.sumInt("history_month_valid_assists", value, table, id, con);
    }

    public static void sumHistoryValidInterception(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_valid_interception", value, table, id, con);
        SqlUtils.sumInt("history_month_valid_interception", value, table, id, con);
    }

    public static void sumHistoryValidShooting(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_valid_shooting", value, table, id, con);
        SqlUtils.sumInt("history_month_valid_shooting", value, table, id, con);
    }

    public static void sumHistoryValidStealing(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_valid_stealing", value, table, id, con);
        SqlUtils.sumInt("history_month_valid_stealing", value, table, id, con);
    }

    public static void sumHistoryValidTackling(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_valid_tackling", value, table, id, con);
        SqlUtils.sumInt("history_month_valid_tackling", value, table, id, con);
    }

    public static void sumHistoryShooting(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_shooting", value, table, id, con);
        SqlUtils.sumInt("history_month_shooting", value, table, id, con);
    }

    public static void sumHistoryStealing(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_stealing", value, table, id, con);
        SqlUtils.sumInt("history_month_stealing", value, table, id, con);
    }

    public static void sumHistoryTackling(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_tackling", value, table, id, con);
        SqlUtils.sumInt("history_month_tackling", value, table, id, con);
    }

    public static void sumHistoryTotalPoints(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_total_points", value, table, id, con);
        SqlUtils.sumInt("history_month_total_points", value, table, id, con);
    }

    public static void setStatusMessage(String value, int id, Connection ... con) {
        SqlUtils.setString("status_message", value, table, id, con);
    }

    public static void addInventoryItem(Item item, int id, Connection ... con) {
        String query = "INSERT INTO items VALUES(?,?,?,?,?,?,?,?,?,?)";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setInt(2, item.getInventoryId());
                stmt.setInt(3, item.getId());
                stmt.setInt(4, item.getExpiration().toInt());
                stmt.setInt(5, item.getBonusOne());
                stmt.setInt(6, item.getBonusTwo());
                stmt.setShort(7, item.getRemainUsages());
                stmt.setTimestamp(8, item.getTimestampExpire());
                stmt.setBoolean(9, item.isSelected());
                stmt.setBoolean(10, item.isVisible());

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void setInventoryItem(Item item, int id, Connection ... con) {
        String query = "UPDATE items SET bonus_one=?, bonus_two=?, usages=?, " +
                "timestamp_expire=?, selected=? WHERE player_id=? AND inventory_id=?";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, item.getBonusOne());
                stmt.setInt(2, item.getBonusTwo());
                stmt.setShort(3, item.getRemainUsages());
                stmt.setTimestamp(4, item.getTimestampExpire());
                stmt.setBoolean(5, item.isSelected());
                stmt.setInt(6, id);
                stmt.setInt(7, item.getInventoryId());

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void addInventoryTraining(Training training, int id, Connection ... con) {
        String query = "INSERT INTO learns VALUES(?,?,?,?)";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setInt(2, training.getInventoryId());
                stmt.setInt(3, training.getId());
                stmt.setBoolean(4, training.isVisible());

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void addInventorySkill(Skill skill, int id, Connection ... con) {
        String query = "INSERT INTO skills VALUES(?,?,?,?,?,?,?)";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setInt(2, skill.getInventoryId());
                stmt.setInt(3, skill.getId());
                stmt.setInt(4, skill.getExpiration().toInt());
                stmt.setByte(5, skill.getSelectionIndex());
                stmt.setTimestamp(6, skill.getTimestampExpire());
                stmt.setBoolean(7, skill.isVisible());

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void setInventorySkill(Skill skill, int id, Connection ... con) {
        String query = "UPDATE skills SET selection_index=?, timestamp_expire=? " +
                "WHERE player_id=? AND inventory_id=?";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setByte(1, skill.getSelectionIndex());
                stmt.setTimestamp(2, skill.getTimestampExpire());
                stmt.setInt(3, id);
                stmt.setInt(4, skill.getInventoryId());

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void addInventoryCele(Celebration cele, int id, Connection ... con) {
        String query = "INSERT INTO ceres VALUES(?,?,?,?,?,?,?)";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setInt(2, cele.getInventoryId());
                stmt.setInt(3, cele.getId());
                stmt.setInt(4, cele.getExpiration().toInt());
                stmt.setByte(5, cele.getSelectionIndex());
                stmt.setTimestamp(6, cele.getTimestampExpire());
                stmt.setBoolean(7, cele.isVisible());

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void setInventoryCele(Celebration cele, int id, Connection ... con) {
        String query = "UPDATE ceres SET selection_index=?, timestamp_expire=? " +
                "WHERE player_id=? AND inventory_id=?";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setByte(1, cele.getSelectionIndex());
                stmt.setTimestamp(2, cele.getTimestampExpire());
                stmt.setInt(3, id);
                stmt.setInt(4, cele.getInventoryId());

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void setFriendsList(FriendsList value, int id, Connection ... con) {
        SqlUtils.setString("friends_list", value.toString(), table, id, con);
    }

    public static void setIgnoredList(IgnoredList value, int id, Connection ... con) {
        SqlUtils.setString("ignored_list", value.toString(), table, id, con);
    }
}
