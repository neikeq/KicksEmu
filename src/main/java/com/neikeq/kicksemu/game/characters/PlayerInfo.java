package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.storage.SqlUtils;

import java.util.Map;

public class PlayerInfo {

    private final int id;

    private final SqlUtils sqlUtils;

    // Sql getters

    public int getId() {
        return id;
    }

    public int getOwner() {
        return sqlUtils.getInt("owner");
    }

    public String getName() {
        return sqlUtils.getString("name");
    }

    public int getClubId() {
        return sqlUtils.getInt("club_id");
    }

    public boolean isBlocked() {
        return sqlUtils.getBoolean("blocked");
    }

    public boolean isModerator() {
        return sqlUtils.getBoolean("moderator");
    }

    public short getLevel() {
        return sqlUtils.getShort("level");
    }

    public short getPosition() {
        return sqlUtils.getShort("position");
    }

    public short getCurrentQuest() {
        return sqlUtils.getShort("quest_current");
    }

    public short getRemainingQuestMatches() {
        return sqlUtils.getShort("quest_matches_left");
    }

    public byte getTutorialDribbling() {
        return sqlUtils.getByte("tutorial_dribbling");
    }

    public byte getTutorialPassing() {
        return sqlUtils.getByte("tutorial_passing");
    }

    public byte getTutorialShooting() {
        return sqlUtils.getByte("tutorial_shooting");
    }

    public byte getTutorialDefense() {
        return sqlUtils.getByte("tutorial_defense");
    }

    public boolean getReceivedReward() {
        return sqlUtils.getBoolean("received_reward");
    }

    public int getExperience() {
        return sqlUtils.getInt("experience");
    }

    public int getPoints() {
        return sqlUtils.getInt("points");
    }

    public short getTicketsKash() {
        return sqlUtils.getShort("tickets_kash");
    }

    public short getTicketsPoints() {
        return sqlUtils.getShort("tickets_points");
    }

    public short getAnimation() {
        return sqlUtils.getShort("animation");
    }

    public short getFace() {
        return sqlUtils.getShort("face");
    }

    public int getDefaultHead() {
        return sqlUtils.getInt("default_head");
    }

    public int getDefaultShirts() {
        return sqlUtils.getInt("default_shirts");
    }

    public int getDefaultPants() {
        return sqlUtils.getInt("default_pants");
    }

    public int getDefaultShoes() {
        return sqlUtils.getInt("default_shoes");
    }

    public int getItemHead() {
        return sqlUtils.getInt("item_head");
    }

    public int getItemGlasses() {
        return sqlUtils.getInt("item_glasses");
    }

    public int getItemShirts() {
        return sqlUtils.getInt("item_shirts");
    }

    public int getItemPants() {
        return sqlUtils.getInt("item_pants");
    }

    public int getItemGlove() {
        return sqlUtils.getInt("item_glove");
    }

    public int getItemShoes() {
        return sqlUtils.getInt("item_shoes");
    }

    public int getItemSocks() {
        return sqlUtils.getInt("item_socks");
    }

    public int getItemWrist() {
        return sqlUtils.getInt("item_wrist");
    }

    public int getItemArm() {
        return sqlUtils.getInt("item_arm");
    }

    public int getItemKnee() {
        return sqlUtils.getInt("item_knee");
    }

    public int getItemEar() {
        return sqlUtils.getInt("item_ear");
    }

    public int getItemNeck() {
        return sqlUtils.getInt("item_neck");
    }

    public int getItemMask() {
        return sqlUtils.getInt("item_mask");
    }

    public int getItemMuffler() {
        return sqlUtils.getInt("item_muffler");
    }

    public int getItemPackage() {
        return sqlUtils.getInt("item_package");
    }

    public short getStatsPoints() {
        return sqlUtils.getShort("stats_points");
    }

    // Stats

    public short getStatsRunning() {
        return sqlUtils.getShort("stats_running");
    }

    public short getStatsEndurance() {
        return sqlUtils.getShort("stats_endurance");
    }

    public short getStatsAgility() {
        return sqlUtils.getShort("stats_agility");
    }

