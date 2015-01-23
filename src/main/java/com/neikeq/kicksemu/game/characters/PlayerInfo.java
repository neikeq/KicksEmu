package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.misc.friendship.FriendsList;
import com.neikeq.kicksemu.game.misc.ignored.IgnoredList;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.storage.SqlUtils;

import java.sql.Connection;
import java.util.Map;

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

    public static int getItemHead(int id, Connection ... con) {
        return SqlUtils.getInt("item_head", table, id, con);
    }

    public static int getItemGlasses(int id, Connection ... con) {
        return SqlUtils.getInt("item_glasses", table, id, con);
    }

    public static int getItemShirts(int id, Connection ... con) {
        return SqlUtils.getInt("item_shirts", table, id, con);
    }

    public static int getItemPants(int id, Connection ... con) {
        return SqlUtils.getInt("item_pants", table, id, con);
    }

    public static int getItemGlove(int id, Connection ... con) {
        return SqlUtils.getInt("item_glove", table, id, con);
    }

    public static int getItemShoes(int id, Connection ... con) {
        return SqlUtils.getInt("item_shoes", table, id, con);
    }

    public static int getItemSocks(int id, Connection ... con) {
        return SqlUtils.getInt("item_socks", table, id, con);
    }

    public static int getItemWrist(int id, Connection ... con) {
        return SqlUtils.getInt("item_wrist", table, id, con);
    }

    public static int getItemArm(int id, Connection ... con) {
        return SqlUtils.getInt("item_arm", table, id, con);
    }

    public static int getItemKnee(int id, Connection ... con) {
        return SqlUtils.getInt("item_knee", table, id, con);
    }

    public static int getItemEar(int id, Connection ... con) {
        return SqlUtils.getInt("item_ear", table, id, con);
    }

    public static int getItemNeck(int id, Connection ... con) {
        return SqlUtils.getInt("item_neck", table, id, con);
    }

    public static int getItemMask(int id, Connection ... con) {
        return SqlUtils.getInt("item_mask", table, id, con);
    }

    public static int getItemMuffler(int id, Connection ... con) {
        return SqlUtils.getInt("item_muffler", table, id, con);
    }

    public static int getItemPackage(int id, Connection ... con) {
        return SqlUtils.getInt("item_package", table, id, con);
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

    // Stats Training

    public static short getTrainingStatsRunning(int id, Connection ... con) {
        return SqlUtils.getShort("training_running", table, id, con);
    }

    public static short getTrainingStatsEndurance(int id, Connection ... con) {
        return SqlUtils.getShort("training_endurance", table, id, con);
    }

    public static short getTrainingStatsAgility(int id, Connection ... con) {
        return SqlUtils.getShort("training_agility", table, id, con);
    }

    public static short getTrainingStatsBallControl(int id, Connection ... con) {
        return SqlUtils.getShort("training_ball_control", table, id, con);
    }

    public static short getTrainingStatsDribbling(int id, Connection ... con) {
        return SqlUtils.getShort("training_dribbling", table, id, con);
    }

    public static short getTrainingStatsStealing(int id, Connection ... con) {
        return SqlUtils.getShort("training_stealing", table, id, con);
    }

    public static short getTrainingStatsTackling(int id, Connection ... con) {
        return SqlUtils.getShort("training_tackling", table, id, con);
    }

    public static short getTrainingStatsHeading(int id, Connection ... con) {
        return SqlUtils.getShort("training_heading", table, id, con);
    }

    public static short getTrainingStatsShortShots(int id, Connection ... con) {
        return SqlUtils.getShort("training_short_shots", table, id, con);
    }

    public static short getTrainingStatsLongShots(int id, Connection ... con) {
        return SqlUtils.getShort("training_long_shots", table, id, con);
    }

    public static short getTrainingStatsCrossing(int id, Connection ... con) {
        return SqlUtils.getShort("training_crossing", table, id, con);
    }

    public static short getTrainingStatsShortPasses(int id, Connection ... con) {
        return SqlUtils.getShort("training_short_passes", table, id, con);
    }

    public static short getTrainingStatsLongPasses(int id, Connection ... con) {
        return SqlUtils.getShort("training_long_passes", table, id, con);
    }

    public static short getTrainingStatsMarking(int id, Connection ... con) {
        return SqlUtils.getShort("training_marking", table, id, con);
    }

    public static short getTrainingStatsGoalkeeping(int id, Connection ... con) {
        return SqlUtils.getShort("training_goalkeeping", table, id, con);
    }

    public static short getTrainingStatsPunching(int id, Connection ... con) {
        return SqlUtils.getShort("training_punching", table, id, con);
    }

    public static short getTrainingStatsDefense(int id, Connection ... con) {
        return SqlUtils.getShort("training_defense", table, id, con);
    }

    // Stats Bonus

    public static short getBonusStatsRunning(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_running", table, id, con);
    }

    public static short getBonusStatsEndurance(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_endurance", table, id, con);
    }

    public static short getBonusStatsAgility(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_agility", table, id, con);
    }

    public static short getBonusStatsBallControl(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_ball_control", table, id, con);
    }

    public static short getBonusStatsDribbling(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_dribbling", table, id, con);
    }

    public static short getBonusStatsStealing(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_stealing", table, id, con);
    }

    public static short getBonusStatsTackling(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_tackling", table, id, con);
    }

    public static short getBonusStatsHeading(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_heading", table, id, con);
    }

    public static short getBonusStatsShortShots(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_short_shots", table, id, con);
    }

    public static short getBonusStatsLongShots(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_long_shots", table, id, con);
    }

    public static short getBonusStatsCrossing(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_crossing", table, id, con);
    }

    public static short getBonusStatsShortPasses(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_short_passes", table, id, con);
    }

    public static short getBonusStatsLongPasses(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_long_passes", table, id, con);
    }

    public static short getBonusStatsMarking(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_marking", table, id, con);
    }

    public static short getBonusStatsGoalkeeping(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_goalkeeping", table, id, con);
    }

    public static short getBonusStatsPunching(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_punching", table, id, con);
    }

    public static short getBonusStatsDefense(int id, Connection ... con) {
        return SqlUtils.getShort("bonus_defense", table, id, con);
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

    // Ranking

    public static short getRankingMatches(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_matches", table, id, con);
    }

    public static short getRankingWins(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_wins", table, id, con);
    }

    public static short getRankingPoints(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_points", table, id, con);
    }

    public static short getRankingMom(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_MOM", table, id, con);
    }

    public static short getRankingValidGoals(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_valid_goals", table, id, con);
    }

    public static short getRankingValidAssists(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_valid_assists", table, id, con);
    }

    public static short getRankingValidInterception(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_valid_interception", table, id, con);
    }

    public static short getRankingValidShooting(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_valid_shooting", table, id, con);
    }

    public static short getRankingValidStealing(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_valid_stealing", table, id, con);
    }

    public static short getRankingValidTackling(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_valid_tackling", table, id, con);
    }

    public static short getRankingAvgGoals(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_avg_goals", table, id, con);
    }

    public static short getRankingAvgAssists(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_avg_assists", table, id, con);
    }

    public static short getRankingAvgInterception(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_avg_interception", table, id, con);
    }

    public static short getRankingAvgShooting(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_avg_shooting", table, id, con);
    }

    public static short getRankingAvgStealing(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_avg_stealing", table, id, con);
    }

    public static short getRankingAvgTackling(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_avg_tackling", table, id, con);
    }

    public static short getRankingAvgVotePoints(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_avg_vote_points", table, id, con);
    }

    public static short getRankingShooting(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_shooting", table, id, con);
    }

    public static short getRankingStealing(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_stealing", table, id, con);
    }

    public static short getRankingTackling(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_tackling", table, id, con);
    }

    public static short getRankingTotalPoints(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_total_points", table, id, con);
    }

    // Ranking Last Month

    public static short getRankingMonthMatches(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_matches", table, id, con);
    }

    public static short getRankingMonthWins(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_wins", table, id, con);
    }

    public static short getRankingMonthPoints(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_points", table, id, con);
    }

    public static short getRankingMonthMom(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_MOM", table, id, con);
    }

    public static short getRankingMonthValidGoals(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_valid_goals", table, id, con);
    }

    public static short getRankingMonthValidAssists(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_valid_assists", table, id, con);
    }

    public static short getRankingMonthValidInterception(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_valid_interception", table, id, con);
    }

    public static short getRankingMonthValidShooting(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_valid_shooting", table, id, con);
    }

    public static short getRankingMonthValidStealing(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_valid_stealing", table, id, con);
    }

    public static short getRankingMonthValidTackling(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_valid_tackling", table, id, con);
    }

    public static short getRankingMonthAvgGoals(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_avg_goals", table, id, con);
    }

    public static short getRankingMonthAvgAssists(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_avg_assists", table, id, con);
    }

    public static short getRankingMonthAvgInterception(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_avg_interception", table, id, con);
    }

    public static short getRankingMonthAvgShooting(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_avg_shooting", table, id, con);
    }

    public static short getRankingMonthAvgStealing(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_avg_stealing", table, id, con);
    }

    public static short getRankingMonthAvgTackling(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_avg_tackling", table, id, con);
    }

    public static short getRankingMonthAvgVotePoints(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_avg_vote_points", table, id, con);
    }

    public static short getRankingMonthShooting(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_shooting", table, id, con);
    }

    public static short getRankingMonthStealing(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_stealing", table, id, con);
    }

    public static short getRankingMonthTackling(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_tackling", table, id, con);
    }

    public static short getRankingMonthTotalPoints(int id, Connection ... con) {
        return SqlUtils.getShort("ranking_month_total_points", table, id, con);
    }

    // Others

    public static String getStatusMessage(int id, Connection ... con) {
        return SqlUtils.getString("status_message", table, id, con);
    }

    public static Map<Integer, Item> getInventoryItems(int id, Connection ... con) {
        return Item.mapFromString(SqlUtils.getString("inventory_items", table, id, con), id);
    }

    public static Map<Integer, Training> getInventoryTraining(int id, Connection ... con) {
        return Training.mapFromString(SqlUtils.getString("inventory_training", table, id, con));
    }

    public static Map<Integer, Skill> getInventorySkills(int id, Connection ... con) {
        return Skill.mapFromString(SqlUtils.getString("inventory_skills", table, id, con), id);
    }

    public static Map<Integer, Celebration> getInventoryCelebration(int id, Connection ... con) {
        return Celebration.mapFromString(SqlUtils.getString("inventory_celebration", table, id, con), id);
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

    public static void setExperience(int value, int id, Connection ... con) {
        SqlUtils.sumInt("experience", value, table, id, con);
    }

    public static void setPoints(int value, int id, Connection ... con) {
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

    public static void setItemHead(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_head", value, table, id, con);
    }

    public static void setItemGlasses(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_glasses", value, table, id, con);
    }

    public static void setItemShirts(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_shirts", value, table, id, con);
    }

    public static void setItemPants(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_pants", value, table, id, con);
    }

    public static void setItemGlove(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_glove", value, table, id, con);
    }

    public static void setItemShoes(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_shoes", value, table, id, con);
    }

    public static void setItemSocks(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_socks", value, table, id, con);
    }

    public static void setItemWrist(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_wrist", value, table, id, con);
    }

    public static void setItemArm(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_arm", value, table, id, con);
    }

    public static void setItemKnee(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_knee", value, table, id, con);
    }

    public static void setItemEar(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_ear", value, table, id, con);
    }

    public static void setItemNeck(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_neck", value, table, id, con);
    }

    public static void setItemMask(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_mask", value, table, id, con);
    }

    public static void setItemMuffler(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_muffler", value, table, id, con);
    }

    public static void setItemPackage(int value, int id, Connection ... con) {
        SqlUtils.setInt("item_package", value, table, id, con);
    }

    public static void setStatsPoints(short value, int id, Connection ... con) {
        SqlUtils.sumShort("stats_points", value, table, id, con);
    }

    public static short setStatsRunning(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsRunning(id, con), value);
        SqlUtils.sumShort("stats_running", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsEndurance(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsEndurance(id, con), value);
        SqlUtils.sumShort("stats_endurance", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsAgility(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsAgility(id, con), value);
        SqlUtils.sumShort("stats_agility", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsBallControl(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsBallControl(id, con), value);
        SqlUtils.sumShort("stats_ball_control", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsDribbling(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsDribbling(id, con), value);
        SqlUtils.sumShort("stats_dribbling", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsStealing(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsStealing(id, con), value);
        SqlUtils.sumShort("stats_stealing", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsTackling(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsTackling(id, con), value);
        SqlUtils.sumShort("stats_tackling", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsHeading(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsHeading(id, con), value);
        SqlUtils.sumShort("stats_heading", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsShortShots(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsShortShots(id, con), value);
        SqlUtils.sumShort("stats_short_shots", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsLongShots(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsLongShots(id, con), value);
        SqlUtils.sumShort("stats_long_shots", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsCrossing(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsCrossing(id, con), value);
        SqlUtils.sumShort("stats_crossing", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsShortPasses(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsShortPasses(id, con), value);
        SqlUtils.sumShort("stats_short_passes", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsLongPasses(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsLongPasses(id, con), value);
        SqlUtils.sumShort("stats_long_passes", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsMarking(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsMarking(id, con), value);
        SqlUtils.sumShort("stats_marking", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsGoalkeeping(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsGoalkeeping(id, con), value);
        SqlUtils.sumShort("stats_goalkeeping", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsPunching(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsPunching(id, con), value);
        SqlUtils.sumShort("stats_punching", add, table, id, con);
        return (short)(value - add);
    }

    public static short setStatsDefense(int value, int id, Connection ... con) {
        short add = CharacterUtils.statsUpToHundred(PlayerInfo.getStatsDefense(id, con), value);
        SqlUtils.sumShort("stats_defense", add, table, id, con);
        return (short)(value - add);
    }

    public static void setTrainingRunning(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_running", value, table, id, con);
    }

    public static void setTrainingEndurance(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_endurance", value, table, id, con);
    }

    public static void setTrainingAgility(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_agility", value, table, id, con);
    }

    public static void setTrainingBallControl(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_ball_control", value, table, id, con);
    }

    public static void setTrainingDribbling(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_dribbling", value, table, id, con);
    }

    public static void setTrainingStealing(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_stealing", value, table, id, con);
    }

    public static void setTrainingTackling(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_tackling", value, table, id, con);
    }

    public static void setTrainingHeading(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_heading", value, table, id, con);
    }

    public static void setTrainingShortShots(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_short_shots", value, table, id, con);
    }

    public static void setTrainingLongShots(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_long_shots", value, table, id, con);
    }

    public static void setTrainingCrossing(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_crossing", value, table, id, con);
    }

    public static void setTrainingShortPasses(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_short_passes", value, table, id, con);
    }

    public static void setTrainingLongPasses(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_long_passes", value, table, id, con);
    }

    public static void setTrainingMarking(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_marking", value, table, id, con);
    }

    public static void setTrainingGoalkeeping(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_goalkeeping", value, table, id, con);
    }

    public static void setTrainingPunching(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_punching", value, table, id, con);
    }

    public static void setTrainingDefense(short value, int id, Connection ... con) {
        SqlUtils.sumShort("training_defense", value, table, id, con);
    }

    public static void setBonusRunning(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_running", value, table, id, con);
    }

    public static void setBonusEndurance(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_endurance", value, table, id, con);
    }

    public static void setBonusAgility(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_agility", value, table, id, con);
    }

    public static void setBonusBallControl(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_ball_control", value, table, id, con);
    }

    public static void setBonusDribbling(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_dribbling", value, table, id, con);
    }

    public static void setBonusStealing(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_stealing", value, table, id, con);
    }

    public static void setBonusTackling(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_tackling", value, table, id, con);
    }

    public static void setBonusHeading(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_heading", value, table, id, con);
    }

    public static void setBonusShortShots(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_short_shots", value, table, id, con);
    }

    public static void setBonusLongShots(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_long_shots", value, table, id, con);
    }

    public static void setBonusCrossing(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_crossing", value, table, id, con);
    }

    public static void setBonusShortPasses(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_short_passes", value, table, id, con);
    }

    public static void setBonusLongPasses(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_long_passes", value, table, id, con);
    }

    public static void setBonusMarking(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_marking", value, table, id, con);
    }

    public static void setBonusGoalkeeping(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_goalkeeping", value, table, id, con);
    }

    public static void setBonusPunching(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_punching", value, table, id, con);
    }

    public static void setBonusDefense(short value, int id, Connection ... con) {
        SqlUtils.sumShort("bonus_defense", value, table, id, con);
    }

    public static void setHistoryMatches(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_matches", value, table, id, con);
        SqlUtils.sumInt("history_month_matches", value, table, id, con);
    }

    public static void setHistoryWins(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_wins", value, table, id, con);
        SqlUtils.sumInt("history_month_wins", value, table, id, con);
    }

    public static void setHistoryDraws(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_draws", value, table, id, con);
        SqlUtils.sumInt("history_month_draws", value, table, id, con);
    }

    public static void setHistoryMom(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_MOM", value, table, id, con);
        SqlUtils.sumInt("history_month_MOM", value, table, id, con);
    }

    public static void setHistoryValidGoals(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_valid_goals", value, table, id, con);
        SqlUtils.sumInt("history_month_valid_goals", value, table, id, con);
    }

    public static void setHistoryValidAssists(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_valid_assists", value, table, id, con);
        SqlUtils.sumInt("history_month_valid_assists", value, table, id, con);
    }

    public static void setHistoryValidInterception(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_valid_interception", value, table, id, con);
        SqlUtils.sumInt("history_month_valid_interception", value, table, id, con);
    }

    public static void setHistoryValidShooting(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_valid_shooting", value, table, id, con);
        SqlUtils.sumInt("history_month_valid_shooting", value, table, id, con);
    }

    public static void setHistoryValidStealing(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_valid_stealing", value, table, id, con);
        SqlUtils.sumInt("history_month_valid_stealing", value, table, id, con);
    }

    public static void setHistoryValidTackling(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_valid_tackling", value, table, id, con);
        SqlUtils.sumInt("history_month_valid_tackling", value, table, id, con);
    }

    public static void setHistoryShooting(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_shooting", value, table, id, con);
        SqlUtils.sumInt("history_month_shooting", value, table, id, con);
    }

    public static void setHistoryStealing(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_stealing", value, table, id, con);
        SqlUtils.sumInt("history_month_stealing", value, table, id, con);
    }

    public static void setHistoryTackling(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_tackling", value, table, id, con);
        SqlUtils.sumInt("history_month_tackling", value, table, id, con);
    }

    public static void setHistoryTotalPoints(int value, int id, Connection ... con) {
        SqlUtils.sumInt("history_total_points", value, table, id, con);
        SqlUtils.sumInt("history_month_total_points", value, table, id, con);
    }

    public static void setStatusMessage(String value, int id, Connection ... con) {
        SqlUtils.setString("status_message", value, table, id, con);
    }

    public static void setInventoryItems(Map<Integer, Item> value, int id, Connection ... con) {
        SqlUtils.setString("inventory_items", Item.mapToString(value), table, id, con);
    }

    public static void setInventoryTraining(Map<Integer, Training> value, int id, Connection ... con) {
        SqlUtils.setString("inventory_training", Training.mapToString(value), table, id, con);
    }

    public static void setInventorySkills(Map<Integer, Skill> value, int id, Connection ... con) {
        SqlUtils.setString("inventory_skills", Skill.mapToString(value), table, id, con);
    }

    public static void setInventoryCelebration(Map<Integer, Celebration> value, int id, Connection ... con) {
        SqlUtils.setString("inventory_celebration", Celebration.mapToString(value), table, id, con);
    }

    public static void setFriendsList(FriendsList value, int id, Connection ... con) {
        SqlUtils.setString("friends_list", value.toString(), table, id, con);
    }

    public static void setIgnoredList(IgnoredList value, int id, Connection ... con) {
        SqlUtils.setString("ignored_list", value.toString(), table, id, con);
    }
}