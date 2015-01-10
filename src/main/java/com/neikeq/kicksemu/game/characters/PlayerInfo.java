package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.misc.friendship.FriendsList;
import com.neikeq.kicksemu.game.misc.ignored.IgnoredList;
import com.neikeq.kicksemu.storage.SqlUtils;

import java.util.Map;

public class PlayerInfo {

    private static final String table = "characters";

    // Sql getters

    public static int getOwner(int id) {
        return SqlUtils.getInt("owner", table, id);
    }

    public static String getName(int id) {
        return SqlUtils.getString("name", table, id);
    }

    public static int getClubId(int id) {
        return SqlUtils.getInt("club_id", table, id);
    }

    public static boolean isBlocked(int id) {
        return SqlUtils.getBoolean("blocked", table, id);
    }

    public static boolean isModerator(int id) {
        return SqlUtils.getBoolean("moderator", table, id);
    }

    public static boolean isVisible(int id) {
        return SqlUtils.getBoolean("visible", table, id);
    }

    public static short getLevel(int id) {
        return SqlUtils.getShort("level", table, id);
    }

    public static short getPosition(int id) {
        return SqlUtils.getShort("position", table, id);
    }

    public static short getCurrentQuest(int id) {
        return SqlUtils.getShort("quest_current", table, id);
    }

    public static short getRemainingQuestMatches(int id) {
        return SqlUtils.getShort("quest_matches_left", table, id);
    }

    public static byte getTutorialDribbling(int id) {
        return SqlUtils.getByte("tutorial_dribbling", table, id);
    }

    public static byte getTutorialPassing(int id) {
        return SqlUtils.getByte("tutorial_passing", table, id);
    }

    public static byte getTutorialShooting(int id) {
        return SqlUtils.getByte("tutorial_shooting", table, id);
    }

    public static byte getTutorialDefense(int id) {
        return SqlUtils.getByte("tutorial_defense", table, id);
    }

    public static boolean getReceivedReward(int id) {
        return SqlUtils.getBoolean("received_reward", table, id);
    }

    public static int getExperience(int id) {
        return SqlUtils.getInt("experience", table, id);
    }

    public static int getPoints(int id) {
        return SqlUtils.getInt("points", table, id);
    }

    public static short getTicketsKash(int id) {
        return SqlUtils.getShort("tickets_kash", table, id);
    }

    public static short getTicketsPoints(int id) {
        return SqlUtils.getShort("tickets_points", table, id);
    }

    public static short getAnimation(int id) {
        return SqlUtils.getShort("animation", table, id);
    }

    public static short getFace(int id) {
        return SqlUtils.getShort("face", table, id);
    }

    public static int getDefaultHead(int id) {
        return SqlUtils.getInt("default_head", table, id);
    }

    public static int getDefaultShirts(int id) {
        return SqlUtils.getInt("default_shirts", table, id);
    }

    public static int getDefaultPants(int id) {
        return SqlUtils.getInt("default_pants", table, id);
    }

    public static int getDefaultShoes(int id) {
        return SqlUtils.getInt("default_shoes", table, id);
    }

    public static int getItemHead(int id) {
        return SqlUtils.getInt("item_head", table, id);
    }

    public static int getItemGlasses(int id) {
        return SqlUtils.getInt("item_glasses", table, id);
    }

    public static int getItemShirts(int id) {
        return SqlUtils.getInt("item_shirts", table, id);
    }

    public static int getItemPants(int id) {
        return SqlUtils.getInt("item_pants", table, id);
    }

    public static int getItemGlove(int id) {
        return SqlUtils.getInt("item_glove", table, id);
    }

    public static int getItemShoes(int id) {
        return SqlUtils.getInt("item_shoes", table, id);
    }

    public static int getItemSocks(int id) {
        return SqlUtils.getInt("item_socks", table, id);
    }

    public static int getItemWrist(int id) {
        return SqlUtils.getInt("item_wrist", table, id);
    }

    public static int getItemArm(int id) {
        return SqlUtils.getInt("item_arm", table, id);
    }

    public static int getItemKnee(int id) {
        return SqlUtils.getInt("item_knee", table, id);
    }

