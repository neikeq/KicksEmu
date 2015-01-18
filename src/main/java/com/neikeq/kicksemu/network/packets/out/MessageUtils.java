package com.neikeq.kicksemu.network.packets.out;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.clubs.ClubInfo;
import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.users.UserInfo;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

class MessageUtils {

    public static void appendResult(byte result, ServerMessage msg) {
        msg.append(result);
        msg.append(result == 0 ? result : (byte)255);
    }

    public static void appendQuestInfo(int playerId, ServerMessage msg) {
        msg.append(PlayerInfo.getCurrentQuest(playerId));
        msg.append(PlayerInfo.getRemainingQuestMatches(playerId));
    }

    public static void appendTutorialInfo(int playerId, ServerMessage msg) {
        msg.append(PlayerInfo.getTutorialDribbling(playerId));
        msg.append(PlayerInfo.getTutorialPassing(playerId));
        msg.append(PlayerInfo.getTutorialShooting(playerId));
        msg.append(PlayerInfo.getTutorialDefense(playerId));
    }

    public static void appendCharacterInfo(int playerId, int ownerId, ServerMessage msg) {
        msg.append(PlayerInfo.getLevel(playerId));
        msg.append(PlayerInfo.getExperience(playerId));
        msg.append(PlayerInfo.getStatsPoints(playerId));

        msg.append(UserInfo.getKash(ownerId));

        msg.append(PlayerInfo.getPoints(playerId));
        msg.appendZeros(8);
        msg.append(PlayerInfo.getTicketsKash(playerId));
        msg.append(PlayerInfo.getTicketsPoints(playerId));
    }

    public static void appendDefaultClothes(int playerId, ServerMessage msg) {
        msg.append(PlayerInfo.getDefaultHead(playerId));
        msg.append(PlayerInfo.getDefaultShirts(playerId));
        msg.append(PlayerInfo.getDefaultPants(playerId));
        msg.append(PlayerInfo.getDefaultShoes(playerId));
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
        msg.append(skill.getExpiration() != null ? skill.getExpiration().toInt() : 0);
        msg.appendZeros(8);
        msg.append((int) (skill.getTimestampExpire().getTime() / 1000));
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
        msg.append(celebration.getExpiration().toInt());
        msg.appendZeros(8);
        msg.append((int) celebration.getTimestampExpire().getTime());
        msg.append(celebration.isVisible());
    }

    public static void appendStats(int playerId, ServerMessage msg) {
        msg.append(PlayerInfo.getStatsRunning(playerId));
        msg.append(PlayerInfo.getStatsEndurance(playerId));
        msg.append(PlayerInfo.getStatsAgility(playerId));
        msg.append(PlayerInfo.getStatsBallControl(playerId));
        msg.append(PlayerInfo.getStatsDribbling(playerId));
        msg.append(PlayerInfo.getStatsStealing(playerId));
        msg.append(PlayerInfo.getStatsTackling(playerId));
        msg.append(PlayerInfo.getStatsHeading(playerId));
        msg.append(PlayerInfo.getStatsShortShots(playerId));
        msg.append(PlayerInfo.getStatsLongShots(playerId));
        msg.append(PlayerInfo.getStatsCrossing(playerId));
        msg.append(PlayerInfo.getStatsShortPasses(playerId));
        msg.append(PlayerInfo.getStatsLongPasses(playerId));
        msg.append(PlayerInfo.getStatsMarking(playerId));
        msg.append(PlayerInfo.getStatsGoalkeeping(playerId));
        msg.append(PlayerInfo.getStatsPunching(playerId));
        msg.append(PlayerInfo.getStatsDefense(playerId));
    }

    public static void appendStatsTraining(int playerId, ServerMessage msg) {
        msg.append(PlayerInfo.getTrainingStatsRunning(playerId));
        msg.append(PlayerInfo.getTrainingStatsEndurance(playerId));
        msg.append(PlayerInfo.getTrainingStatsAgility(playerId));
        msg.append(PlayerInfo.getTrainingStatsBallControl(playerId));
        msg.append(PlayerInfo.getTrainingStatsDribbling(playerId));
        msg.append(PlayerInfo.getTrainingStatsStealing(playerId));
        msg.append(PlayerInfo.getTrainingStatsTackling(playerId));
        msg.append(PlayerInfo.getTrainingStatsHeading(playerId));
        msg.append(PlayerInfo.getTrainingStatsShortShots(playerId));
        msg.append(PlayerInfo.getTrainingStatsLongShots(playerId));
        msg.append(PlayerInfo.getTrainingStatsCrossing(playerId));
        msg.append(PlayerInfo.getTrainingStatsShortPasses(playerId));
        msg.append(PlayerInfo.getTrainingStatsLongPasses(playerId));
        msg.append(PlayerInfo.getTrainingStatsMarking(playerId));
        msg.append(PlayerInfo.getTrainingStatsGoalkeeping(playerId));
        msg.append(PlayerInfo.getTrainingStatsPunching(playerId));
        msg.append(PlayerInfo.getTrainingStatsDefense(playerId));
    }

