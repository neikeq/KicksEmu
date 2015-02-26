package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerRanking {

    private static short getPlayerRanking(String rankCol, int id, Connection ... con) {
        String query = "SELECT index FROM ranking WHERE " + rankCol + " = ?";

        try {
            Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getShort("index");
                    } else {
                        return -1;
                    }
                }
            } finally {
                if (con.length <= 0) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    // Ranking

    public static short getRankingMatches(int id, Connection ... con) {
        return getPlayerRanking("ranking_matches", id, con);
    }

    public static short getRankingWins(int id, Connection ... con) {
        return getPlayerRanking("ranking_wins", id, con);
    }

    public static short getRankingPoints(int id, Connection ... con) {
        return getPlayerRanking("ranking_points", id, con);
    }

    public static short getRankingMom(int id, Connection ... con) {
        return getPlayerRanking("ranking_mom", id, con);
    }

    public static short getRankingValidGoals(int id, Connection ... con) {
        return getPlayerRanking("ranking_valid_goals", id, con);
    }

    public static short getRankingValidAssists(int id, Connection ... con) {
        return getPlayerRanking("ranking_valid_assists", id, con);
    }

    public static short getRankingValidInterception(int id, Connection ... con) {
        return getPlayerRanking("ranking_valid_interception", id, con);
    }

    public static short getRankingValidShooting(int id, Connection ... con) {
        return getPlayerRanking("ranking_valid_shooting", id, con);
    }

    public static short getRankingValidStealing(int id, Connection ... con) {
        return getPlayerRanking("ranking_valid_stealing", id, con);
    }

    public static short getRankingValidTackling(int id, Connection ... con) {
        return getPlayerRanking("ranking_valid_tackling", id, con);
    }

    public static short getRankingAvgGoals(int id, Connection ... con) {
        return getPlayerRanking("ranking_avg_goals", id, con);
    }

    public static short getRankingAvgAssists(int id, Connection ... con) {
        return getPlayerRanking("ranking_avg_assists", id, con);
    }

    public static short getRankingAvgInterception(int id, Connection ... con) {
        return getPlayerRanking("ranking_avg_interception", id, con);
    }

    public static short getRankingAvgShooting(int id, Connection ... con) {
        return getPlayerRanking("ranking_avg_shooting", id, con);
    }

    public static short getRankingAvgStealing(int id, Connection ... con) {
        return getPlayerRanking("ranking_avg_stealing", id, con);
    }

    public static short getRankingAvgTackling(int id, Connection ... con) {
        return getPlayerRanking("ranking_avg_tackling", id, con);
    }

    public static short getRankingAvgVotePoints(int id, Connection ... con) {
        return getPlayerRanking("ranking_avg_vote_points", id, con);
    }

    public static short getRankingShooting(int id, Connection ... con) {
        return getPlayerRanking("ranking_shooting", id, con);
    }

    public static short getRankingStealing(int id, Connection ... con) {
        return getPlayerRanking("ranking_stealing", id, con);
    }

    public static short getRankingTackling(int id, Connection ... con) {
        return getPlayerRanking("ranking_tackling", id, con);
    }

    public static short getRankingTotalPoints(int id, Connection ... con) {
        return getPlayerRanking("ranking_total_points", id, con);
    }

    // Ranking Last Month

    public static short getRankingMonthMatches(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_matches", id, con);
    }

    public static short getRankingMonthWins(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_wins", id, con);
    }

    public static short getRankingMonthPoints(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_points", id, con);
    }

    public static short getRankingMonthMom(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_mom", id, con);
    }

    public static short getRankingMonthValidGoals(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_valid_goals", id, con);
    }

    public static short getRankingMonthValidAssists(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_valid_assists", id, con);
    }

    public static short getRankingMonthValidInterception(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_valid_interception", id, con);
    }

    public static short getRankingMonthValidShooting(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_valid_shooting", id, con);
    }

    public static short getRankingMonthValidStealing(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_valid_stealing", id, con);
    }

    public static short getRankingMonthValidTackling(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_valid_tackling", id, con);
    }

    public static short getRankingMonthAvgGoals(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_avg_goals", id, con);
    }

    public static short getRankingMonthAvgAssists(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_avg_assists", id, con);
    }

    public static short getRankingMonthAvgInterception(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_avg_interception", id, con);
    }

    public static short getRankingMonthAvgShooting(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_avg_shooting", id, con);
    }

    public static short getRankingMonthAvgStealing(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_avg_stealing", id, con);
    }

    public static short getRankingMonthAvgTackling(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_avg_tackling", id, con);
    }

    public static short getRankingMonthAvgVotePoints(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_avg_vote_points", id, con);
    }

    public static short getRankingMonthShooting(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_shooting", id, con);
    }

    public static short getRankingMonthStealing(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_stealing", id, con);
    }

    public static short getRankingMonthTackling(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_tackling", id, con);
    }

    public static short getRankingMonthTotalPoints(int id, Connection ... con) {
        return getPlayerRanking("ranking_month_total_points", id, con);
    }
}
