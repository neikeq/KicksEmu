package com.neikeq.kicksemu.network.packets.out;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.PlayerStats;
import com.neikeq.kicksemu.game.clubs.ClubInfo;
import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.rooms.match.MatchResult;
import com.neikeq.kicksemu.game.rooms.match.PlayerResult;
import com.neikeq.kicksemu.game.rooms.match.TeamResult;
import com.neikeq.kicksemu.game.users.UserInfo;

import java.sql.Connection;

class MessageUtils {

    public static void appendResult(byte result, ServerMessage msg) {
        msg.append(result);
        msg.append(result == 0 ? result : (byte)255);
    }

    public static void appendQuestInfo(int playerId, ServerMessage msg, Connection ... con) {
        msg.append(PlayerInfo.getCurrentQuest(playerId, con));
        msg.append(PlayerInfo.getRemainingQuestMatches(playerId, con));
    }

    public static void appendTutorialInfo(int playerId, ServerMessage msg, Connection ... con) {
        msg.append(PlayerInfo.getTutorialDribbling(playerId, con));
        msg.append(PlayerInfo.getTutorialPassing(playerId, con));
        msg.append(PlayerInfo.getTutorialShooting(playerId, con));
        msg.append(PlayerInfo.getTutorialDefense(playerId, con));
    }

    public static void appendCharacterInfo(int playerId, int ownerId,
                                           ServerMessage msg, Connection ... con) {
        msg.append(PlayerInfo.getLevel(playerId, con));
        msg.append(PlayerInfo.getExperience(playerId, con));
        msg.append(PlayerInfo.getStatsPoints(playerId, con));

        msg.append(UserInfo.getKash(ownerId));

        msg.append(PlayerInfo.getPoints(playerId, con));
        msg.appendZeros(8);
        msg.append(PlayerInfo.getTicketsKash(playerId, con));
        msg.append(PlayerInfo.getTicketsPoints(playerId, con));
    }

    public static void appendDefaultClothes(int playerId, ServerMessage msg, Connection ... con) {
        msg.append(PlayerInfo.getDefaultHead(playerId, con));
        msg.append(PlayerInfo.getDefaultShirts(playerId, con));
        msg.append(PlayerInfo.getDefaultPants(playerId, con));
        msg.append(PlayerInfo.getDefaultShoes(playerId, con));
    }

    public static void appendInventoryItem(Item item, ServerMessage msg) {
        // If the item is null we replace it with an empty one
        if (item == null) {
            item = new Item();
        }

        msg.append(item.getInventoryId());
        msg.append(item.getId());
        msg.append(item.isSelected());
        msg.append(item.getExpiration() != null ? item.getExpiration().toInt() : 0);
        msg.append(item.getStatsBonusOne());
        msg.append(item.getStatsBonusTwo());
        msg.append(item.getRemainUsages());
        msg.append(item.getExpiration() != null && item.getExpiration().isPermanent() ?
                0 : (int) (item.getTimestampExpire().getTime() / 1000));
        msg.append(item.isVisible());
    }

    public static void appendInventoryTraining(Training training, ServerMessage msg) {
        // If the training is null we replace it with an empty one
        if (training == null) {
            training = new Training();
        }

        msg.append(training.getInventoryId());
        msg.append(training.getId());
        msg.appendZeros(14);
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
        msg.append(skill.getExpiration() != null && skill.getExpiration().isPermanent() ?
                0 : (int) (skill.getTimestampExpire().getTime() / 1000));
        msg.append(skill.isVisible());
    }

    public static void appendInventoryCelebration(Celebration cele, ServerMessage msg) {
        // If the celebration is null we replace it with an empty one
        if (cele == null) {
            cele = new Celebration();
        }

        msg.append(cele.getInventoryId());
        msg.append((short)cele.getId());
        msg.append(cele.getSelectionIndex());
        msg.append(cele.getExpiration() != null ? cele.getExpiration().toInt() : 0);
        msg.appendZeros(8);
        msg.append(cele.getExpiration() != null && cele.getExpiration().isPermanent() ?
                0 : (int) (cele.getTimestampExpire().getTime() / 1000));
        msg.append(cele.isVisible());
    }