    public static void appendStatsBonus(int playerId, ServerMessage msg) {
        msg.append(PlayerInfo.getBonusStatsRunning(playerId));
        msg.append(PlayerInfo.getBonusStatsEndurance(playerId));
        msg.append(PlayerInfo.getBonusStatsAgility(playerId));
        msg.append(PlayerInfo.getBonusStatsBallControl(playerId));
        msg.append(PlayerInfo.getBonusStatsDribbling(playerId));
        msg.append(PlayerInfo.getBonusStatsStealing(playerId));
        msg.append(PlayerInfo.getBonusStatsTackling(playerId));
        msg.append(PlayerInfo.getBonusStatsHeading(playerId));
        msg.append(PlayerInfo.getBonusStatsShortShots(playerId));
        msg.append(PlayerInfo.getBonusStatsLongShots(playerId));
        msg.append(PlayerInfo.getBonusStatsCrossing(playerId));
        msg.append(PlayerInfo.getBonusStatsShortPasses(playerId));
        msg.append(PlayerInfo.getBonusStatsLongPasses(playerId));
        msg.append(PlayerInfo.getBonusStatsMarking(playerId));
        msg.append(PlayerInfo.getBonusStatsGoalkeeping(playerId));
        msg.append(PlayerInfo.getBonusStatsPunching(playerId));
        msg.append(PlayerInfo.getBonusStatsDefense(playerId));
    }

    public static void appendHistory(int playerId, ServerMessage msg) {
        msg.append(PlayerInfo.getHistoryMatches(playerId));
        msg.append(PlayerInfo.getHistoryWins(playerId));
        msg.append(PlayerInfo.getHistoryDraws(playerId));
        msg.append(PlayerInfo.getHistoryMom(playerId));
        msg.append(PlayerInfo.getHistoryValidGoals(playerId));
        msg.append(PlayerInfo.getHistoryValidAssists(playerId));
        msg.append(PlayerInfo.getHistoryValidInterception(playerId));
        msg.append(PlayerInfo.getHistoryValidShooting(playerId));
        msg.append(PlayerInfo.getHistoryValidStealing(playerId));
        msg.append(PlayerInfo.getHistoryValidTackling(playerId));
        msg.appendZeros(4);
        msg.append(PlayerInfo.getHistoryShooting(playerId));
        msg.append(PlayerInfo.getHistoryStealing(playerId));
        msg.append(PlayerInfo.getHistoryTackling(playerId));
        msg.appendZeros(4);
        msg.append(PlayerInfo.getHistoryTotalPoints(playerId));
    }

    public static void appendHistoryLastMonth(int playerId, ServerMessage msg) {
        msg.append(PlayerInfo.getHistoryMonthMatches(playerId));
        msg.append(PlayerInfo.getHistoryMonthWins(playerId));
        msg.append(PlayerInfo.getHistoryMonthDraws(playerId));
        msg.append(PlayerInfo.getHistoryMonthMom(playerId));
        msg.append(PlayerInfo.getHistoryMonthValidGoals(playerId));
        msg.append(PlayerInfo.getHistoryMonthValidAssists(playerId));
        msg.append(PlayerInfo.getHistoryMonthValidInterception(playerId));
        msg.append(PlayerInfo.getHistoryMonthValidShooting(playerId));
        msg.append(PlayerInfo.getHistoryMonthValidStealing(playerId));
        msg.append(PlayerInfo.getHistoryMonthValidTackling(playerId));
        msg.appendZeros(4);
        msg.append(PlayerInfo.getHistoryMonthShooting(playerId));
        msg.append(PlayerInfo.getHistoryMonthStealing(playerId));
        msg.append(PlayerInfo.getHistoryMonthTackling(playerId));
        msg.appendZeros(4);
        msg.append(PlayerInfo.getHistoryMonthTotalPoints(playerId));
    }

    public static void appendRanking(int playerId, ServerMessage msg) {
        msg.append(PlayerInfo.getRankingMatches(playerId));
        msg.append(PlayerInfo.getRankingWins(playerId));
        msg.append(PlayerInfo.getRankingPoints(playerId));
        msg.append(PlayerInfo.getRankingMom(playerId));
        msg.append(PlayerInfo.getRankingValidGoals(playerId));
        msg.append(PlayerInfo.getRankingValidAssists(playerId));
        msg.append(PlayerInfo.getRankingValidInterception(playerId));
        msg.append(PlayerInfo.getRankingValidShooting(playerId));
        msg.append(PlayerInfo.getRankingValidStealing(playerId));
        msg.append(PlayerInfo.getRankingValidTackling(playerId));
        msg.appendZeros(2);
        msg.append(PlayerInfo.getRankingAvgGoals(playerId));
        msg.append(PlayerInfo.getRankingAvgAssists(playerId));
        msg.append(PlayerInfo.getRankingAvgInterception(playerId));
        msg.append(PlayerInfo.getRankingAvgShooting(playerId));
        msg.append(PlayerInfo.getRankingAvgStealing(playerId));
        msg.append(PlayerInfo.getRankingAvgTackling(playerId));
        msg.appendZeros(2);
        msg.append(PlayerInfo.getRankingAvgVotePoints(playerId));
        msg.append(PlayerInfo.getRankingShooting(playerId));
        msg.append(PlayerInfo.getRankingStealing(playerId));
        msg.append(PlayerInfo.getRankingTackling(playerId));
        msg.appendZeros(2);
        msg.append(PlayerInfo.getRankingTotalPoints(playerId));
    }

