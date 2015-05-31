package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.characters.types.PlayerHistory;
import com.neikeq.kicksemu.game.characters.types.PlayerStats;
import com.neikeq.kicksemu.game.characters.types.QuestState;
import com.neikeq.kicksemu.game.characters.types.TutorialState;
import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.DefaultClothes;
import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.game.inventory.InventoryManager;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.types.ItemType;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class PlayerInfo {

    private static final String TABLE = "characters";

    private static final String ITEM_ACTIVE = String.format("(" +
            "((expiration = %d OR expiration = %d OR expiration = %d) AND usages > 0) OR" +
            "((expiration = %d OR expiration = %d) AND timestamp_expire > CURRENT_TIMESTAMP) OR" +
            " expiration = %d)",
            Expiration.USAGE_10.toInt(), Expiration.USAGE_50.toInt(), Expiration.USAGE_100.toInt(),
            Expiration.DAYS_7.toInt(), Expiration.DAYS_30.toInt(),
            Expiration.DAYS_PERM.toInt());

    private static final String PRODUCT_ACTIVE = String.format(
            "(timestamp_expire > CURRENT_TIMESTAMP OR expiration = %d)",
            Expiration.DAYS_PERM.toInt());

    // getters

    public static int getOwner(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);
        int owner = s != null && s.getPlayerCache().getOwner() != null ?
                s.getPlayerCache().getOwner() : SqlUtils.getInt("owner", TABLE, id, con);

        if (s != null) {
            s.getPlayerCache().setOwner(owner);
        }

        return owner;
    }

    public static String getName(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);
        String name = s != null && s.getPlayerCache().getName() != null ?
                s.getPlayerCache().getName() : SqlUtils.getString("name", TABLE, id, con);

        if (s != null) {
            s.getPlayerCache().setName(name);
        }

        return name;
    }

    public static boolean isBlocked(int id, Connection ... con) {
        return SqlUtils.getBoolean("blocked", TABLE, id, con);
    }

    public static boolean isVisibleInLobby(int id, Connection ... con) {
        final String query = "SELECT moderator, visible FROM " + TABLE + " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() &&
                            (!rs.getBoolean("moderator") || rs.getBoolean("visible"));
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean isModerator(int id, Connection ... con) {
        return SqlUtils.getBoolean("moderator", TABLE, id, con);
    }

    public static boolean isVisible(int id, Connection ... con) {
        return SqlUtils.getBoolean("visible", TABLE, id, con);
    }

    public static short getLevel(int id, Connection ... con) {
        return SqlUtils.getShort("level", TABLE, id, con);
    }

    public static short getPosition(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);

        if (s != null) {
            Short position = s.getPlayerCache().getPosition();

            if (position != null) {
                return position;
            }
        }

        short position = SqlUtils.getShort("position", TABLE, id, con);

        if (s != null) {
            s.getPlayerCache().setPosition(position);
        }

        return position;
    }

    public static QuestState getQuestState(int id, Connection ... con) {
        final String query = "SELECT quest_current, quest_matches_left FROM " +
                TABLE + " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new QuestState(rs.getShort("quest_current"),
                                rs.getShort("quest_matches_left"));
                    } else {
                        return new QuestState();
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            return new QuestState();
        }
    }

    public static TutorialState getTutorialState(int id, Connection ... con) {
        final String query = "SELECT tutorial_dribbling, tutorial_passing, tutorial_shooting, " +
                "tutorial_defense FROM " + TABLE + " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new TutorialState(
                                rs.getByte("tutorial_dribbling"), rs.getByte("tutorial_passing"),
                                rs.getByte("tutorial_shooting"), rs.getByte("tutorial_defense"));
                    } else {
                        return new TutorialState();
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            return new TutorialState();
        }
    }

    public static boolean getReceivedReward(int id, Connection ... con) {
        return SqlUtils.getBoolean("received_reward", TABLE, id, con);
    }

    public static int getExperience(int id, Connection ... con) {
        return SqlUtils.getInt("experience", TABLE, id, con);
    }

    public static int getPoints(int id, Connection ... con) {
        return SqlUtils.getInt("points", TABLE, id, con);
    }

    public static short getTicketsCash(int id, Connection... con) {
        return SqlUtils.getShort("tickets_kash", TABLE, id, con);
    }

    public static short getTicketsPoints(int id, Connection ... con) {
        return SqlUtils.getShort("tickets_points", TABLE, id, con);
    }

    public static short getAnimation(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);
        short animation = s != null && s.getPlayerCache().getAnimation() != null ?
                s.getPlayerCache().getAnimation() : SqlUtils.getShort("animation", TABLE, id, con);

        if (s != null) {
            s.getPlayerCache().setAnimation(animation);
        }

        return animation;
    }

    public static short getFace(int id, Connection ... con) {
        return SqlUtils.getShort("face", TABLE, id, con);
    }

    public static DefaultClothes getDefaultClothes(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);

        DefaultClothes defaultClothes = null;

        if (s != null) {
            defaultClothes = s.getPlayerCache().getDefaultClothes();

            if (defaultClothes != null) {
                return defaultClothes;
            }
        }

        final String query = "SELECT default_head, default_shirts, default_pants, " +
                "default_shoes FROM " + TABLE + " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        defaultClothes = new DefaultClothes(
                                rs.getInt("default_head"), rs.getInt("default_shirts"),
                                rs.getInt("default_pants"), rs.getInt("default_shoes"));
                    } else {
                        defaultClothes = new DefaultClothes(-1, -1, -1, -1);
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            defaultClothes = new DefaultClothes(-1, -1, -1, -1);
        }

        if (s != null) {
            s.getPlayerCache().setDefaultClothes(defaultClothes);
        }

        return defaultClothes;
    }

    public static byte getSkillSlots(int id, Connection ... con) {
        Iterator<Item> items = getInventoryItems(id, con).values().stream()
                .filter(i -> i.getId() == 2021010 && i.isSelected()).iterator();

        // Default and minimum skill slots is 6
        byte slots = 6;

        while (items.hasNext()) {
            Item item = items.next();
            OptionInfo optionInfo = TableManager.getOptionInfo(oi ->
                    oi.getId() == item.getBonusOne());

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
        return SqlUtils.getShort("stats_points", TABLE, id, con);
    }

    public static PlayerStats getStats(int id, Connection ... con) {
        final String query = "SELECT stats_running, stats_endurance, stats_agility, " +
                "stats_ball_control, stats_dribbling, stats_stealing, stats_tackling, " +
                "stats_heading, stats_short_shots, stats_long_shots, stats_crossing, " +
                "stats_short_passes, stats_long_passes, stats_marking, stats_goalkeeping, " +
                "stats_punching, stats_defense FROM " + TABLE + " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new PlayerStats(
                                rs.getShort("stats_running"), rs.getShort("stats_endurance"),
                                rs.getShort("stats_agility"), rs.getShort("stats_ball_control"),
                                rs.getShort("stats_dribbling"), rs.getShort("stats_stealing"),
                                rs.getShort("stats_tackling"), rs.getShort("stats_heading"),
                                rs.getShort("stats_short_shots"), rs.getShort("stats_long_shots"),
                                rs.getShort("stats_crossing"), rs.getShort("stats_short_passes"),
                                rs.getShort("stats_long_passes"), rs.getShort("stats_marking"),
                                rs.getShort("stats_goalkeeping"), rs.getShort("stats_punching"),
                                rs.getShort("stats_defense"));
                    } else {
                        return new PlayerStats();
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            return new PlayerStats();
        }
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

    public static PlayerHistory getHistory(int id, Connection ... con) {
        final String query = "SELECT history_matches, history_wins, history_draws, history_MOM, " +
                "history_valid_goals, history_valid_assists, history_valid_interception, " +
                "history_valid_shooting, history_valid_stealing, history_valid_tackling, " +
                "history_shooting, history_stealing, history_tackling, history_total_points " +
                "FROM " + TABLE + " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new PlayerHistory(
                                rs.getInt("history_matches"),
                                rs.getInt("history_wins"),
                                rs.getInt("history_draws"),
                                rs.getInt("history_MOM"),
                                rs.getInt("history_valid_goals"),
                                rs.getInt("history_valid_assists"),
                                rs.getInt("history_valid_interception"),
                                rs.getInt("history_valid_shooting"),
                                rs.getInt("history_valid_stealing"),
                                rs.getInt("history_valid_tackling"),
                                rs.getInt("history_shooting"),
                                rs.getInt("history_stealing"),
                                rs.getInt("history_tackling"),
                                rs.getInt("history_total_points"));
                    } else {
                        return new PlayerHistory();
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            return new PlayerHistory();
        }
    }

    public static PlayerHistory getMonthHistory(int id, Connection ... con) {
        final String query = "SELECT history_month_matches, history_month_wins, " +
                "history_month_draws, history_month_MOM, history_month_valid_goals, " +
                "history_month_valid_assists, history_month_valid_interception, " +
                "history_month_valid_shooting, history_month_valid_stealing, " +
                "history_month_valid_tackling, history_month_shooting, " +
                "history_month_stealing, history_month_tackling, history_month_total_points " +
                "FROM " + TABLE + " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new PlayerHistory(
                                rs.getInt("history_month_matches"),
                                rs.getInt("history_month_wins"),
                                rs.getInt("history_month_draws"),
                                rs.getInt("history_month_MOM"),
                                rs.getInt("history_month_valid_goals"),
                                rs.getInt("history_month_valid_assists"),
                                rs.getInt("history_month_valid_interception"),
                                rs.getInt("history_month_valid_shooting"),
                                rs.getInt("history_month_valid_stealing"),
                                rs.getInt("history_month_valid_tackling"),
                                rs.getInt("history_month_shooting"),
                                rs.getInt("history_month_stealing"),
                                rs.getInt("history_month_tackling"),
                                rs.getInt("history_month_total_points"));
                    } else {
                        return new PlayerHistory();
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            return new PlayerHistory();
        }
    }

    public static String getStatusMessage(int id, Connection ... con) {
        return SqlUtils.getString("status_message", TABLE, id, con);
    }

    public static Map<Integer, Item> getInventoryItems(int id, Connection ... con) {
        Session s = ServerManager.getSessionById(id);

        if (s != null) {
            Map<Integer, Item> items = s.getPlayerCache().getItems();

            if (items != null) {
                return items;
            }
        }

        Map<Integer, Item> items = new LinkedHashMap<>();

        final String query = "SELECT * FROM items WHERE player_id = ? AND " + ITEM_ACTIVE +
                " LIMIT " + InventoryManager.MAX_INVENTORY_ITEMS;

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

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

        if (s != null) {
            Map<Integer, Training> learns = s.getPlayerCache().getLearns();

            if (learns != null) {
                return learns;
            }
        }

        Map<Integer, Training> learns = new LinkedHashMap<>();

        final String query = "SELECT * FROM learns WHERE player_id = ?";

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

        if (s != null) {
            Map<Integer, Skill> skills = s.getPlayerCache().getSkills();

            if (skills != null) {
                return skills;
            }
        }

        Map<Integer, Skill> skills = new LinkedHashMap<>();

        final String query = "SELECT * FROM skills WHERE player_id = ? AND " + PRODUCT_ACTIVE;

        byte slots = PlayerInfo.getSkillSlots(id, con);
        byte slotsUsed = 0;

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        byte selectionIndex = rs.getByte("selection_index");

                        if (selectionIndex > 0) {
                            if (slotsUsed >= slots) {
                                selectionIndex = 0;
                            } else {
                                slotsUsed++;
                            }
                        }

                        Skill skill = new Skill(
                                rs.getInt("skill_id"), rs.getInt("inventory_id"),
                                rs.getInt("expiration"), selectionIndex,
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

        if (s != null) {
            Map<Integer, Celebration> celes = s.getPlayerCache().getCeles();

            if (celes != null) {
                return celes;
            }
        }

        Map<Integer, Celebration> celes = new LinkedHashMap<>();

        final String query = "SELECT * FROM ceres WHERE player_id = ? AND " + PRODUCT_ACTIVE;

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);

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
        return FriendsList.fromString(SqlUtils.getString("friends_list", TABLE, id, con), id);
    }

    public static IgnoredList getIgnoredList(int id, Connection ... con) {
        return IgnoredList.fromString(SqlUtils.getString("ignored_list", TABLE, id, con), id);
    }

    // setters

    public static void setVisible(boolean value, int id, Connection ... con) {
        SqlUtils.setBoolean("visible", value, TABLE, id, con);
    }

    public static void setLevel(short value, int id, Connection ... con) {
        SqlUtils.setShort("level", value, TABLE, id, con);
    }

    public static void setPosition(short value, int id, Connection ... con) {
        SqlUtils.setShort("position", value, TABLE, id, con);
    }

    public static void setCurrentQuest(short value, int id, Connection ... con) {
        SqlUtils.setShort("quest_current", value, TABLE, id, con);
    }

    public static void setRemainingQuestMatches(short value, int id, Connection ... con) {
        SqlUtils.setShort("quest_matches_left", value, TABLE, id, con);
    }

    public static void setTutorialState(TutorialState tutorial, int id, Connection ... con) {
        final String query = "UPDATE " + TABLE + " SET tutorial_dribbling=?, tutorial_passing=?, " +
                "tutorial_shooting=?, tutorial_defense=? WHERE id=? LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setByte(1, tutorial.getDribbling());
                stmt.setByte(2, tutorial.getPassing());
                stmt.setByte(3, tutorial.getShooting());
                stmt.setByte(4, tutorial.getDefense());
                stmt.setInt(5, id);

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void setReceivedReward(boolean value, int id, Connection ... con) {
        SqlUtils.setBoolean("received_reward", value, TABLE, id, con);
    }

    public static void sumMoney(int experience, int points, int id, Connection ... con) {
        final String query = "UPDATE " + TABLE + " SET experience = experience + ?, " +
                "points = points + ? WHERE id = ? LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, experience >= 0 ? experience : 0);
                stmt.setInt(2, points >= 0 ? points : 0);
                stmt.setInt(3, id);

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void sumPoints(int value, int id, Connection ... con) {
        SqlUtils.sumInt("points", value, TABLE, id, con);
    }

    public static void setTicketsCash(short value, int id, Connection... con) {
        SqlUtils.setShort("tickets_kash", value, TABLE, id, con);
    }

    public static void setTicketsPoints(short value, int id, Connection ... con) {
        SqlUtils.setShort("tickets_points", value, TABLE, id, con);
    }

    public static void setFace(short value, int id, Connection ... con) {
        SqlUtils.setShort("face", value, TABLE, id, con);
    }

    public static void setStatsPoints(short value, int id, Connection ... con) {
        SqlUtils.setShort("stats_points", value, TABLE, id, con);
    }

    public static void setStats(PlayerStats stats, int id, Connection ... con) {
        final String query = "UPDATE " + TABLE + " SET stats_running = ?, stats_endurance = ?, " +
                "stats_agility = ?, stats_ball_control = ?, stats_dribbling = ?," +
                "stats_stealing = ?, stats_tackling = ?, stats_heading = ?, " +
                "stats_short_shots = ?, stats_long_shots = ?, stats_crossing = ?, " +
                "stats_short_passes = ?, stats_long_passes = ?, stats_marking = ?," +
                "stats_goalkeeping = ?, stats_punching = ?, stats_defense = ? " +
                "WHERE id = ? LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setShort(1, stats.getRunning());
                stmt.setShort(2, stats.getEndurance());
                stmt.setShort(3, stats.getAgility());
                stmt.setShort(4, stats.getBallControl());
                stmt.setShort(5, stats.getDribbling());
                stmt.setShort(6, stats.getStealing());
                stmt.setShort(7, stats.getTackling());
                stmt.setShort(8, stats.getHeading());
                stmt.setShort(9, stats.getShortShots());
                stmt.setShort(10, stats.getLongShots());
                stmt.setShort(11, stats.getCrossing());
                stmt.setShort(12, stats.getShortPasses());
                stmt.setShort(13, stats.getLongPasses());
                stmt.setShort(14, stats.getMarking());
                stmt.setShort(15, stats.getGoalkeeping());
                stmt.setShort(16, stats.getPunching());
                stmt.setShort(17, stats.getDefense());
                stmt.setInt(18, id);

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void sumHistory(PlayerHistory history, int id, Connection ... con) {
        final String query = "UPDATE " + TABLE + " SET history_matches = history_matches + ?, " +
                "history_wins = history_wins + ?, history_draws = history_draws + ?, " +
                "history_MOM = history_MOM + ?, history_valid_goals = history_valid_goals + ?, " +
                "history_valid_assists = history_valid_assists + ?, " +
                "history_valid_interception = history_valid_interception + ?, " +
                "history_valid_shooting = history_valid_shooting + ?, " +
                "history_valid_stealing = history_valid_stealing + ?, " +
                "history_valid_tackling = history_valid_tackling + ?, " +
                "history_shooting = history_shooting + ?, " +
                "history_stealing = history_stealing + ?, " +
                "history_tackling = history_tackling + ?, " +
                "history_total_points = history_total_points + ?, " +
                "history_month_matches = history_month_matches + ?, " +
                "history_month_wins = history_month_wins + ?, " +
                "history_month_draws = history_month_draws + ?, " +
                "history_month_MOM = history_month_MOM + ?, " +
                "history_month_valid_goals = history_month_valid_goals + ?, " +
                "history_month_valid_assists = history_month_valid_assists + ?, " +
                "history_month_valid_interception = history_month_valid_interception + ?, " +
                "history_month_valid_shooting = history_month_valid_shooting + ?, " +
                "history_month_valid_stealing = history_month_valid_stealing + ?, " +
                "history_month_valid_tackling = history_month_valid_tackling + ?, " +
                "history_month_shooting = history_month_shooting + ?, " +
                "history_month_stealing = history_month_stealing + ?, " +
                "history_month_tackling = history_month_tackling + ?, " +
                "history_month_total_points = history_month_total_points + ?" +
                " WHERE id = ? LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, history.getMatches());
                stmt.setInt(2, history.getWins());
                stmt.setInt(3, history.getDraws());
                stmt.setInt(4, history.getMom());
                stmt.setInt(5, history.getValidGoals());
                stmt.setInt(6, history.getValidAssists());
                stmt.setInt(7, history.getValidInterception());
                stmt.setInt(8, history.getValidShooting());
                stmt.setInt(9, history.getValidStealing());
                stmt.setInt(10, history.getValidTackling());
                stmt.setInt(11, history.getShooting());
                stmt.setInt(12, history.getStealing());
                stmt.setInt(13, history.getTackling());
                stmt.setInt(14, history.getTotalPoints());

                stmt.setInt(15, history.getMatches());
                stmt.setInt(16, history.getWins());
                stmt.setInt(17, history.getDraws());
                stmt.setInt(18, history.getMom());
                stmt.setInt(19, history.getValidGoals());
                stmt.setInt(20, history.getValidAssists());
                stmt.setInt(21, history.getValidInterception());
                stmt.setInt(22, history.getValidShooting());
                stmt.setInt(23, history.getValidStealing());
                stmt.setInt(24, history.getValidTackling());
                stmt.setInt(25, history.getShooting());
                stmt.setInt(26, history.getStealing());
                stmt.setInt(27, history.getTackling());
                stmt.setInt(28, history.getTotalPoints());

                stmt.setInt(29, id);

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void sumStatsPoints(short value, int id, Connection ... con) {
        SqlUtils.sumShort("stats_points", value, TABLE, id, con);
    }

    public static void setStatusMessage(String value, int id, Connection ... con) {
        SqlUtils.setString("status_message", value, TABLE, id, con);
    }

    public static void addInventoryItem(Item item, int id, Connection ... con) {
        final String query = "INSERT INTO items VALUES(?,?,?,?,?,?,?,?,?,?)";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setInt(2, item.getInventoryId());
                stmt.setInt(3, item.getId());
                stmt.setInt(4, item.getExpiration().toInt());
                stmt.setInt(5, item.getBonusOne());
                stmt.setInt(6, item.getBonusTwo());
                stmt.setShort(7, item.getUsages());
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
        final String query = "UPDATE items SET " +
                "bonus_one=?, bonus_two=?, usages=?, timestamp_expire=?, selected=? " +
                "WHERE player_id=? AND inventory_id=? AND " + ITEM_ACTIVE + " LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, item.getBonusOne());
                stmt.setInt(2, item.getBonusTwo());
                stmt.setShort(3, item.getUsages());
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

    public static void removeInventoryItem(Item item, int id, Connection ... con) {
        final String query = "DELETE FROM items WHERE player_id = ? AND inventory_id = ?;";
        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setInt(2, item.getInventoryId());

                stmt.executeUpdate();
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void addInventoryTraining(Training training, int id, Connection ... con) {
        final String query = "INSERT INTO learns VALUES(?,?,?,?)";

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
        final String query = "INSERT INTO skills VALUES(?,?,?,?,?,?,?)";

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
        final String query = "UPDATE skills SET selection_index=?, timestamp_expire=? " +
                "WHERE player_id=? AND inventory_id=? AND " + PRODUCT_ACTIVE + " LIMIT 1;";

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
        final String query = "INSERT INTO ceres VALUES(?,?,?,?,?,?,?)";

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
        final String query = "UPDATE ceres SET selection_index=?, timestamp_expire=? " +
                "WHERE player_id=? AND inventory_id=? AND " + PRODUCT_ACTIVE + " LIMIT 1;";

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
        SqlUtils.setString("friends_list", value.toString(), TABLE, id, con);
    }

    public static void setIgnoredList(IgnoredList value, int id, Connection ... con) {
        SqlUtils.setString("ignored_list", value.toString(), TABLE, id, con);
    }
}