    public short getStatsBallControl() {
        return sqlUtils.getShort("stats_ball_control");
    }

    public short getStatsDribbling() {
        return sqlUtils.getShort("stats_dribbling");
    }

    public short getStatsStealing() {
        return sqlUtils.getShort("stats_stealing");
    }

    public short getStatsTackling() {
        return sqlUtils.getShort("stats_tackling");
    }

    public short getStatsHeading() {
        return sqlUtils.getShort("stats_heading");
    }

    public short getStatsShortShots() {
        return sqlUtils.getShort("stats_short_shots");
    }

    public short getStatsLongShots() {
        return sqlUtils.getShort("stats_long_shots");
    }

    public short getStatsCrossing() {
        return sqlUtils.getShort("stats_crossing");
    }

    public short getStatsShortPasses() {
        return sqlUtils.getShort("stats_short_passes");
    }

    public short getStatsLongPasses() {
        return sqlUtils.getShort("stats_long_passes");
    }

    public short getStatsMarking() {
        return sqlUtils.getShort("stats_marking");
    }

    public short getStatsGoalkeeping() {
        return sqlUtils.getShort("stats_goalkeeping");
    }

    public short getStatsPunching() {
        return sqlUtils.getShort("stats_punching");
    }

    public short getStatsDefense() {
        return sqlUtils.getShort("stats_defense");
    }

    // Stats Training

    public short getTrainingStatsRunning() {
        return sqlUtils.getShort("training_running");
    }

    public short getTrainingStatsEndurance() {
        return sqlUtils.getShort("training_endurance");
    }

    public short getTrainingStatsAgility() {
        return sqlUtils.getShort("training_agility");
    }

    public short getTrainingStatsBallControl() {
        return sqlUtils.getShort("training_ball_control");
    }

    public short getTrainingStatsDribbling() {
        return sqlUtils.getShort("training_dribbling");
    }

    public short getTrainingStatsStealing() {
        return sqlUtils.getShort("training_stealing");
    }

    public short getTrainingStatsTackling() {
        return sqlUtils.getShort("training_tackling");
    }

    public short getTrainingStatsHeading() {
        return sqlUtils.getShort("training_heading");
    }

    public short getTrainingStatsShortShots() {
        return sqlUtils.getShort("training_short_shots");
    }

    public short getTrainingStatsLongShots() {
        return sqlUtils.getShort("training_long_shots");
    }

    public short getTrainingStatsCrossing() {
        return sqlUtils.getShort("training_crossing");
    }

    public short getTrainingStatsShortPasses() {
        return sqlUtils.getShort("training_short_passes");
    }

    public short getTrainingStatsLongPasses() {
        return sqlUtils.getShort("training_long_passes");
    }

    public short getTrainingStatsMarking() {
        return sqlUtils.getShort("training_marking");
    }

    public short getTrainingStatsGoalkeeping() {
        return sqlUtils.getShort("training_goalkeeping");
    }

    public short getTrainingStatsPunching() {
        return sqlUtils.getShort("training_punching");
    }

    public short getTrainingStatsDefense() {
        return sqlUtils.getShort("training_defense");
    }

    // Stats Bonus

    public short getBonusStatsRunning() {
        return sqlUtils.getShort("bonus_running");
    }

    public short getBonusStatsEndurance() {
        return sqlUtils.getShort("bonus_endurance");
    }

    public short getBonusStatsAgility() {
        return sqlUtils.getShort("bonus_agility");
    }

    public short getBonusStatsBallControl() {
        return sqlUtils.getShort("bonus_ball_control");
    }

    public short getBonusStatsDribbling() {
        return sqlUtils.getShort("bonus_dribbling");
    }

    public short getBonusStatsStealing() {
        return sqlUtils.getShort("bonus_stealing");
    }

    public short getBonusStatsTackling() {
        return sqlUtils.getShort("bonus_tackling");
    }

    public short getBonusStatsHeading() {
        return sqlUtils.getShort("bonus_heading");
    }