    public static void appendStats(int playerId, ServerMessage msg, Connection ... con) {
        msg.append(PlayerInfo.getStatsRunning(playerId, con));
        msg.append(PlayerInfo.getStatsEndurance(playerId, con));
        msg.append(PlayerInfo.getStatsAgility(playerId, con));
        msg.append(PlayerInfo.getStatsBallControl(playerId, con));
        msg.append(PlayerInfo.getStatsDribbling(playerId, con));
        msg.append(PlayerInfo.getStatsStealing(playerId, con));
        msg.append(PlayerInfo.getStatsTackling(playerId, con));
        msg.append(PlayerInfo.getStatsHeading(playerId, con));
        msg.append(PlayerInfo.getStatsShortShots(playerId, con));
        msg.append(PlayerInfo.getStatsLongShots(playerId, con));
        msg.append(PlayerInfo.getStatsCrossing(playerId, con));
        msg.append(PlayerInfo.getStatsShortPasses(playerId, con));
        msg.append(PlayerInfo.getStatsLongPasses(playerId, con));
        msg.append(PlayerInfo.getStatsMarking(playerId, con));
        msg.append(PlayerInfo.getStatsGoalkeeping(playerId, con));
        msg.append(PlayerInfo.getStatsPunching(playerId, con));
        msg.append(PlayerInfo.getStatsDefense(playerId, con));
    }

    public static void appendStatsTraining(int playerId, ServerMessage msg, Connection ... con) {
        PlayerStats learnStats = PlayerInfo.getTrainingStats(playerId, con);

        msg.append(learnStats.getRunning());
        msg.append(learnStats.getEndurance());
        msg.append(learnStats.getAgility());
        msg.append(learnStats.getBallControl());
        msg.append(learnStats.getDribbling());
        msg.append(learnStats.getStealing());
        msg.append(learnStats.getTackling());
        msg.append(learnStats.getHeading());
        msg.append(learnStats.getShortShots());
        msg.append(learnStats.getLongShots());
        msg.append(learnStats.getCrossing());
        msg.append(learnStats.getShortPasses());
        msg.append(learnStats.getLongPasses());
        msg.append(learnStats.getMarking());
        msg.append(learnStats.getGoalkeeping());
        msg.append(learnStats.getPunching());
        msg.append(learnStats.getDefense());
    }

    public static void appendStatsBonus(int playerId, ServerMessage msg, Connection ... con) {
        PlayerStats bonusStats = PlayerInfo.getBonusStats(playerId, con);

        msg.append(bonusStats.getRunning());
        msg.append(bonusStats.getEndurance());
        msg.append(bonusStats.getAgility());
        msg.append(bonusStats.getBallControl());
        msg.append(bonusStats.getDribbling());
        msg.append(bonusStats.getStealing());
        msg.append(bonusStats.getTackling());
        msg.append(bonusStats.getHeading());
        msg.append(bonusStats.getShortShots());
        msg.append(bonusStats.getLongShots());
        msg.append(bonusStats.getCrossing());
        msg.append(bonusStats.getShortPasses());
        msg.append(bonusStats.getLongPasses());
        msg.append(bonusStats.getMarking());
        msg.append(bonusStats.getGoalkeeping());
        msg.append(bonusStats.getPunching());
        msg.append(bonusStats.getDefense());
    }

    public static void appendHistory(int playerId, ServerMessage msg, Connection ... con) {
        msg.append(PlayerInfo.getHistoryMatches(playerId, con));
        msg.append(PlayerInfo.getHistoryWins(playerId, con));
        msg.append(PlayerInfo.getHistoryDraws(playerId, con));
        msg.append(PlayerInfo.getHistoryMom(playerId, con));
        msg.append(PlayerInfo.getHistoryValidGoals(playerId, con));
        msg.append(PlayerInfo.getHistoryValidAssists(playerId, con));
        msg.append(PlayerInfo.getHistoryValidInterception(playerId, con));
        msg.append(PlayerInfo.getHistoryValidShooting(playerId, con));
        msg.append(PlayerInfo.getHistoryValidStealing(playerId, con));
        msg.append(PlayerInfo.getHistoryValidTackling(playerId, con));
        msg.appendZeros(4);
        msg.append(PlayerInfo.getHistoryShooting(playerId, con));
        msg.append(PlayerInfo.getHistoryStealing(playerId, con));
        msg.append(PlayerInfo.getHistoryTackling(playerId, con));
        msg.appendZeros(4);
        msg.append(PlayerInfo.getHistoryTotalPoints(playerId, con));
    }