    public static void appendRankingLastMonth(int playerId, ServerMessage msg) {
        msg.append(PlayerInfo.getRankingMonthMatches(playerId));
        msg.append(PlayerInfo.getRankingMonthWins(playerId));
        msg.append(PlayerInfo.getRankingMonthPoints(playerId));
        msg.append(PlayerInfo.getRankingMonthMom(playerId));
        msg.append(PlayerInfo.getRankingMonthValidGoals(playerId));
        msg.append(PlayerInfo.getRankingMonthValidAssists(playerId));
        msg.append(PlayerInfo.getRankingMonthValidInterception(playerId));
        msg.append(PlayerInfo.getRankingMonthValidShooting(playerId));
        msg.append(PlayerInfo.getRankingMonthValidStealing(playerId));
        msg.append(PlayerInfo.getRankingMonthValidTackling(playerId));
        msg.appendZeros(2);
        msg.append(PlayerInfo.getRankingMonthAvgGoals(playerId));
        msg.append(PlayerInfo.getRankingMonthAvgAssists(playerId));
        msg.append(PlayerInfo.getRankingMonthAvgInterception(playerId));
        msg.append(PlayerInfo.getRankingMonthAvgShooting(playerId));
        msg.append(PlayerInfo.getRankingMonthAvgStealing(playerId));
        msg.append(PlayerInfo.getRankingMonthAvgTackling(playerId));
        msg.appendZeros(2);
        msg.append(PlayerInfo.getRankingMonthAvgVotePoints(playerId));
        msg.append(PlayerInfo.getRankingMonthShooting(playerId));
        msg.append(PlayerInfo.getRankingMonthStealing(playerId));
        msg.append(PlayerInfo.getRankingMonthTackling(playerId));
        msg.appendZeros(2);
        msg.append(PlayerInfo.getRankingMonthTotalPoints(playerId));
    }

    private static void appendItemInUse(Item item, ServerMessage msg) {
        msg.append(item != null ? item.getId() : 0);
        msg.append(item != null, 4);
        msg.appendZeros(20);
    }

    public static void appendItemsInUse(int playerId, ServerMessage msg) {
        Map<Integer, Item> items = PlayerInfo.getInventoryItems(playerId);

        appendItemInUse(items.get(PlayerInfo.getItemHead(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemGlasses(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemShirts(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemPants(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemGlove(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemShoes(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemSocks(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemWrist(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemArm(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemKnee(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemEar(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemNeck(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemMask(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemMuffler(playerId)), msg);
        appendItemInUse(items.get(PlayerInfo.getItemPackage(playerId)), msg);
    }

    public static void appendInventoryItemsInUse(int playerId, ServerMessage msg) {
        Map<Integer, Item> items = PlayerInfo.getInventoryItems(playerId);

        appendInventoryItem(items.get(PlayerInfo.getItemHead(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemGlasses(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemShirts(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemPants(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemGlove(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemShoes(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemSocks(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemWrist(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemArm(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemKnee(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemEar(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemNeck(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemMask(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemMuffler(playerId)), msg);
        appendInventoryItem(items.get(PlayerInfo.getItemPackage(playerId)), msg);
    }

    public static void appendInventorySkillsInUse(int playerId, ServerMessage msg) {
        Skill[] skills = PlayerInfo.getInventorySkills(playerId).values().stream()
                .filter(s -> s.getSelectionIndex() > 0).toArray(Skill[]::new);

        for (int i = 0; i < 30; i++) {
            appendInventorySkill(i < skills.length ? skills[i] : null, msg);
        }
    }

    public static void appendInventoryCelebrationsInUse(int playerId, ServerMessage msg) {
        Celebration[] celebrations = PlayerInfo.getInventoryCelebration(playerId).values()
                .stream().filter(c -> c.getSelectionIndex() > 0).toArray(Celebration[]::new);

        for (int i = 0; i < 5; i++) {
            appendInventoryCelebration(i < celebrations.length ? celebrations[i] : null, msg);
        }
    }

    public static void appendClubUniform(int clubId, ServerMessage msg) {
        msg.append(ClubInfo.getUniformHomeShirts(clubId));
        msg.append(ClubInfo.getUniformHomePants(clubId));
        msg.append(ClubInfo.getUniformHomeSocks(clubId));
        msg.append(ClubInfo.getUniformHomeWrist(clubId));

        msg.append(ClubInfo.getUniformAwayShirts(clubId));
        msg.append(ClubInfo.getUniformAwayPants(clubId));
        msg.append(ClubInfo.getUniformAwaySocks(clubId));
        msg.append(ClubInfo.getUniformAwayWrist(clubId));
    }
}