    public short getBonusStatsShortShots() {
        return sqlUtils.getShort("bonus_short_shots");
    }

    public short getBonusStatsLongShots() {
        return sqlUtils.getShort("bonus_long_shots");
    }

    public short getBonusStatsCrossing() {
        return sqlUtils.getShort("bonus_crossing");
    }

    public short getBonusStatsShortPasses() {
        return sqlUtils.getShort("bonus_short_passes");
    }

    public short getBonusStatsLongPasses() {
        return sqlUtils.getShort("bonus_long_passes");
    }

    public short getBonusStatsMarking() {
        return sqlUtils.getShort("bonus_marking");
    }

    public short getBonusStatsGoalkeeping() {
        return sqlUtils.getShort("bonus_goalkeeping");
    }

    public short getBonusStatsPunching() {
        return sqlUtils.getShort("bonus_punching");
    }

    public short getBonusStatsDefense() {
        return sqlUtils.getShort("bonus_defense");
    }

    // History

    public int getHistoryMatches() {
        return sqlUtils.getInt("history_matches");
    }

    public int getHistoryWins() {
        return sqlUtils.getInt("history_wins");
    }

    public int getHistoryDraws() {
        return sqlUtils.getInt("history_draws");
    }

    public int getHistoryMom() {
        return sqlUtils.getInt("history_MOM");
    }

    public int getHistoryValidGoals() {
        return sqlUtils.getInt("history_valid_goals");
    }

    public int getHistoryValidAssists() {
        return sqlUtils.getInt("history_valid_assists");
    }

    public int getHistoryValidInterception() {
        return sqlUtils.getInt("history_valid_interception");
    }

    public int getHistoryValidShooting() {
        return sqlUtils.getInt("history_valid_shooting");
    }

    public int getHistoryValidStealing() {
        return sqlUtils.getInt("history_valid_stealing");
    }

    public int getHistoryValidTackling() {
        return sqlUtils.getInt("history_valid_tackling");
    }

    public int getHistoryShooting() {
        return sqlUtils.getInt("history_shooting");
    }

    public int getHistoryStealing() {
        return sqlUtils.getInt("history_stealing");
    }

    public int getHistoryTackling() {
        return sqlUtils.getInt("history_tackling");
    }

    public int getHistoryTotalPoints() {
        return sqlUtils.getInt("history_total_points");
    }

    // History Last Month

    public int getHistoryMonthMatches() {
        return sqlUtils.getInt("history_month_matches");
    }

    public int getHistoryMonthWins() {
        return sqlUtils.getInt("history_month_wins");
    }

    public int getHistoryMonthDraws() {
        return sqlUtils.getInt("history_month_draws");
    }

    public int getHistoryMonthMom() {
        return sqlUtils.getInt("history_month_MOM");
    }

    public int getHistoryMonthValidGoals() {
        return sqlUtils.getInt("history_month_valid_goals");
    }

    public int getHistoryMonthValidAssists() {
        return sqlUtils.getInt("history_month_valid_assists");
    }

    public int getHistoryMonthValidInterception() {
        return sqlUtils.getInt("history_month_valid_interception");
    }

    public int getHistoryMonthValidShooting() {
        return sqlUtils.getInt("history_month_valid_shooting");
    }

    public int getHistoryMonthValidStealing() {
        return sqlUtils.getInt("history_month_valid_stealing");
    }

    public int getHistoryMonthValidTackling() {
        return sqlUtils.getInt("history_month_valid_tackling");
    }

    public int getHistoryMonthShooting() {
        return sqlUtils.getInt("history_month_shooting");
    }

    public int getHistoryMonthStealing() {
        return sqlUtils.getInt("history_month_stealing");
    }

    public int getHistoryMonthTackling() {
        return sqlUtils.getInt("history_month_tackling");
    }

    public int getHistoryMonthTotalPoints() {
        return sqlUtils.getInt("history_month_total_points");
    }

    // Ranking

    public short getRankingMatches() {
        return sqlUtils.getShort("ranking_matches");
    }

    public short getRankingWins() {
        return sqlUtils.getShort("ranking_wins");
    }