    public static void appendHistoryLastMonth(int playerId, ServerMessage msg, Connection ... con) {
        msg.append(PlayerInfo.getHistoryMonthMatches(playerId, con));
        msg.append(PlayerInfo.getHistoryMonthWins(playerId, con));
        msg.append(PlayerInfo.getHistoryMonthDraws(playerId, con));
        msg.append(PlayerInfo.getHistoryMonthMom(playerId, con));
        msg.append(PlayerInfo.getHistoryMonthValidGoals(playerId, con));
        msg.append(PlayerInfo.getHistoryMonthValidAssists(playerId, con));
        msg.append(PlayerInfo.getHistoryMonthValidInterception(playerId, con));
        msg.append(PlayerInfo.getHistoryMonthValidShooting(playerId, con));
        msg.append(PlayerInfo.getHistoryMonthValidStealing(playerId, con));
        msg.append(PlayerInfo.getHistoryMonthValidTackling(playerId, con));
        msg.appendZeros(4);
        msg.append(PlayerInfo.getHistoryMonthShooting(playerId, con));
        msg.append(PlayerInfo.getHistoryMonthStealing(playerId, con));
        msg.append(PlayerInfo.getHistoryMonthTackling(playerId, con));
        msg.appendZeros(4);
        msg.append(PlayerInfo.getHistoryMonthTotalPoints(playerId, con));
    }

    public static void appendMatchHistory(PlayerResult playerResult, Room room, MatchResult result,
                                          ServerMessage msg, Connection ... con) {
        int id = playerResult.getPlayerId();
        boolean training = room.getTrainingFactor() <= 0;

        TeamResult tr = !training ? room.getPlayerTeam(id) == RoomTeam.RED ?
                result.getRedTeam() : result.getBlueTeam() :
                new TeamResult((short)-1, (short)0, (short)0, (short)0, (short)0, (short)0,
                        (short)0, (short)0, (short)0);

        PlayerResult pr = !training ? playerResult : new PlayerResult((short)-1, (short)0,
                (short)0, (short)0, (short)0, (short)0, (short)0, (short)0, (short)0);

        msg.append(PlayerInfo.getHistoryMatches(id, con) + (training ? 0 : 1));
        msg.append(PlayerInfo.getHistoryWins(id, con) +
                (tr.getResult() == 1 ? 1 : 0));
        msg.append(PlayerInfo.getHistoryDraws(id, con) +
                (tr.getResult() == 0 ? 1 : 0));
        msg.append(PlayerInfo.getHistoryMom(id, con) + (result.getMom() == id ? 1 : 0));
        msg.append(PlayerInfo.getHistoryValidGoals(id, con) + pr.getGoals());
        msg.append(PlayerInfo.getHistoryValidAssists(id, con) + pr.getAssists());
        msg.append(PlayerInfo.getHistoryValidInterception(id, con) + pr.getBlocks());
        msg.append(PlayerInfo.getHistoryValidShooting(id, con) + pr.getShots());
        msg.append(PlayerInfo.getHistoryValidStealing(id, con) + pr.getSteals());
        msg.append(PlayerInfo.getHistoryValidTackling(id, con) + pr.getTackles());
        msg.appendZeros(4);
        msg.append(PlayerInfo.getHistoryShooting(id, con));
        msg.append(PlayerInfo.getHistoryStealing(id, con));
        msg.append(PlayerInfo.getHistoryTackling(id, con));
        msg.appendZeros(4);
        msg.append(PlayerInfo.getHistoryTotalPoints(id, con) + pr.getVotePoints());
        msg.append(PlayerInfo.getHistoryMonthMatches(id, con) + (training ? 0 : 1));
        msg.append(PlayerInfo.getHistoryMonthWins(id, con) +
                (tr.getResult() == 1 ? 1 : 0));
        msg.append(PlayerInfo.getHistoryMonthDraws(id, con) +
                (tr.getResult() == 0 ? 1 : 0));
        msg.append(PlayerInfo.getHistoryMonthMom(id, con) + (result.getMom() == id ? 1 : 0));
        msg.append(PlayerInfo.getHistoryMonthValidGoals(id, con) + pr.getGoals());
        msg.append(PlayerInfo.getHistoryMonthValidAssists(id, con) + pr.getAssists());
        msg.append(PlayerInfo.getHistoryMonthValidInterception(id, con) + pr.getBlocks());
        msg.append(PlayerInfo.getHistoryMonthValidShooting(id, con) + pr.getShots());
        msg.append(PlayerInfo.getHistoryMonthValidStealing(id, con) + pr.getSteals());
        msg.append(PlayerInfo.getHistoryMonthValidTackling(id, con) + pr.getTackles());
        msg.appendZeros(4);
        msg.append(PlayerInfo.getHistoryMonthShooting(id, con));
        msg.append(PlayerInfo.getHistoryMonthStealing(id, con));
        msg.append(PlayerInfo.getHistoryMonthTackling(id, con));
        msg.appendZeros(4);
        msg.append(PlayerInfo.getHistoryMonthTotalPoints(id, con) + pr.getVotePoints());
    }