    public static int getItemEar(int id) {
        return SqlUtils.getInt("item_ear", table, id);
    }

    public static int getItemNeck(int id) {
        return SqlUtils.getInt("item_neck", table, id);
    }

    public static int getItemMask(int id) {
        return SqlUtils.getInt("item_mask", table, id);
    }

    public static int getItemMuffler(int id) {
        return SqlUtils.getInt("item_muffler", table, id);
    }

    public static int getItemPackage(int id) {
        return SqlUtils.getInt("item_package", table, id);
    }

    public static short getStatsPoints(int id) {
        return SqlUtils.getShort("stats_points", table, id);
    }

    // Stats

    public static short getStatsRunning(int id) {
        return SqlUtils.getShort("stats_running", table, id);
    }

    public static short getStatsEndurance(int id) {
        return SqlUtils.getShort("stats_endurance", table, id);
    }

    public static short getStatsAgility(int id) {
        return SqlUtils.getShort("stats_agility", table, id);
    }

    public static short getStatsBallControl(int id) {
        return SqlUtils.getShort("stats_ball_control", table, id);
    }

    public static short getStatsDribbling(int id) {
        return SqlUtils.getShort("stats_dribbling", table, id);
    }

    public static short getStatsStealing(int id) {
        return SqlUtils.getShort("stats_stealing", table, id);
    }

    public static short getStatsTackling(int id) {
        return SqlUtils.getShort("stats_tackling", table, id);
    }

    public static short getStatsHeading(int id) {
        return SqlUtils.getShort("stats_heading", table, id);
    }

    public static short getStatsShortShots(int id) {
        return SqlUtils.getShort("stats_short_shots", table, id);
    }

    public static short getStatsLongShots(int id) {
        return SqlUtils.getShort("stats_long_shots", table, id);
    }

    public static short getStatsCrossing(int id) {
        return SqlUtils.getShort("stats_crossing", table, id);
    }

    public static short getStatsShortPasses(int id) {
        return SqlUtils.getShort("stats_short_passes", table, id);
    }

    public static short getStatsLongPasses(int id) {
        return SqlUtils.getShort("stats_long_passes", table, id);
    }

    public static short getStatsMarking(int id) {
        return SqlUtils.getShort("stats_marking", table, id);
    }

    public static short getStatsGoalkeeping(int id) {
        return SqlUtils.getShort("stats_goalkeeping", table, id);
    }

    public static short getStatsPunching(int id) {
        return SqlUtils.getShort("stats_punching", table, id);
    }

    public static short getStatsDefense(int id) {
        return SqlUtils.getShort("stats_defense", table, id);
    }

    // Stats Training

    public static short getTrainingStatsRunning(int id) {
        return SqlUtils.getShort("training_running", table, id);
    }

    public static short getTrainingStatsEndurance(int id) {
        return SqlUtils.getShort("training_endurance", table, id);
    }

    public static short getTrainingStatsAgility(int id) {
        return SqlUtils.getShort("training_agility", table, id);
    }

    public static short getTrainingStatsBallControl(int id) {
        return SqlUtils.getShort("training_ball_control", table, id);
    }

    public static short getTrainingStatsDribbling(int id) {
        return SqlUtils.getShort("training_dribbling", table, id);
    }

    public static short getTrainingStatsStealing(int id) {
        return SqlUtils.getShort("training_stealing", table, id);
    }

    public static short getTrainingStatsTackling(int id) {
        return SqlUtils.getShort("training_tackling", table, id);
    }

    public static short getTrainingStatsHeading(int id) {
        return SqlUtils.getShort("training_heading", table, id);
    }

    public static short getTrainingStatsShortShots(int id) {
        return SqlUtils.getShort("training_short_shots", table, id);
    }

    public static short getTrainingStatsLongShots(int id) {
        return SqlUtils.getShort("training_long_shots", table, id);
    }

    public static short getTrainingStatsCrossing(int id) {
        return SqlUtils.getShort("training_crossing", table, id);
    }

    public static short getTrainingStatsShortPasses(int id) {
        return SqlUtils.getShort("training_short_passes", table, id);
    }

