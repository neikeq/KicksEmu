package com.neikeq.kicksemu.network.packets.out;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.clubs.ClubInfo;
import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.users.UserInfo;

import java.util.Map;

public class MessageUtils {

    public static void appendQuestInfo(PlayerInfo player, ServerMessage msg) {
        msg.append(player.getCurrentQuest());
        msg.append(player.getRemainingQuestMatches());
    }

    public static void appendTutorialInfo(PlayerInfo player, ServerMessage msg) {
        msg.append(player.getTutorialDribbling());
        msg.append(player.getTutorialPassing());
        msg.append(player.getTutorialShooting());
        msg.append(player.getTutorialDefense());
    }

    public static void appendCharacterInfo(PlayerInfo player, UserInfo owner, ServerMessage msg) {
        msg.append(player.getLevel());
        msg.append(player.getExperience());
        msg.append(player.getStatsPoints());

        msg.append(owner.getKash());

        msg.append(player.getPoints());
        msg.appendZeros(8);
        msg.append(player.getTicketsKash());
        msg.append(player.getTicketsPoints());
    }

    public static void appendDefaultClothes(PlayerInfo player, ServerMessage msg) {
        msg.append(player.getDefaultHead());
        msg.append(player.getDefaultShirts());
        msg.append(player.getDefaultPants());
        msg.append(player.getDefaultShoes());
    }

    public static void appendInventoryItem(Item item, ServerMessage msg) {
        // If the item is null we replace it with an empty one
        if (item == null) {
            item = new Item();
        }

        msg.append(item.getInventoryId());
        msg.append(item.getId());
        msg.append(item.isSelected());
        msg.append(item.getExpiration());
        msg.append(item.getStatsBonusOne());
        msg.append(item.getStatsBonusTwo());
        msg.append(item.getRemainUsages());
        msg.append((int)item.getTimestampExpire());
        msg.append(item.isVisible());
    }

    public static void appendInventoryTraining(Training training, ServerMessage msg) {
        // If the training is null we replace it with an empty one
        if (training == null) {
            training = new Training();
        }

        msg.append(training.getInventoryId());
        msg.append(training.getId());
        msg.appendZeros(10);
        msg.append((int) training.getTimestampExpire());
        msg.append(training.isVisible());
    }

    public static void appendInventorySkill(Skill skill, ServerMessage msg) {
        // If the skill is null we replace it with an empty one
        if (skill == null) {
            skill = new Skill();
        }

        msg.append(skill.getInventoryId());
        msg.append(skill.getId());
        msg.append(skill.getSelectionIndex());
        msg.append(skill.getExpiration());
        msg.appendZeros(8);
        msg.append((int) skill.getTimestampExpire());
        msg.append(skill.isVisible());
    }

    public static void appendInventoryCelebration(Celebration celebration, ServerMessage msg) {
        // If the celebration is null we replace it with an empty one
        if (celebration == null) {
            celebration = new Celebration();
        }

        msg.append(celebration.getInventoryId());
        msg.append((short)celebration.getId());
        msg.append(celebration.getSelectionIndex());
        msg.append(celebration.getExpiration());
        msg.appendZeros(8);
        msg.append((int) celebration.getTimestampExpire());
        msg.append(celebration.isVisible());
    }

    public static void appendStats(PlayerInfo player, ServerMessage msg) {
        msg.append(player.getStatsRunning());
        msg.append(player.getStatsEndurance());
        msg.append(player.getStatsAgility());
        msg.append(player.getStatsBallControl());
        msg.append(player.getStatsDribbling());
        msg.append(player.getStatsStealing());
        msg.append(player.getStatsTackling());
        msg.append(player.getStatsHeading());
        msg.append(player.getStatsShortShots());
        msg.append(player.getStatsLongShots());
        msg.append(player.getStatsCrossing());
        msg.append(player.getStatsShortPasses());
        msg.append(player.getStatsLongPasses());
        msg.append(player.getStatsMarking());
        msg.append(player.getStatsGoalkeeping());
        msg.append(player.getStatsPunching());
        msg.append(player.getStatsDefense());
    }

    public static void appendStatsTraining(PlayerInfo player, ServerMessage msg) {
        msg.append(player.getStatsRunning());
        msg.append(player.getStatsEndurance());
        msg.append(player.getStatsAgility());
        msg.append(player.getStatsBallControl());
        msg.append(player.getStatsDribbling());
        msg.append(player.getStatsStealing());
        msg.append(player.getStatsTackling());
        msg.append(player.getStatsHeading());
        msg.append(player.getStatsShortShots());
        msg.append(player.getStatsLongShots());
        msg.append(player.getStatsCrossing());
        msg.append(player.getStatsShortPasses());
        msg.append(player.getStatsLongPasses());
        msg.append(player.getStatsMarking());
        msg.append(player.getStatsGoalkeeping());
        msg.append(player.getStatsPunching());
        msg.append(player.getStatsDefense());
    }