    public static void appendRanking(int playerId, ServerMessage msg, Connection ... con) {
        msg.append(PlayerInfo.getRankingMatches(playerId, con));
        msg.append(PlayerInfo.getRankingWins(playerId, con));
        msg.append(PlayerInfo.getRankingPoints(playerId, con));
        msg.append(PlayerInfo.getRankingMom(playerId, con));
        msg.append(PlayerInfo.getRankingValidGoals(playerId, con));
        msg.append(PlayerInfo.getRankingValidAssists(playerId, con));
        msg.append(PlayerInfo.getRankingValidInterception(playerId, con));
        msg.append(PlayerInfo.getRankingValidShooting(playerId, con));
        msg.append(PlayerInfo.getRankingValidStealing(playerId, con));
        msg.append(PlayerInfo.getRankingValidTackling(playerId, con));
        msg.appendZeros(2);
        msg.append(PlayerInfo.getRankingAvgGoals(playerId, con));
        msg.append(PlayerInfo.getRankingAvgAssists(playerId, con));
        msg.append(PlayerInfo.getRankingAvgInterception(playerId, con));
        msg.append(PlayerInfo.getRankingAvgShooting(playerId, con));
        msg.append(PlayerInfo.getRankingAvgStealing(playerId, con));
        msg.append(PlayerInfo.getRankingAvgTackling(playerId, con));
        msg.appendZeros(2);
        msg.append(PlayerInfo.getRankingAvgVotePoints(playerId, con));
        msg.append(PlayerInfo.getRankingShooting(playerId, con));
        msg.append(PlayerInfo.getRankingStealing(playerId, con));
        msg.append(PlayerInfo.getRankingTackling(playerId, con));
        msg.appendZeros(2);
        msg.append(PlayerInfo.getRankingTotalPoints(playerId, con));
    }