    public short getRankingPoints() {
        return sqlUtils.getShort("ranking_points");
    }

    public short getRankingMom() {
        return sqlUtils.getShort("ranking_MOM");
    }

    public short getRankingValidGoals() {
        return sqlUtils.getShort("ranking_valid_goals");
    }

    public short getRankingValidAssists() {
        return sqlUtils.getShort("ranking_valid_assists");
    }

    public short getRankingValidInterception() {
        return sqlUtils.getShort("ranking_valid_interception");
    }

    public short getRankingValidShooting() {
        return sqlUtils.getShort("ranking_valid_shooting");
    }

    public short getRankingValidStealing() {
        return sqlUtils.getShort("ranking_valid_stealing");
    }

    public short getRankingValidTackling() {
        return sqlUtils.getShort("ranking_valid_tackling");
    }

    public short getRankingAvgGoals() {
        return sqlUtils.getShort("ranking_avg_goals");
    }

    public short getRankingAvgAssists() {
        return sqlUtils.getShort("ranking_avg_assists");
    }

    public short getRankingAvgInterception() {
        return sqlUtils.getShort("ranking_avg_interception");
    }

    public short getRankingAvgShooting() {
        return sqlUtils.getShort("ranking_avg_shooting");
    }

    public short getRankingAvgStealing() {
        return sqlUtils.getShort("ranking_avg_stealing");
    }

    public short getRankingAvgTackling() {
        return sqlUtils.getShort("ranking_avg_tackling");
    }

    public short getRankingAvgVotePoints() {
        return sqlUtils.getShort("ranking_avg_vote_points");
    }

    public short getRankingShooting() {
        return sqlUtils.getShort("ranking_shooting");
    }

    public short getRankingStealing() {
        return sqlUtils.getShort("ranking_stealing");
    }

    public short getRankingTackling() {
        return sqlUtils.getShort("ranking_tackling");
    }

    public short getRankingTotalPoints() {
        return sqlUtils.getShort("ranking_total_points");
    }

    // Ranking Last Month

    public short getRankingMonthMatches() {
        return sqlUtils.getShort("ranking_month_matches");
    }

    public short getRankingMonthWins() {
        return sqlUtils.getShort("ranking_month_wins");
    }

    public short getRankingMonthPoints() {
        return sqlUtils.getShort("ranking_month_points");
    }

    public short getRankingMonthMom() {
        return sqlUtils.getShort("ranking_month_MOM");
    }

    public short getRankingMonthValidGoals() {
        return sqlUtils.getShort("ranking_month_valid_goals");
    }

    public short getRankingMonthValidAssists() {
        return sqlUtils.getShort("ranking_month_valid_assists");
    }

    public short getRankingMonthValidInterception() {
        return sqlUtils.getShort("ranking_month_valid_interception");
    }

    public short getRankingMonthValidShooting() {
        return sqlUtils.getShort("ranking_month_valid_shooting");
    }

    public short getRankingMonthValidStealing() {
        return sqlUtils.getShort("ranking_month_valid_stealing");
    }

    public short getRankingMonthValidTackling() {
        return sqlUtils.getShort("ranking_month_valid_tackling");
    }

    public short getRankingMonthAvgGoals() {
        return sqlUtils.getShort("ranking_month_avg_goals");
    }

    public short getRankingMonthAvgAssists() {
        return sqlUtils.getShort("ranking_month_avg_assists");
    }

    public short getRankingMonthAvgInterception() {
        return sqlUtils.getShort("ranking_month_avg_interception");
    }

    public short getRankingMonthAvgShooting() {
        return sqlUtils.getShort("ranking_month_avg_shooting");
    }

    public short getRankingMonthAvgStealing() {
        return sqlUtils.getShort("ranking_month_avg_stealing");
    }

    public short getRankingMonthAvgTackling() {
        return sqlUtils.getShort("ranking_month_avg_tackling");
    }

    public short getRankingMonthAvgVotePoints() {
        return sqlUtils.getShort("ranking_month_avg_vote_points");
    }

