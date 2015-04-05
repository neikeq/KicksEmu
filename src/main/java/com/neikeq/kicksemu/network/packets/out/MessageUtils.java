package com.neikeq.kicksemu.network.packets.out;

import com.neikeq.kicksemu.game.characters.PlayerHistory;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.PlayerRanking;
import com.neikeq.kicksemu.game.characters.PlayerStats;
import com.neikeq.kicksemu.game.characters.QuestState;
import com.neikeq.kicksemu.game.characters.TutorialState;
import com.neikeq.kicksemu.game.clubs.ClubInfo;
import com.neikeq.kicksemu.game.clubs.ClubUniform;
import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.DefaultClothes;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.rooms.match.MatchResult;
import com.neikeq.kicksemu.game.rooms.match.PlayerResult;
import com.neikeq.kicksemu.game.rooms.match.TeamResult;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class MessageUtils {

    public static void appendResult(byte result, ServerMessage msg) {
        msg.append(result);
        msg.append(result == 0 ? result : (byte)255);
    }

    public static void appendQuestInfo(int playerId, ServerMessage msg, Connection ... con) {
        QuestState questState = PlayerInfo.getQuestState(playerId, con);

        msg.append(questState.getCurrentQuest());
        msg.append(questState.getRemainMatches());
    }

    public static void appendTutorialInfo(int playerId, ServerMessage msg, Connection ... con) {
        TutorialState tutorialState = PlayerInfo.getTutorialState(playerId, con);

        msg.append(tutorialState.getDribbling());
        msg.append(tutorialState.getPassing());
        msg.append(tutorialState.getShooting());
        msg.append(tutorialState.getDefense());
    }

    public static void appendCharacterInfo(int playerId, ServerMessage msg, Connection ... con) {
        String query = "SELECT level, experience, stats_points, owner, points, " +
                "tickets_kash, tickets_points FROM characters WHERE id = ? LIMIT 1;";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, playerId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        msg.append(rs.getShort("level"));
                        msg.append(rs.getInt("experience"));
                        msg.append(rs.getShort("stats_points"));
                        msg.append(UserInfo.getKash(rs.getInt("owner")));
                        msg.append(rs.getInt("points"));
                        msg.appendZeros(8);
                        msg.append(rs.getShort("tickets_kash"));
                        msg.append(rs.getShort("tickets_points"));
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void appendDefaultClothes(int playerId, ServerMessage msg, Connection ... con) {
        DefaultClothes defaultClothes = PlayerInfo.getDefaultClothes(playerId, con);

        msg.append(defaultClothes.getHead());
        msg.append(defaultClothes.getShirts());
        msg.append(defaultClothes.getPants());
        msg.append(defaultClothes.getShoes());
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
        msg.append(item.getBonusOne());
        msg.append(item.getBonusTwo());
        msg.append(item.getUsages());

        if (item.getExpiration() != null &&
                (item.getExpiration().isPermanent() || item.getExpiration().isUsage())) {
            msg.append(0);
        } else {
            msg.append((int) (item.getTimestampExpire().getTime() / 1000));
        }

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
        PlayerStats stats = PlayerInfo.getStats(playerId, con);

        msg.append(stats.getRunning());
        msg.append(stats.getEndurance());
        msg.append(stats.getAgility());
        msg.append(stats.getBallControl());
        msg.append(stats.getDribbling());
        msg.append(stats.getStealing());
        msg.append(stats.getTackling());
        msg.append(stats.getHeading());
        msg.append(stats.getShortShots());
        msg.append(stats.getLongShots());
        msg.append(stats.getCrossing());
        msg.append(stats.getShortPasses());
        msg.append(stats.getLongPasses());
        msg.append(stats.getMarking());
        msg.append(stats.getGoalkeeping());
        msg.append(stats.getPunching());
        msg.append(stats.getDefense());
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
        PlayerHistory history = PlayerInfo.getHistory(playerId, con);

        msg.append(history.getMatches());
        msg.append(history.getWins());
        msg.append(history.getDraws());
        msg.append(history.getMom());
        msg.append(history.getValidGoals());
        msg.append(history.getValidAssists());
        msg.append(history.getValidInterception());
        msg.append(history.getValidShooting());
        msg.append(history.getValidStealing());
        msg.append(history.getValidTackling());
        msg.appendZeros(4);
        msg.append(history.getShooting());
        msg.append(history.getStealing());
        msg.append(history.getTackling());
        msg.appendZeros(4);
        msg.append(history.getTotalPoints());
    }

    public static void appendHistoryMonth(int playerId, ServerMessage msg, Connection... con) {
        PlayerHistory monthHistory = PlayerInfo.getMonthHistory(playerId, con);

        msg.append(monthHistory.getMatches());
        msg.append(monthHistory.getWins());
        msg.append(monthHistory.getDraws());
        msg.append(monthHistory.getMom());
        msg.append(monthHistory.getValidGoals());
        msg.append(monthHistory.getValidAssists());
        msg.append(monthHistory.getValidInterception());
        msg.append(monthHistory.getValidShooting());
        msg.append(monthHistory.getValidStealing());
        msg.append(monthHistory.getValidTackling());
        msg.appendZeros(4);
        msg.append(monthHistory.getShooting());
        msg.append(monthHistory.getStealing());
        msg.append(monthHistory.getTackling());
        msg.appendZeros(4);
        msg.append(monthHistory.getTotalPoints());
    }

    public static void appendMatchHistory(PlayerResult playerResult, Room room, MatchResult result,
                                          ServerMessage msg, Connection ... con) {
        int id = playerResult.getPlayerId();
        boolean training = room.getTrainingFactor() == 0;

        TeamResult tr = !training ?
                (room.getPlayerTeam(id) == RoomTeam.RED ?
                        result.getRedTeam() : result.getBlueTeam()) :
                new TeamResult((short)-1, (short)0, (short)0, (short)0, (short)0, (short)0,
                        (short)0, (short)0, (short)0);

        PlayerResult pr = !training ? playerResult : new PlayerResult(-1, (short)0,
                (short)0, (short)0, (short)0, (short)0, (short)0, (short)0, (short)0);

        PlayerHistory history = PlayerInfo.getHistory(id, con);

        msg.append(history.getMatches() + (training ? 0 : 1));
        msg.append(history.getWins() + (tr.getResult() == 1 ? 1 : 0));
        msg.append(history.getDraws() + (tr.getResult() == 0 ? 1 : 0));
        msg.append(history.getMom() + (result.getMom() == id ? 1 : 0));
        msg.append(history.getValidGoals() + pr.getGoals());
        msg.append(history.getValidAssists() + pr.getAssists());
        msg.append(history.getValidInterception() + pr.getBlocks());
        msg.append(history.getValidShooting() + pr.getShots());
        msg.append(history.getValidStealing() + pr.getSteals());
        msg.append(history.getValidTackling() + pr.getTackles());
        msg.appendZeros(4);
        msg.append(history.getShooting());
        msg.append(history.getStealing());
        msg.append(history.getTackling());
        msg.appendZeros(4);
        msg.append(history.getTotalPoints() + pr.getVotePoints());

        PlayerHistory monthHistory = PlayerInfo.getMonthHistory(id, con);

        msg.append(monthHistory.getMatches() + (training ? 0 : 1));
        msg.append(monthHistory.getWins() + (tr.getResult() == 1 ? 1 : 0));
        msg.append(monthHistory.getDraws() + (tr.getResult() == 0 ? 1 : 0));
        msg.append(monthHistory.getMom() + (result.getMom() == id ? 1 : 0));
        msg.append(monthHistory.getValidGoals() + pr.getGoals());
        msg.append(monthHistory.getValidAssists() + pr.getAssists());
        msg.append(monthHistory.getValidInterception() + pr.getBlocks());
        msg.append(monthHistory.getValidShooting() + pr.getShots());
        msg.append(monthHistory.getValidStealing() + pr.getSteals());
        msg.append(monthHistory.getValidTackling() + pr.getTackles());
        msg.appendZeros(4);
        msg.append(monthHistory.getShooting());
        msg.append(monthHistory.getStealing());
        msg.append(monthHistory.getTackling());
        msg.appendZeros(4);
        msg.append(monthHistory.getTotalPoints() + pr.getVotePoints());
    }

    public static void appendRanking(int playerId, ServerMessage msg, Connection ... con) {
        msg.append(PlayerRanking.getRankingMatches(playerId, con));
        msg.append(PlayerRanking.getRankingWins(playerId, con));
        msg.append(PlayerRanking.getRankingPoints(playerId, con));
        msg.append(PlayerRanking.getRankingMom(playerId, con));
        msg.append(PlayerRanking.getRankingValidGoals(playerId, con));
        msg.append(PlayerRanking.getRankingValidAssists(playerId, con));
        msg.append(PlayerRanking.getRankingValidInterception(playerId, con));
        msg.append(PlayerRanking.getRankingValidShooting(playerId, con));
        msg.append(PlayerRanking.getRankingValidStealing(playerId, con));
        msg.append(PlayerRanking.getRankingValidTackling(playerId, con));
        msg.appendZeros(2);
        msg.append(PlayerRanking.getRankingAvgGoals(playerId, con));
        msg.append(PlayerRanking.getRankingAvgAssists(playerId, con));
        msg.append(PlayerRanking.getRankingAvgInterception(playerId, con));
        msg.append(PlayerRanking.getRankingAvgShooting(playerId, con));
        msg.append(PlayerRanking.getRankingAvgStealing(playerId, con));
        msg.append(PlayerRanking.getRankingAvgTackling(playerId, con));
        msg.appendZeros(2);
        msg.append(PlayerRanking.getRankingAvgVotePoints(playerId, con));
        msg.append(PlayerRanking.getRankingShooting(playerId, con));
        msg.append(PlayerRanking.getRankingStealing(playerId, con));
        msg.append(PlayerRanking.getRankingTackling(playerId, con));
        msg.appendZeros(2);
        msg.append(PlayerRanking.getRankingTotalPoints(playerId, con));
    }

    public static void appendRankingLastMonth(int playerId,
                                              ServerMessage msg, Connection ... con) {
        msg.append(PlayerRanking.getRankingMonthMatches(playerId, con));
        msg.append(PlayerRanking.getRankingMonthWins(playerId, con));
        msg.append(PlayerRanking.getRankingMonthPoints(playerId, con));
        msg.append(PlayerRanking.getRankingMonthMom(playerId, con));
        msg.append(PlayerRanking.getRankingMonthValidGoals(playerId, con));
        msg.append(PlayerRanking.getRankingMonthValidAssists(playerId, con));
        msg.append(PlayerRanking.getRankingMonthValidInterception(playerId, con));
        msg.append(PlayerRanking.getRankingMonthValidShooting(playerId, con));
        msg.append(PlayerRanking.getRankingMonthValidStealing(playerId, con));
        msg.append(PlayerRanking.getRankingMonthValidTackling(playerId, con));
        msg.appendZeros(2);
        msg.append(PlayerRanking.getRankingMonthAvgGoals(playerId, con));
        msg.append(PlayerRanking.getRankingMonthAvgAssists(playerId, con));
        msg.append(PlayerRanking.getRankingMonthAvgInterception(playerId, con));
        msg.append(PlayerRanking.getRankingMonthAvgShooting(playerId, con));
        msg.append(PlayerRanking.getRankingMonthAvgStealing(playerId, con));
        msg.append(PlayerRanking.getRankingMonthAvgTackling(playerId, con));
        msg.appendZeros(2);
        msg.append(PlayerRanking.getRankingMonthAvgVotePoints(playerId, con));
        msg.append(PlayerRanking.getRankingMonthShooting(playerId, con));
        msg.append(PlayerRanking.getRankingMonthStealing(playerId, con));
        msg.append(PlayerRanking.getRankingMonthTackling(playerId, con));
        msg.appendZeros(2);
        msg.append(PlayerRanking.getRankingMonthTotalPoints(playerId, con));
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

    public static void appendInventoryItemsInUse(int playerId,
                                                 ServerMessage msg, Connection ... con) {
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

    public static void appendInventorySkillsInUse(int playerId, ServerMessage msg,
                                                  Connection ... con) {
        Skill[] skills = PlayerInfo.getInventorySkills(playerId, con).values().stream()
                .filter(s -> s.getSelectionIndex() > 0).toArray(Skill[]::new);

        for (int i = 0; i < 30; i++) {
            appendInventorySkill(i < skills.length ? skills[i] : null, msg);
        }
    }

    public static void appendInventoryCelebrationsInUse(int playerId, ServerMessage msg,
                                                        Connection ... con) {
        Celebration[] celebrations = PlayerInfo.getInventoryCelebration(playerId, con).values()
                .stream().filter(c -> c.getSelectionIndex() > 0).toArray(Celebration[]::new);

        for (int i = 0; i < 5; i++) {
            appendInventoryCelebration(i < celebrations.length ? celebrations[i] : null, msg);
        }
    }

    public static void appendClubUniform(int clubId, ServerMessage msg, Connection ... con) {
        ClubUniform clubUniform = ClubInfo.getUniform(clubId, con);

        msg.append(clubUniform.getHomeShirts());
        msg.append(clubUniform.getHomePants());
        msg.append(clubUniform.getHomeSocks());
        msg.append(clubUniform.getHomeWrist());

        msg.append(clubUniform.getAwayShirts());
        msg.append(clubUniform.getAwayPants());
        msg.append(clubUniform.getAwaySocks());
        msg.append(clubUniform.getAwayWrist());
    }
}