    public static void appendRankingLastMonth(int playerId, ServerMessage msg, Connection ... con) {
        msg.append(PlayerInfo.getRankingMonthMatches(playerId, con));
        msg.append(PlayerInfo.getRankingMonthWins(playerId, con));
        msg.append(PlayerInfo.getRankingMonthPoints(playerId, con));
        msg.append(PlayerInfo.getRankingMonthMom(playerId, con));
        msg.append(PlayerInfo.getRankingMonthValidGoals(playerId, con));
        msg.append(PlayerInfo.getRankingMonthValidAssists(playerId, con));
        msg.append(PlayerInfo.getRankingMonthValidInterception(playerId, con));
        msg.append(PlayerInfo.getRankingMonthValidShooting(playerId, con));
        msg.append(PlayerInfo.getRankingMonthValidStealing(playerId, con));
        msg.append(PlayerInfo.getRankingMonthValidTackling(playerId, con));
        msg.appendZeros(2);
        msg.append(PlayerInfo.getRankingMonthAvgGoals(playerId, con));
        msg.append(PlayerInfo.getRankingMonthAvgAssists(playerId, con));
        msg.append(PlayerInfo.getRankingMonthAvgInterception(playerId, con));
        msg.append(PlayerInfo.getRankingMonthAvgShooting(playerId, con));
        msg.append(PlayerInfo.getRankingMonthAvgStealing(playerId, con));
        msg.append(PlayerInfo.getRankingMonthAvgTackling(playerId, con));
        msg.appendZeros(2);
        msg.append(PlayerInfo.getRankingMonthAvgVotePoints(playerId, con));
        msg.append(PlayerInfo.getRankingMonthShooting(playerId, con));
        msg.append(PlayerInfo.getRankingMonthStealing(playerId, con));
        msg.append(PlayerInfo.getRankingMonthTackling(playerId, con));
        msg.appendZeros(2);
        msg.append(PlayerInfo.getRankingMonthTotalPoints(playerId, con));
    }

    private static void appendItemInUse(Item item, ServerMessage msg) {
        msg.append(item != null ? item.getId() : 0);
        msg.append(item != null, 4);
        msg.appendZeros(20);
    }

    public static void appendItemsInUse(int playerId, ServerMessage msg, Connection ... con) {
        appendItemInUse(PlayerInfo.getItemHead(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemGlasses(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemShirts(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemPants(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemGlove(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemShoes(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemSocks(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemWrist(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemArm(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemKnee(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemEar(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemNeck(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemMask(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemMuffler(playerId, con), msg);
        appendItemInUse(PlayerInfo.getItemPackage(playerId, con), msg);
    }

    public static void appendInventoryItemsInUse(int playerId, ServerMessage msg, Connection ... con) {
        appendInventoryItem(PlayerInfo.getItemHead(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemGlasses(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemShirts(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemPants(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemGlove(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemShoes(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemSocks(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemWrist(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemArm(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemKnee(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemEar(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemNeck(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemMask(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemMuffler(playerId, con), msg);
        appendInventoryItem(PlayerInfo.getItemPackage(playerId, con), msg);
    }

    public static void appendInventorySkillsInUse(int playerId, ServerMessage msg, Connection ... con) {
        Skill[] skills = PlayerInfo.getInventorySkills(playerId, con).values().stream()
                .filter(s -> s.getSelectionIndex() > 0).toArray(Skill[]::new);

        for (int i = 0; i < 30; i++) {
            appendInventorySkill(i < skills.length ? skills[i] : null, msg);
        }
    }

    public static void appendInventoryCelebrationsInUse(int playerId, ServerMessage msg, Connection ... con) {
        Celebration[] celebrations = PlayerInfo.getInventoryCelebration(playerId, con).values()
                .stream().filter(c -> c.getSelectionIndex() > 0).toArray(Celebration[]::new);

        for (int i = 0; i < 5; i++) {
            appendInventoryCelebration(i < celebrations.length ? celebrations[i] : null, msg);
        }
    }

    public static void appendClubUniform(int clubId, ServerMessage msg, Connection ... con) {
        msg.append(ClubInfo.getUniformHomeShirts(clubId, con));
        msg.append(ClubInfo.getUniformHomePants(clubId, con));
        msg.append(ClubInfo.getUniformHomeSocks(clubId, con));
        msg.append(ClubInfo.getUniformHomeWrist(clubId, con));

        msg.append(ClubInfo.getUniformAwayShirts(clubId, con));
        msg.append(ClubInfo.getUniformAwayPants(clubId, con));
        msg.append(ClubInfo.getUniformAwaySocks(clubId, con));
        msg.append(ClubInfo.getUniformAwayWrist(clubId, con));
    }
}