    public static short getTrainingStatsLongPasses(int id) {
        return SqlUtils.getShort("training_long_passes", table, id);
    }

    public static short getTrainingStatsMarking(int id) {
        return SqlUtils.getShort("training_marking", table, id);
    }

    public static short getTrainingStatsGoalkeeping(int id) {
        return SqlUtils.getShort("training_goalkeeping", table, id);
    }

    public static short getTrainingStatsPunching(int id) {
        return SqlUtils.getShort("training_punching", table, id);
    }

    public static short getTrainingStatsDefense(int id) {
        return SqlUtils.getShort("training_defense", table, id);
    }

    // Stats Bonus

    public static short getBonusStatsRunning(int id) {
        return SqlUtils.getShort("bonus_running", table, id);
    }

    public static short getBonusStatsEndurance(int id) {
        return SqlUtils.getShort("bonus_endurance", table, id);
    }

    public static short getBonusStatsAgility(int id) {
        return SqlUtils.getShort("bonus_agility", table, id);
    }

    public static short getBonusStatsBallControl(int id) {
        return SqlUtils.getShort("bonus_ball_control", table, id);
    }

    public static short getBonusStatsDribbling(int id) {
        return SqlUtils.getShort("bonus_dribbling", table, id);
    }

    public static short getBonusStatsStealing(int id) {
        return SqlUtils.getShort("bonus_stealing", table, id);
    }

    public static short getBonusStatsTackling(int id) {
        return SqlUtils.getShort("bonus_tackling", table, id);
    }

    public static short getBonusStatsHeading(int id) {
        return SqlUtils.getShort("bonus_heading", table, id);
    }

    public static short getBonusStatsShortShots(int id) {
        return SqlUtils.getShort("bonus_short_shots", table, id);
    }

    public static short getBonusStatsLongShots(int id) {
        return SqlUtils.getShort("bonus_long_shots", table, id);
    }

    public static short getBonusStatsCrossing(int id) {
        return SqlUtils.getShort("bonus_crossing", table, id);
    }

    public static short getBonusStatsShortPasses(int id) {
        return SqlUtils.getShort("bonus_short_passes", table, id);
    }

    public static short getBonusStatsLongPasses(int id) {
        return SqlUtils.getShort("bonus_long_passes", table, id);
    }

    public static short getBonusStatsMarking(int id) {
        return SqlUtils.getShort("bonus_marking", table, id);
    }

    public static short getBonusStatsGoalkeeping(int id) {
        return SqlUtils.getShort("bonus_goalkeeping", table, id);
    }

    public static short getBonusStatsPunching(int id) {
        return SqlUtils.getShort("bonus_punching", table, id);
    }

    public static short getBonusStatsDefense(int id) {
        return SqlUtils.getShort("bonus_defense", table, id);
    }

    // History

    public static int getHistoryMatches(int id) {
        return SqlUtils.getInt("history_matches", table, id);
    }

    public static int getHistoryWins(int id) {
        return SqlUtils.getInt("history_wins", table, id);
    }

    public static int getHistoryDraws(int id) {
        return SqlUtils.getInt("history_draws", table, id);
    }

    public static int getHistoryMom(int id) {
        return SqlUtils.getInt("history_MOM", table, id);
    }

    public static int getHistoryValidGoals(int id) {
        return SqlUtils.getInt("history_valid_goals", table, id);
    }

    public static int getHistoryValidAssists(int id) {
        return SqlUtils.getInt("history_valid_assists", table, id);
    }

    public static int getHistoryValidInterception(int id) {
        return SqlUtils.getInt("history_valid_interception", table, id);
    }

    public static int getHistoryValidShooting(int id) {
        return SqlUtils.getInt("history_valid_shooting", table, id);
    }

    public static int getHistoryValidStealing(int id) {
        return SqlUtils.getInt("history_valid_stealing", table, id);
    }

    public static int getHistoryValidTackling(int id) {
        return SqlUtils.getInt("history_valid_tackling", table, id);
    }

    public static int getHistoryShooting(int id) {
        return SqlUtils.getInt("history_shooting", table, id);
    }