    public short getRankingMonthShooting() {
        return sqlUtils.getShort("ranking_month_shooting");
    }

    public short getRankingMonthStealing() {
        return sqlUtils.getShort("ranking_month_stealing");
    }

    public short getRankingMonthTackling() {
        return sqlUtils.getShort("ranking_month_tackling");
    }

    public short getRankingMonthTotalPoints() {
        return sqlUtils.getShort("ranking_month_total_points");
    }

    // Others

    public String getStatusMessage() {
        return sqlUtils.getString("status_message");
    }

    public Map<Integer, Item> getInventoryItems() {
        return Item.mapFromString(sqlUtils.getString("inventory_items"));
    }

    public Map<Integer, Training> getInventoryTraining() {
        return Training.mapFromString(sqlUtils.getString("inventory_training"));
    }

    public Map<Integer, Skill> getInventorySkills() {
        return Skill.mapFromString(sqlUtils.getString("inventory_skills"));
    }

    public Map<Integer, Celebration> getInventoryCelebration() {
        return Celebration.mapFromString(sqlUtils.getString("inventory_celebration"));
    }

    public String getInventorySkillsString() {
        return sqlUtils.getString("inventory_skills");
    }

    public String getInventoryCelebrationString() {
        return sqlUtils.getString("inventory_celebration");
    }

    public String getFriends() {
        return sqlUtils.getString("friend");
    }

    // Sql setters

    public boolean setOwner(int value) {
        return sqlUtils.setInt("owner", value);
    }

    public boolean setName(String value) {
        return sqlUtils.setString("name", value);
    }

    public boolean setBlocked(boolean value) {
        return sqlUtils.setBoolean("blocked", value);
    }

    public boolean setLevel(short value) {
        return sqlUtils.setShort("level", value);
    }

    public boolean setPosition(short value) {
        return sqlUtils.setShort("position", value);
    }

    public boolean setClubId(int value) {
        return sqlUtils.setInt("club_id", value);
    }

    public boolean setCurrentQuest(short value) {
        return sqlUtils.setShort("quest_current", value);
    }

    public boolean setRemainingQuestMatches(short value) {
        return sqlUtils.setShort("quest_matches_left", value);
    }

    public boolean setTutorialDribbling(byte value) {
        return sqlUtils.setByte("tutorial_dribbling", value);
    }

    public boolean setTutorialPassing(byte value) {
        return sqlUtils.setByte("tutorial_passing", value);
    }

    public boolean setTutorialShooting(byte value) {
        return sqlUtils.setByte("tutorial_shooting", value);
    }

    public boolean setTutorialDefense(byte value) {
        return sqlUtils.setByte("tutorial_defense", value);
    }

    public boolean setReceivedReward(boolean value) {
        return sqlUtils.setBoolean("received_reward", value);
    }

    public boolean setExperience(int value) {
        return sqlUtils.setInt("experience", value);
    }

    public boolean setPoints(int value) {
        return sqlUtils.setInt("points", value);
    }

    public boolean setTicketsKash(short value) {
        return sqlUtils.setShort("tickets_kash", value);
    }

    public boolean setTicketsPoints(short value) {
        return sqlUtils.setShort("tickets_points", value);
    }

    public boolean setAnimation(short value) {
        return sqlUtils.setShort("animation", value);
    }

    public boolean setFace(short value) {
        return sqlUtils.setShort("face", value);
    }

    public boolean setDefaultHead(int value) {
        return sqlUtils.setInt("default_head", value);
    }

    public boolean setDefaultShirts(int value) {
        return sqlUtils.setInt("default_shirts", value);
    }

    public boolean setDefaultPants(int value) {
        return sqlUtils.setInt("default_pants", value);
    }

    public boolean setDefaultShoes(int value) {
        return sqlUtils.setInt("default_shoes", value);
    }

    public boolean setItemHead(int value) {
        return sqlUtils.setInt("item_head", value);
    }

    public boolean setItemGlasses(int value) {
        return sqlUtils.setInt("item_glasses", value);
    }

    public boolean setItemShirts(int value) {
        return sqlUtils.setInt("item_shirts", value);
    }