    public static void appendStatsBonus(PlayerInfo player, ServerMessage msg) {
        msg.append(player.getStatsRunning());
        msg.append(player.getStatsEndurance());
        msg.append(player.getStatsAgility());
        msg.append(player.getStatsBallControl());
        msg.append(player.getStatsDribbling());
        msg.append(player.getStatsStealing());
        msg.append(player.getStatsTackling());
        msg.append(player.getStatsHeading());
        msg.append(player.getStatsShortShots());
        msg.append(player.getStatsLongShots());
        msg.append(player.getStatsCrossing());
        msg.append(player.getStatsShortPasses());
        msg.append(player.getStatsLongPasses());
        msg.append(player.getStatsMarking());
        msg.append(player.getStatsGoalkeeping());
        msg.append(player.getStatsPunching());
        msg.append(player.getStatsDefense());
    }

    public static void appendHistory(PlayerInfo player, ServerMessage msg) {
        msg.append(player.getHistoryMatches());
        msg.append(player.getHistoryWins());
        msg.append(player.getHistoryDraws());
        msg.append(player.getHistoryMom());
        msg.append(player.getHistoryValidGoals());
        msg.append(player.getHistoryValidAssists());
        msg.append(player.getHistoryValidInterception());
        msg.append(player.getHistoryValidShooting());
        msg.append(player.getHistoryValidStealing());
        msg.append(player.getHistoryValidTackling());
        msg.appendZeros(4);
        msg.append(player.getHistoryShooting());
        msg.append(player.getHistoryStealing());
        msg.append(player.getHistoryTackling());
        msg.appendZeros(4);
        msg.append(player.getHistoryTotalPoints());
    }

    public static void appendHistoryLastMonth(PlayerInfo player, ServerMessage msg) {
        msg.append(player.getHistoryMonthMatches());
        msg.append(player.getHistoryMonthWins());
        msg.append(player.getHistoryMonthDraws());
        msg.append(player.getHistoryMonthMom());
        msg.append(player.getHistoryMonthValidGoals());
        msg.append(player.getHistoryMonthValidAssists());
        msg.append(player.getHistoryMonthValidInterception());
        msg.append(player.getHistoryMonthValidShooting());
        msg.append(player.getHistoryMonthValidStealing());
        msg.append(player.getHistoryMonthValidTackling());
        msg.appendZeros(4);
        msg.append(player.getHistoryMonthShooting());
        msg.append(player.getHistoryMonthStealing());
        msg.append(player.getHistoryMonthTackling());
        msg.appendZeros(4);
        msg.append(player.getHistoryMonthTotalPoints());
    }

    public static void appendRanking(PlayerInfo player, ServerMessage msg) {
        msg.append(player.getRankingMatches());
        msg.append(player.getRankingWins());
        msg.append(player.getRankingPoints());
        msg.append(player.getRankingMom());
        msg.append(player.getRankingValidGoals());
        msg.append(player.getRankingValidAssists());
        msg.append(player.getRankingValidInterception());
        msg.append(player.getRankingValidShooting());
        msg.append(player.getRankingValidStealing());
        msg.append(player.getRankingValidTackling());
        msg.appendZeros(2);
        msg.append(player.getRankingAvgGoals());
        msg.append(player.getRankingAvgAssists());
        msg.append(player.getRankingAvgInterception());
        msg.append(player.getRankingAvgShooting());
        msg.append(player.getRankingAvgStealing());
        msg.append(player.getRankingAvgTackling());
        msg.appendZeros(2);
        msg.append(player.getRankingAvgVotePoints());
        msg.append(player.getRankingShooting());
        msg.append(player.getRankingStealing());
        msg.append(player.getRankingTackling());
        msg.appendZeros(2);
        msg.append(player.getRankingTotalPoints());
    }