    public static int getHistoryStealing(int id) {
        return SqlUtils.getInt("history_stealing", table, id);
    }

    public static int getHistoryTackling(int id) {
        return SqlUtils.getInt("history_tackling", table, id);
    }

    public static int getHistoryTotalPoints(int id) {
        return SqlUtils.getInt("history_total_points", table, id);
    }

    // History Last Month

    public static int getHistoryMonthMatches(int id) {
        return SqlUtils.getInt("history_month_matches", table, id);
    }

    public static int getHistoryMonthWins(int id) {
        return SqlUtils.getInt("history_month_wins", table, id);
    }

    public static int getHistoryMonthDraws(int id) {
        return SqlUtils.getInt("history_month_draws", table, id);
    }

    public static int getHistoryMonthMom(int id) {
        return SqlUtils.getInt("history_month_MOM", table, id);
    }

    public static int getHistoryMonthValidGoals(int id) {
        return SqlUtils.getInt("history_month_valid_goals", table, id);
    }

    public static int getHistoryMonthValidAssists(int id) {
        return SqlUtils.getInt("history_month_valid_assists", table, id);
    }

    public static int getHistoryMonthValidInterception(int id) {
        return SqlUtils.getInt("history_month_valid_interception", table, id);
    }

    public static int getHistoryMonthValidShooting(int id) {
        return SqlUtils.getInt("history_month_valid_shooting", table, id);
    }

    public static int getHistoryMonthValidStealing(int id) {
        return SqlUtils.getInt("history_month_valid_stealing", table, id);
    }

    public static int getHistoryMonthValidTackling(int id) {
        return SqlUtils.getInt("history_month_valid_tackling", table, id);
    }

    public static int getHistoryMonthShooting(int id) {
        return SqlUtils.getInt("history_month_shooting", table, id);
    }

    public static int getHistoryMonthStealing(int id) {
        return SqlUtils.getInt("history_month_stealing", table, id);
    }

    public static int getHistoryMonthTackling(int id) {
        return SqlUtils.getInt("history_month_tackling", table, id);
    }

    public static int getHistoryMonthTotalPoints(int id) {
        return SqlUtils.getInt("history_month_total_points", table, id);
    }

    // Ranking

    public static short getRankingMatches(int id) {
        return SqlUtils.getShort("ranking_matches", table, id);
    }

    public static short getRankingWins(int id) {
        return SqlUtils.getShort("ranking_wins", table, id);
    }

    public static short getRankingPoints(int id) {
        return SqlUtils.getShort("ranking_points", table, id);
    }

    public static short getRankingMom(int id) {
        return SqlUtils.getShort("ranking_MOM", table, id);
    }

    public static short getRankingValidGoals(int id) {
        return SqlUtils.getShort("ranking_valid_goals", table, id);
    }

    public static short getRankingValidAssists(int id) {
        return SqlUtils.getShort("ranking_valid_assists", table, id);
    }

    public static short getRankingValidInterception(int id) {
        return SqlUtils.getShort("ranking_valid_interception", table, id);
    }

    public static short getRankingValidShooting(int id) {
        return SqlUtils.getShort("ranking_valid_shooting", table, id);
    }

    public static short getRankingValidStealing(int id) {
        return SqlUtils.getShort("ranking_valid_stealing", table, id);
    }

    public static short getRankingValidTackling(int id) {
        return SqlUtils.getShort("ranking_valid_tackling", table, id);
    }

    public static short getRankingAvgGoals(int id) {
        return SqlUtils.getShort("ranking_avg_goals", table, id);
    }

    public static short getRankingAvgAssists(int id) {
        return SqlUtils.getShort("ranking_avg_assists", table, id);
    }

    public static short getRankingAvgInterception(int id) {
        return SqlUtils.getShort("ranking_avg_interception", table, id);
    }

    public static short getRankingAvgShooting(int id) {
        return SqlUtils.getShort("ranking_avg_shooting", table, id);
    }

    public static short getRankingAvgStealing(int id) {
        return SqlUtils.getShort("ranking_avg_stealing", table, id);
    }

