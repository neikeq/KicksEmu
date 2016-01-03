package com.neikeq.kicksemu.network.packets.out;

import com.neikeq.kicksemu.game.characters.types.PlayerHistory;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.types.PlayerRanking;
import com.neikeq.kicksemu.game.characters.types.PlayerStats;
import com.neikeq.kicksemu.game.misc.quests.QuestState;
import com.neikeq.kicksemu.game.misc.tutorial.TutorialState;
import com.neikeq.kicksemu.game.clubs.ClubInfo;
import com.neikeq.kicksemu.game.clubs.ClubUniform;
import com.neikeq.kicksemu.game.inventory.products.Celebration;
import com.neikeq.kicksemu.game.inventory.products.DefaultClothes;
import com.neikeq.kicksemu.game.inventory.products.Item;
import com.neikeq.kicksemu.game.inventory.products.Skill;
import com.neikeq.kicksemu.game.inventory.products.Training;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.rooms.enums.VictoryResult;
import com.neikeq.kicksemu.game.rooms.match.MatchResult;
import com.neikeq.kicksemu.game.rooms.match.PlayerResult;
import com.neikeq.kicksemu.game.rooms.match.TeamResult;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class MessageUtils {

    public static void appendQuestInfo(int playerId, ServerMessage msg, Connection ... con) {
        QuestState questState = PlayerInfo.getQuestState(playerId, con);

        msg.writeShort(questState.getCurrentQuest());
        msg.writeShort(questState.getRemainMatches());
    }

    public static void appendTutorialInfo(int playerId, ServerMessage msg, Connection ... con) {
        TutorialState tutorialState = PlayerInfo.getTutorialState(playerId, con);

        msg.writeByte(tutorialState.getDribbling());
        msg.writeByte(tutorialState.getPassing());
        msg.writeByte(tutorialState.getShooting());
        msg.writeByte(tutorialState.getDefense());
    }

    public static void appendCharacterInfo(int playerId, ServerMessage msg, Connection ... con) {
        try {
            Connection connection = (con.length > 0) ? con[0] : MySqlManager.getConnection();

            final String query = "SELECT level, experience, stats_points, owner, points, " +
                    "tickets_kash, tickets_points FROM characters WHERE id = ? LIMIT 1;";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, playerId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        msg.writeShort(rs.getShort("level"));
                        msg.writeInt(rs.getInt("experience"));
                        msg.writeShort(rs.getShort("stats_points"));
                        msg.writeInt(UserInfo.getCash(rs.getInt("owner")));
                        msg.writeInt(rs.getInt("points"));
                        msg.writeZeros(8);
                        msg.writeShort(rs.getShort("tickets_kash"));
                        msg.writeShort(rs.getShort("tickets_points"));
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            Output.println("Exception when appending character info to message: " +
                    e.getMessage(), Level.DEBUG);
        }
    }

    public static void appendDefaultClothes(DefaultClothes defaultClothes,
                                            ServerMessage msg) {
        msg.writeInt(defaultClothes.getHead());
        msg.writeInt(defaultClothes.getShirts());
        msg.writeInt(defaultClothes.getPants());
        msg.writeInt(defaultClothes.getShoes());
    }

    public static void appendInventoryItem(Item item, ServerMessage msg) {
        // If the item is null we replace it with an empty one
        if (item == null) {
            item = new Item();
        }

        msg.writeInt(item.getInventoryId());
        msg.writeInt(item.getId());
        msg.writeBool(item.isSelected());
        msg.writeInt((item.getExpiration() != null) ? item.getExpiration().toInt() : 0);
        msg.writeInt(item.getBonusOne());
        msg.writeInt(item.getBonusTwo());
        msg.writeShort(item.getUsages());

        if ((item.getExpiration() != null) &&
                (item.getExpiration().isPermanent() || item.getExpiration().isUsage())) {
            msg.writeInt(0);
        } else {
            msg.writeInt((int) (item.getTimestampExpire().getTime() / 1000));
        }

        msg.writeBool(item.isVisible());
    }

    public static void appendInventoryTraining(Training training, ServerMessage msg) {
        // If the training is null we replace it with an empty one
        if (training == null) {
            training = new Training();
        }

        msg.writeInt(training.getInventoryId());
        msg.writeInt(training.getId());
        msg.writeZeros(14);
        msg.writeBool(training.isVisible());
    }

    public static void appendInventorySkill(Skill skill, ServerMessage msg) {
        // If the skill is null we replace it with an empty one
        if (skill == null) {
            skill = new Skill();
        }

        msg.writeInt(skill.getInventoryId());
        msg.writeInt(skill.getId());
        msg.writeByte(skill.getSelectionIndex());
        msg.writeInt((skill.getExpiration() != null) ? skill.getExpiration().toInt() : 0);
        msg.writeZeros(8);
        msg.writeInt(((skill.getExpiration() != null) && skill.getExpiration().isPermanent()) ?
                0 : (int) (skill.getTimestampExpire().getTime() / 1000));
        msg.writeBool(skill.isVisible());
    }

    public static void appendInventoryCelebration(Celebration cele, ServerMessage msg) {
        // If the celebration is null we replace it with an empty one
        if (cele == null) {
            cele = new Celebration();
        }

        msg.writeInt(cele.getInventoryId());
        msg.writeShort((short) cele.getId());
        msg.writeByte(cele.getSelectionIndex());
        msg.writeInt((cele.getExpiration() != null) ? cele.getExpiration().toInt() : 0);
        msg.writeZeros(8);
        msg.writeInt(((cele.getExpiration() != null) && cele.getExpiration().isPermanent()) ?
                0 : (int) (cele.getTimestampExpire().getTime() / 1000));
        msg.writeBool(cele.isVisible());
    }

    public static void appendStats(int playerId, ServerMessage msg, Connection ... con) {
        PlayerStats stats = PlayerInfo.getStats(playerId, con);

        msg.writeShort(stats.getRunning());
        msg.writeShort(stats.getEndurance());
        msg.writeShort(stats.getAgility());
        msg.writeShort(stats.getBallControl());
        msg.writeShort(stats.getDribbling());
        msg.writeShort(stats.getStealing());
        msg.writeShort(stats.getTackling());
        msg.writeShort(stats.getHeading());
        msg.writeShort(stats.getShortShots());
        msg.writeShort(stats.getLongShots());
        msg.writeShort(stats.getCrossing());
        msg.writeShort(stats.getShortPasses());
        msg.writeShort(stats.getLongPasses());
        msg.writeShort(stats.getMarking());
        msg.writeShort(stats.getGoalkeeping());
        msg.writeShort(stats.getPunching());
        msg.writeShort(stats.getDefense());
    }

    public static void appendStatsTraining(Session session, ServerMessage msg, Connection ... con) {
        PlayerStats learnStats = PlayerInfo.getTrainingStats(session, con);

        msg.writeShort(learnStats.getRunning());
        msg.writeShort(learnStats.getEndurance());
        msg.writeShort(learnStats.getAgility());
        msg.writeShort(learnStats.getBallControl());
        msg.writeShort(learnStats.getDribbling());
        msg.writeShort(learnStats.getStealing());
        msg.writeShort(learnStats.getTackling());
        msg.writeShort(learnStats.getHeading());
        msg.writeShort(learnStats.getShortShots());
        msg.writeShort(learnStats.getLongShots());
        msg.writeShort(learnStats.getCrossing());
        msg.writeShort(learnStats.getShortPasses());
        msg.writeShort(learnStats.getLongPasses());
        msg.writeShort(learnStats.getMarking());
        msg.writeShort(learnStats.getGoalkeeping());
        msg.writeShort(learnStats.getPunching());
        msg.writeShort(learnStats.getDefense());
    }

    public static void appendStatsBonus(Session session, ServerMessage msg, Connection ... con) {
        PlayerStats bonusStats = PlayerInfo.getBonusStats(session, con);

        msg.writeShort(bonusStats.getRunning());
        msg.writeShort(bonusStats.getEndurance());
        msg.writeShort(bonusStats.getAgility());
        msg.writeShort(bonusStats.getBallControl());
        msg.writeShort(bonusStats.getDribbling());
        msg.writeShort(bonusStats.getStealing());
        msg.writeShort(bonusStats.getTackling());
        msg.writeShort(bonusStats.getHeading());
        msg.writeShort(bonusStats.getShortShots());
        msg.writeShort(bonusStats.getLongShots());
        msg.writeShort(bonusStats.getCrossing());
        msg.writeShort(bonusStats.getShortPasses());
        msg.writeShort(bonusStats.getLongPasses());
        msg.writeShort(bonusStats.getMarking());
        msg.writeShort(bonusStats.getGoalkeeping());
        msg.writeShort(bonusStats.getPunching());
        msg.writeShort(bonusStats.getDefense());
    }

    public static void appendHistory(int playerId, ServerMessage msg, Connection ... con) {
        PlayerHistory history = PlayerInfo.getHistory(playerId, con);

        msg.writeInt(history.getMatches());
        msg.writeInt(history.getWins());
        msg.writeInt(history.getDraws());
        msg.writeInt(history.getMom());
        msg.writeInt(history.getValidGoals());
        msg.writeInt(history.getValidAssists());
        msg.writeInt(history.getValidInterception());
        msg.writeInt(history.getValidShooting());
        msg.writeInt(history.getValidStealing());
        msg.writeInt(history.getValidTackling());
        msg.writeZeros(4);
        msg.writeInt(history.getShooting());
        msg.writeInt(history.getStealing());
        msg.writeInt(history.getTackling());
        msg.writeZeros(4);
        msg.writeInt(history.getTotalPoints());
    }

    public static void appendHistoryMonth(int playerId, ServerMessage msg, Connection... con) {
        PlayerHistory monthHistory = PlayerInfo.getMonthHistory(playerId, con);

        msg.writeInt(monthHistory.getMatches());
        msg.writeInt(monthHistory.getWins());
        msg.writeInt(monthHistory.getDraws());
        msg.writeInt(monthHistory.getMom());
        msg.writeInt(monthHistory.getValidGoals());
        msg.writeInt(monthHistory.getValidAssists());
        msg.writeInt(monthHistory.getValidInterception());
        msg.writeInt(monthHistory.getValidShooting());
        msg.writeInt(monthHistory.getValidStealing());
        msg.writeInt(monthHistory.getValidTackling());
        msg.writeZeros(4);
        msg.writeInt(monthHistory.getShooting());
        msg.writeInt(monthHistory.getStealing());
        msg.writeInt(monthHistory.getTackling());
        msg.writeZeros(4);
        msg.writeInt(monthHistory.getTotalPoints());
    }

    public static void appendMatchHistory(PlayerResult playerResult, Room room, MatchResult result,
                                          ServerMessage msg, Connection ... con) {
        int id = playerResult.getPlayerId();
        boolean training = !room.trainingFactorAllowsRewards();

        TeamResult tr;

        if (training) {
            tr = new TeamResult();
        } else {
            tr = (room.getPlayerTeam(id) == RoomTeam.RED) ? result.getRedTeam() : result.getBlueTeam();
        }

        PlayerResult pr = !training ? playerResult : new PlayerResult(-1, (short) 0,
                (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0);

        PlayerHistory history = PlayerInfo.getHistory(id, con);

        msg.writeInt(history.getMatches() + (training ? 0 : 1));
        msg.writeInt(history.getWins() + ((tr.getResult() == VictoryResult.WIN) ? 1 : 0));
        msg.writeInt(history.getDraws() + ((tr.getResult() == VictoryResult.DRAW) ? 1 : 0));
        msg.writeInt(history.getMom() + ((result.getMom() == id) ? 1 : 0));
        msg.writeInt(history.getValidGoals() + pr.getGoals());
        msg.writeInt(history.getValidAssists() + pr.getAssists());
        msg.writeInt(history.getValidInterception() + pr.getBlocks());
        msg.writeInt(history.getValidShooting() + pr.getShots());
        msg.writeInt(history.getValidStealing() + pr.getSteals());
        msg.writeInt(history.getValidTackling() + pr.getTackles());
        msg.writeZeros(4);
        msg.writeInt(history.getShooting());
        msg.writeInt(history.getStealing());
        msg.writeInt(history.getTackling());
        msg.writeZeros(4);
        msg.writeInt(history.getTotalPoints() + pr.getVotePoints());

        PlayerHistory monthHistory = PlayerInfo.getMonthHistory(id, con);

        msg.writeInt(monthHistory.getMatches() + (training ? 0 : 1));
        msg.writeInt(monthHistory.getWins() + ((tr.getResult() == VictoryResult.WIN) ? 1 : 0));
        msg.writeInt(monthHistory.getDraws() + ((tr.getResult() == VictoryResult.DRAW) ? 1 : 0));
        msg.writeInt(monthHistory.getMom() + ((result.getMom() == id) ? 1 : 0));
        msg.writeInt(monthHistory.getValidGoals() + pr.getGoals());
        msg.writeInt(monthHistory.getValidAssists() + pr.getAssists());
        msg.writeInt(monthHistory.getValidInterception() + pr.getBlocks());
        msg.writeInt(monthHistory.getValidShooting() + pr.getShots());
        msg.writeInt(monthHistory.getValidStealing() + pr.getSteals());
        msg.writeInt(monthHistory.getValidTackling() + pr.getTackles());
        msg.writeZeros(4);
        msg.writeInt(monthHistory.getShooting());
        msg.writeInt(monthHistory.getStealing());
        msg.writeInt(monthHistory.getTackling());
        msg.writeZeros(4);
        msg.writeInt(monthHistory.getTotalPoints() + pr.getVotePoints());
    }

    public static void appendRanking(int playerId, ServerMessage msg, Connection ... con) {
        msg.writeShort(PlayerRanking.getRankingMatches(playerId, con));
        msg.writeShort(PlayerRanking.getRankingWins(playerId, con));
        msg.writeShort(PlayerRanking.getRankingPoints(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMom(playerId, con));
        msg.writeShort(PlayerRanking.getRankingValidGoals(playerId, con));
        msg.writeShort(PlayerRanking.getRankingValidAssists(playerId, con));
        msg.writeShort(PlayerRanking.getRankingValidInterception(playerId, con));
        msg.writeShort(PlayerRanking.getRankingValidShooting(playerId, con));
        msg.writeShort(PlayerRanking.getRankingValidStealing(playerId, con));
        msg.writeShort(PlayerRanking.getRankingValidTackling(playerId, con));
        msg.writeZeros(2);
        msg.writeShort(PlayerRanking.getRankingAvgGoals(playerId, con));
        msg.writeShort(PlayerRanking.getRankingAvgAssists(playerId, con));
        msg.writeShort(PlayerRanking.getRankingAvgInterception(playerId, con));
        msg.writeShort(PlayerRanking.getRankingAvgShooting(playerId, con));
        msg.writeShort(PlayerRanking.getRankingAvgStealing(playerId, con));
        msg.writeShort(PlayerRanking.getRankingAvgTackling(playerId, con));
        msg.writeZeros(2);
        msg.writeShort(PlayerRanking.getRankingAvgVotePoints(playerId, con));
        msg.writeShort(PlayerRanking.getRankingShooting(playerId, con));
        msg.writeShort(PlayerRanking.getRankingStealing(playerId, con));
        msg.writeShort(PlayerRanking.getRankingTackling(playerId, con));
        msg.writeZeros(2);
        msg.writeShort(PlayerRanking.getRankingTotalPoints(playerId, con));
    }

    public static void appendRankingLastMonth(int playerId,
                                              ServerMessage msg, Connection ... con) {
        msg.writeShort(PlayerRanking.getRankingMonthMatches(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthWins(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthPoints(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthMom(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthValidGoals(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthValidAssists(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthValidInterception(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthValidShooting(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthValidStealing(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthValidTackling(playerId, con));
        msg.writeZeros(2);
        msg.writeShort(PlayerRanking.getRankingMonthAvgGoals(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthAvgAssists(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthAvgInterception(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthAvgShooting(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthAvgStealing(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthAvgTackling(playerId, con));
        msg.writeZeros(2);
        msg.writeShort(PlayerRanking.getRankingMonthAvgVotePoints(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthShooting(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthStealing(playerId, con));
        msg.writeShort(PlayerRanking.getRankingMonthTackling(playerId, con));
        msg.writeZeros(2);
        msg.writeShort(PlayerRanking.getRankingMonthTotalPoints(playerId, con));
    }

    private static void appendItemInUse(Item item, ServerMessage msg) {
        msg.writeInt((item != null) ? item.getId() : 0);
        msg.writeBool(item != null, 4);
        msg.writeZeros(20);
    }

    public static void appendItemsInUse(Session session, ServerMessage msg, Connection ... con) {
        appendItemInUse(PlayerInfo.getItemHead(session, con), msg);
        appendItemInUse(PlayerInfo.getItemGlasses(session, con), msg);
        appendItemInUse(PlayerInfo.getItemShirts(session, con), msg);
        appendItemInUse(PlayerInfo.getItemPants(session, con), msg);
        appendItemInUse(PlayerInfo.getItemGlove(session, con), msg);
        appendItemInUse(PlayerInfo.getItemShoes(session, con), msg);
        appendItemInUse(PlayerInfo.getItemSocks(session, con), msg);
        appendItemInUse(PlayerInfo.getItemWrist(session, con), msg);
        appendItemInUse(PlayerInfo.getItemArm(session, con), msg);
        appendItemInUse(PlayerInfo.getItemKnee(session, con), msg);
        appendItemInUse(PlayerInfo.getItemEar(session, con), msg);
        appendItemInUse(PlayerInfo.getItemNeck(session, con), msg);
        appendItemInUse(PlayerInfo.getItemMask(session, con), msg);
        appendItemInUse(PlayerInfo.getItemMuffler(session, con), msg);
        appendItemInUse(PlayerInfo.getItemPackage(session, con), msg);
    }

    public static void appendInventoryItemsInUse(Session session,
                                                 ServerMessage msg, Connection ... con) {
        appendInventoryItem(PlayerInfo.getItemHead(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemGlasses(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemShirts(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemPants(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemGlove(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemShoes(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemSocks(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemWrist(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemArm(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemKnee(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemEar(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemNeck(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemMask(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemMuffler(session, con), msg);
        appendInventoryItem(PlayerInfo.getItemPackage(session, con), msg);
    }

    public static void appendInventorySkillsInUse(Session session, ServerMessage msg,
                                                  Connection ... con) {
        Skill[] skills = session.getCache().getSkills(con).values().stream()
                .filter(s -> s.getSelectionIndex() > 0).toArray(Skill[]::new);

        for (int i = 0; i < 30; i++) {
            appendInventorySkill((i < skills.length) ? skills[i] : null, msg);
        }
    }

    public static void appendInventoryCelebrationsInUse(Session session, ServerMessage msg,
                                                        Connection ... con) {
        Celebration[] celebrations = session.getCache().getCelebrations(con).values()
                .stream().filter(c -> c.getSelectionIndex() > 0).toArray(Celebration[]::new);

        for (int i = 0; i < 5; i++) {
            appendInventoryCelebration((i < celebrations.length) ? celebrations[i] : null, msg);
        }
    }

    public static void appendClubUniform(int clubId, ServerMessage msg, Connection ... con) {
        ClubUniform clubUniform = ClubInfo.getUniform(clubId, con);

        msg.writeInt(clubUniform.getHomeUniform().getShirts());
        msg.writeInt(clubUniform.getHomeUniform().getPants());
        msg.writeInt(clubUniform.getHomeUniform().getSocks());
        msg.writeInt(clubUniform.getHomeUniform().getWrist());

        msg.writeInt(clubUniform.getAwayUniform().getShirts());
        msg.writeInt(clubUniform.getAwayUniform().getPants());
        msg.writeInt(clubUniform.getAwayUniform().getSocks());
        msg.writeInt(clubUniform.getAwayUniform().getWrist());
    }
}