    public static void appendRankingLastMonth(PlayerInfo player, ServerMessage msg) {
        msg.append(player.getRankingMonthMatches());
        msg.append(player.getRankingMonthWins());
        msg.append(player.getRankingMonthPoints());
        msg.append(player.getRankingMonthMom());
        msg.append(player.getRankingMonthValidGoals());
        msg.append(player.getRankingMonthValidAssists());
        msg.append(player.getRankingMonthValidInterception());
        msg.append(player.getRankingMonthValidShooting());
        msg.append(player.getRankingMonthValidStealing());
        msg.append(player.getRankingMonthValidTackling());
        msg.appendZeros(2);
        msg.append(player.getRankingMonthAvgGoals());
        msg.append(player.getRankingMonthAvgAssists());
        msg.append(player.getRankingMonthAvgInterception());
        msg.append(player.getRankingMonthAvgShooting());
        msg.append(player.getRankingMonthAvgStealing());
        msg.append(player.getRankingMonthAvgTackling());
        msg.appendZeros(2);
        msg.append(player.getRankingMonthAvgVotePoints());
        msg.append(player.getRankingMonthShooting());
        msg.append(player.getRankingMonthStealing());
        msg.append(player.getRankingMonthTackling());
        msg.appendZeros(2);
        msg.append(player.getRankingMonthTotalPoints());
    }

    public static void appendItemInUse(Item item, ServerMessage msg) {
        msg.append(item != null ? item.getId() : 0);
        msg.append(item != null, 4);
        msg.appendZeros(20);
    }

    public static void appendItemsInUse(PlayerInfo player, ServerMessage msg) {
        Map<Integer, Item> items = player.getInventoryItems();

        appendItemInUse(items.get(player.getItemHead()), msg);
        appendItemInUse(items.get(player.getItemGlasses()), msg);
        appendItemInUse(items.get(player.getItemShirts()), msg);
        appendItemInUse(items.get(player.getItemPants()), msg);
        appendItemInUse(items.get(player.getItemGlove()), msg);
        appendItemInUse(items.get(player.getItemShoes()), msg);
        appendItemInUse(items.get(player.getItemSocks()), msg);
        appendItemInUse(items.get(player.getItemWrist()), msg);
        appendItemInUse(items.get(player.getItemArm()), msg);
        appendItemInUse(items.get(player.getItemKnee()), msg);
        appendItemInUse(items.get(player.getItemEar()), msg);
        appendItemInUse(items.get(player.getItemNeck()), msg);
        appendItemInUse(items.get(player.getItemMask()), msg);
        appendItemInUse(items.get(player.getItemMuffler()), msg);
        appendItemInUse(items.get(player.getItemPackage()), msg);
    }

    public static void appendInventoryItemsInUse(PlayerInfo player, ServerMessage msg) {
        Map<Integer, Item> items = player.getInventoryItems();

        appendInventoryItem(items.get(player.getItemHead()), msg);
        appendInventoryItem(items.get(player.getItemGlasses()), msg);
        appendInventoryItem(items.get(player.getItemShirts()), msg);
        appendInventoryItem(items.get(player.getItemPants()), msg);
        appendInventoryItem(items.get(player.getItemGlove()), msg);
        appendInventoryItem(items.get(player.getItemShoes()), msg);
        appendInventoryItem(items.get(player.getItemSocks()), msg);
        appendInventoryItem(items.get(player.getItemWrist()), msg);
        appendInventoryItem(items.get(player.getItemArm()), msg);
        appendInventoryItem(items.get(player.getItemKnee()), msg);
        appendInventoryItem(items.get(player.getItemEar()), msg);
        appendInventoryItem(items.get(player.getItemNeck()), msg);
        appendInventoryItem(items.get(player.getItemMask()), msg);
        appendInventoryItem(items.get(player.getItemMuffler()), msg);
        appendInventoryItem(items.get(player.getItemPackage()), msg);
    }

    public static void appendInventorySkillsInUse(PlayerInfo player, ServerMessage msg) {
        Map<Integer, Skill> skills = Skill.inUseFromString(player.getInventorySkillsString());

        for (int i = 1; i <= 30; i++) {
            appendInventorySkill(skills.get(i), msg);
        }
    }

    public static void appendInventoryCelebrationsInUse(PlayerInfo player, ServerMessage msg) {
        Map<Integer, Celebration> celebrations =
                Celebration.inUseFromString(player.getInventoryCelebrationString());

        for (int i = 1; i <= 5; i++) {
            appendInventoryCelebration(celebrations.get(i), msg);
        }
    }

    public static void appendClubUniform(int clubId, ServerMessage msg) {
        ClubInfo club = new ClubInfo(clubId);

        msg.append(club.getUniformHomeShirts());
        msg.append(club.getUniformHomePants());
        msg.append(club.getUniformHomeSocks());
        msg.append(club.getUniformHomeWrist());

        msg.append(club.getUniformAwayShirts());
        msg.append(club.getUniformAwayPants());
        msg.append(club.getUniformAwaySocks());
        msg.append(club.getUniformAwayWrist());
    }
}