    public static short getRankingAvgTackling(int id) {
        return SqlUtils.getShort("ranking_avg_tackling", table, id);
    }

    public static short getRankingAvgVotePoints(int id) {
        return SqlUtils.getShort("ranking_avg_vote_points", table, id);
    }

    public static short getRankingShooting(int id) {
        return SqlUtils.getShort("ranking_shooting", table, id);
    }

    public static short getRankingStealing(int id) {
        return SqlUtils.getShort("ranking_stealing", table, id);
    }

    public static short getRankingTackling(int id) {
        return SqlUtils.getShort("ranking_tackling", table, id);
    }

    public static short getRankingTotalPoints(int id) {
        return SqlUtils.getShort("ranking_total_points", table, id);
    }

    // Ranking Last Month

    public static short getRankingMonthMatches(int id) {
        return SqlUtils.getShort("ranking_month_matches", table, id);
    }

    public static short getRankingMonthWins(int id) {
        return SqlUtils.getShort("ranking_month_wins", table, id);
    }

    public static short getRankingMonthPoints(int id) {
        return SqlUtils.getShort("ranking_month_points", table, id);
    }

    public static short getRankingMonthMom(int id) {
        return SqlUtils.getShort("ranking_month_MOM", table, id);
    }

    public static short getRankingMonthValidGoals(int id) {
        return SqlUtils.getShort("ranking_month_valid_goals", table, id);
    }

    public static short getRankingMonthValidAssists(int id) {
        return SqlUtils.getShort("ranking_month_valid_assists", table, id);
    }

    public static short getRankingMonthValidInterception(int id) {
        return SqlUtils.getShort("ranking_month_valid_interception", table, id);
    }

    public static short getRankingMonthValidShooting(int id) {
        return SqlUtils.getShort("ranking_month_valid_shooting", table, id);
    }

    public static short getRankingMonthValidStealing(int id) {
        return SqlUtils.getShort("ranking_month_valid_stealing", table, id);
    }

    public static short getRankingMonthValidTackling(int id) {
        return SqlUtils.getShort("ranking_month_valid_tackling", table, id);
    }

    public static short getRankingMonthAvgGoals(int id) {
        return SqlUtils.getShort("ranking_month_avg_goals", table, id);
    }

    public static short getRankingMonthAvgAssists(int id) {
        return SqlUtils.getShort("ranking_month_avg_assists", table, id);
    }

    public static short getRankingMonthAvgInterception(int id) {
        return SqlUtils.getShort("ranking_month_avg_interception", table, id);
    }

    public static short getRankingMonthAvgShooting(int id) {
        return SqlUtils.getShort("ranking_month_avg_shooting", table, id);
    }

    public static short getRankingMonthAvgStealing(int id) {
        return SqlUtils.getShort("ranking_month_avg_stealing", table, id);
    }

    public static short getRankingMonthAvgTackling(int id) {
        return SqlUtils.getShort("ranking_month_avg_tackling", table, id);
    }

    public static short getRankingMonthAvgVotePoints(int id) {
        return SqlUtils.getShort("ranking_month_avg_vote_points", table, id);
    }

    public static short getRankingMonthShooting(int id) {
        return SqlUtils.getShort("ranking_month_shooting", table, id);
    }

    public static short getRankingMonthStealing(int id) {
        return SqlUtils.getShort("ranking_month_stealing", table, id);
    }

    public static short getRankingMonthTackling(int id) {
        return SqlUtils.getShort("ranking_month_tackling", table, id);
    }

    public static short getRankingMonthTotalPoints(int id) {
        return SqlUtils.getShort("ranking_month_total_points", table, id);
    }

    // Others

    public static String getStatusMessage(int id) {
        return SqlUtils.getString("status_message", table, id);
    }

    public static Map<Integer, Item> getInventoryItems(int id) {
        return Item.mapFromString(SqlUtils.getString("inventory_items", table, id));
    }

    public static Map<Integer, Training> getInventoryTraining(int id) {
        return Training.mapFromString(SqlUtils.getString("inventory_training", table, id));
    }

    public static String getInventorySkillsString(int id) {
        return SqlUtils.getString("inventory_skills", table, id);
    }