    public boolean setItemPants(int value) {
        return sqlUtils.setInt("item_pants", value);
    }

    public boolean setItemGlove(int value) {
        return sqlUtils.setInt("item_glove", value);
    }

    public boolean setItemShoes(int value) {
        return sqlUtils.setInt("item_shoes", value);
    }

    public boolean setItemSocks(int value) {
        return sqlUtils.setInt("item_socks", value);
    }

    public boolean setItemWrist(int value) {
        return sqlUtils.setInt("item_wrist", value);
    }

    public boolean setItemArm(int value) {
        return sqlUtils.setInt("item_arm", value);
    }

    public boolean setItemKnee(int value) {
        return sqlUtils.setInt("item_knee", value);
    }

    public boolean setItemEar(int value) {
        return sqlUtils.setInt("item_ear", value);
    }

    public boolean setItemNeck(int value) {
        return sqlUtils.setInt("item_neck", value);
    }

    public boolean setItemMask(int value) {
        return sqlUtils.setInt("item_mask", value);
    }

    public boolean setItemMuffler(int value) {
        return sqlUtils.setInt("item_muffler", value);
    }

    public boolean setItemPackage(int value) {
        return sqlUtils.setInt("item_package", value);
    }

    public boolean setStatsPoints(short value) {
        return sqlUtils.setShort("stats_points", value);
    }

    public boolean setStatsRunning(short value) {
        return sqlUtils.setShort("stats_running", value);
    }

    public boolean setStatsEndurance(short value) {
        return sqlUtils.setShort("stats_endurance", value);
    }

    public boolean setStatsAgility(short value) {
        return sqlUtils.setShort("stats_agility", value);
    }

    public boolean setStatsBallControl(short value) {
        return sqlUtils.setShort("stats_ball_control", value);
    }

    public boolean setStatsDribbling(short value) {
        return sqlUtils.setShort("stats_dribbling", value);
    }

    public boolean setStatsStealing(short value) {
        return sqlUtils.setShort("stats_stealing", value);
    }

    public boolean setStatsTackling(short value) {
        return sqlUtils.setShort("stats_tackling", value);
    }

    public boolean setStatsHeading(short value) {
        return sqlUtils.setShort("stats_heading", value);
    }

    public boolean setStatsShortShots(short value) {
        return sqlUtils.setShort("stats_short_shots", value);
    }

    public boolean setStatsLongShots(short value) {
        return sqlUtils.setShort("stats_long_shots", value);
    }

    public boolean setStatsCrossing(short value) {
        return sqlUtils.setShort("stats_crossing", value);
    }

    public boolean setStatsShortPasses(short value) {
        return sqlUtils.setShort("stats_shorts_passes", value);
    }

    public boolean setStatsLongPasses(short value) {
        return sqlUtils.setShort("stats_long_passes", value);
    }

    public boolean setStatsMarking(short value) {
        return sqlUtils.setShort("stats_marking", value);
    }

    public boolean setStatsGoalkeeping(short value) {
        return sqlUtils.setShort("stats_goalkeeping", value);
    }

    public boolean setStatsPunching(short value) {
        return sqlUtils.setShort("stats_punching", value);
    }

    public boolean setStatsDefense(short value) {
        return sqlUtils.setShort("stats_defense", value);
    }

    public boolean setStatusMessage(String value) {
        return sqlUtils.setString("status_message", value);
    }

    public boolean setInventoryItems(String value) {
        return sqlUtils.setString("inventory_items", value);
    }

    public boolean setInventoryTraining(String value) {
        return sqlUtils.setString("inventory_training", value);
    }

    public boolean setInventorySkills(String value) {
        return sqlUtils.setString("inventory_skills", value);
    }

    public boolean setInventoryCelebration(String value) {
        return sqlUtils.setString("inventory_celebration", value);
    }

    public boolean setFriends(String value) {
        return sqlUtils.setString("friend", value);
    }

    public PlayerInfo(int id) {
        this.id = id;
        this.sqlUtils = new SqlUtils(id, "characters");
    }
}