    public static Map<Integer, Skill> getInventorySkills(int id) {
        return Skill.mapFromString(SqlUtils.getString("inventory_skills", table, id), id);
    }

    public static String getInventoryCelebrationString(int id) {
        return SqlUtils.getString("inventory_celebration", table, id);
    }

    public static Map<Integer, Celebration> getInventoryCelebration(int id) {
        return Celebration.mapFromString(SqlUtils.getString("inventory_celebration", table, id));
    }

    public static FriendsList getFriendsList(int id) {
        return FriendsList.fromString(SqlUtils.getString("friends_list", table, id), id);
    }

    public static IgnoredList getIgnoredList(int id) {
        return IgnoredList.fromString(SqlUtils.getString("ignored_list", table, id), id);
    }

    // Sql setters

    public static void setOwner(int value, int id) {
        SqlUtils.setInt("owner", value, table, id);
    }

    public static void setName(String value, int id) {
        SqlUtils.setString("name", value, table, id);
    }

    public static void setBlocked(boolean value, int id) {
        SqlUtils.setBoolean("blocked", value, table, id);
    }

    public static void setVisible(boolean value, int id) {
        SqlUtils.setBoolean("visible", value, table, id);
    }

    public static void setLevel(short value, int id) {
        SqlUtils.setShort("level", value, table, id);
    }

    public static void setPosition(short value, int id) {
        SqlUtils.setShort("position", value, table, id);
    }

    public static void setClubId(int value, int id) {
        SqlUtils.setInt("club_id", value, table, id);
    }

    public static void setCurrentQuest(short value, int id) {
        SqlUtils.setShort("quest_current", value, table, id);
    }

    public static void setRemainingQuestMatches(short value, int id) {
        SqlUtils.setShort("quest_matches_left", value, table, id);
    }

    public static void setTutorialDribbling(byte value, int id) {
        SqlUtils.setByte("tutorial_dribbling", value, table, id);
    }

    public static void setTutorialPassing(byte value, int id) {
        SqlUtils.setByte("tutorial_passing", value, table, id);
    }

    public static void setTutorialShooting(byte value, int id) {
        SqlUtils.setByte("tutorial_shooting", value, table, id);
    }

    public static void setTutorialDefense(byte value, int id) {
        SqlUtils.setByte("tutorial_defense", value, table, id);
    }

    public static void setReceivedReward(boolean value, int id) {
        SqlUtils.setBoolean("received_reward", value, table, id);
    }

    public static void setExperience(int value, int id) {
        SqlUtils.sumInt("experience", value, table, id);
    }

    public static void setPoints(int value, int id) {
        SqlUtils.sumInt("points", value, table, id);
    }

    public static void setTicketsKash(short value, int id) {
        SqlUtils.setShort("tickets_kash", value, table, id);
    }

    public static void setTicketsPoints(short value, int id) {
        SqlUtils.setShort("tickets_points", value, table, id);
    }

    public static void setAnimation(short value, int id) {
        SqlUtils.setShort("animation", value, table, id);
    }

    public static void setFace(short value, int id) {
        SqlUtils.setShort("face", value, table, id);
    }

    public static void setDefaultHead(int value, int id) {
        SqlUtils.setInt("default_head", value, table, id);
    }

    public static void setDefaultShirts(int value, int id) {
        SqlUtils.setInt("default_shirts", value, table, id);
    }

    public static void setDefaultPants(int value, int id) {
        SqlUtils.setInt("default_pants", value, table, id);
    }

    public static void setDefaultShoes(int value, int id) {
        SqlUtils.setInt("default_shoes", value, table, id);
    }

    public static void setItemHead(int value, int id) {
        SqlUtils.setInt("item_head", value, table, id);
    }

    public static void setItemGlasses(int value, int id) {
        SqlUtils.setInt("item_glasses", value, table, id);
    }

    public static void setItemShirts(int value, int id) {
        SqlUtils.setInt("item_shirts", value, table, id);
    }

    public static void setItemPants(int value, int id) {
        SqlUtils.setInt("item_pants", value, table, id);
    }

    public static void setItemGlove(int value, int id) {
        SqlUtils.setInt("item_glove", value, table, id);
    }

    public static void setItemShoes(int value, int id) {
        SqlUtils.setInt("item_shoes", value, table, id);
    }

    public static void setItemSocks(int value, int id) {
        SqlUtils.setInt("item_socks", value, table, id);
    }

    public static void setItemWrist(int value, int id) {
        SqlUtils.setInt("item_wrist", value, table, id);
    }

    public static void setItemArm(int value, int id) {
        SqlUtils.setInt("item_arm", value, table, id);
    }

    public static void setItemKnee(int value, int id) {
        SqlUtils.setInt("item_knee", value, table, id);
    }

    public static void setItemEar(int value, int id) {
        SqlUtils.setInt("item_ear", value, table, id);
    }

    public static void setItemNeck(int value, int id) {
        SqlUtils.setInt("item_neck", value, table, id);
    }

    public static void setItemMask(int value, int id) {
        SqlUtils.setInt("item_mask", value, table, id);
    }

    public static void setItemMuffler(int value, int id) {
        SqlUtils.setInt("item_muffler", value, table, id);
    }

    public static void setItemPackage(int value, int id) {
        SqlUtils.setInt("item_package", value, table, id);
    }

    public static void setStatsPoints(short value, int id) {
        SqlUtils.sumShort("stats_points", value, table, id);
    }

    public static short setStatsRunning(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsRunning(id), value);
        SqlUtils.sumShort("stats_running", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsEndurance(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsEndurance(id), value);
        SqlUtils.sumShort("stats_endurance", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsAgility(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsAgility(id), value);
        SqlUtils.sumShort("stats_agility", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsBallControl(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsBallControl(id), value);
        SqlUtils.sumShort("stats_ball_control", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsDribbling(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsDribbling(id), value);
        SqlUtils.sumShort("stats_dribbling", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsStealing(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsStealing(id), value);
        SqlUtils.sumShort("stats_stealing", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsTackling(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsTackling(id), value);
        SqlUtils.sumShort("stats_tackling", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsHeading(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsHeading(id), value);
        SqlUtils.sumShort("stats_heading", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsShortShots(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsShortShots(id), value);
        SqlUtils.sumShort("stats_short_shots", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsLongShots(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsLongShots(id), value);
        SqlUtils.sumShort("stats_long_shots", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsCrossing(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsCrossing(id), value);
        SqlUtils.sumShort("stats_crossing", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsShortPasses(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsShortPasses(id), value);
        SqlUtils.sumShort("stats_short_passes", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsLongPasses(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsLongPasses(id), value);
        SqlUtils.sumShort("stats_long_passes", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsMarking(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsMarking(id), value);
        SqlUtils.sumShort("stats_marking", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsGoalkeeping(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsGoalkeeping(id), value);
        SqlUtils.sumShort("stats_goalkeeping", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsPunching(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsPunching(id), value);
        SqlUtils.sumShort("stats_punching", add, table, id);
        return (short)(value - add);
    }

    public static short setStatsDefense(int value, int id) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsDefense(id), value);
        SqlUtils.sumShort("stats_defense", add, table, id);
        return (short)(value - add);
    }

    public static void setStatusMessage(String value, int id) {
        SqlUtils.setString("status_message", value, table, id);
    }

    public static void setInventoryItems(String value, int id) {
        SqlUtils.setString("inventory_items", value, table, id);
    }

    public static void setInventoryTraining(String value, int id) {
        SqlUtils.setString("inventory_training", value, table, id);
    }

    public static void setInventorySkills(Map<Integer, Skill> value, int id) {
        SqlUtils.setString("inventory_skills", Skill.mapToString(value), table, id);
    }

    public static void setInventoryCelebration(String value, int id) {
        SqlUtils.setString("inventory_celebration", value, table, id);
    }

    public static void setFriendsList(FriendsList value, int id) {
        SqlUtils.setString("friends_list", value.toString(), table, id);
    }

    public static void setIgnoredList(IgnoredList value, int id) {
        SqlUtils.setString("ignored_list", value.toString(), table, id);
    }
}