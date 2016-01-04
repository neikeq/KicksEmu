package com.neikeq.kicksemu.game.characters.types;

import com.neikeq.kicksemu.storage.ConnectionRef;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerRanking {

    private static short getPlayerRanking(String rankCol, int id, ConnectionRef ... con) {
        final String query = "SELECT `index` FROM ranking WHERE " + rankCol + " = ?";

        try (ConnectionRef connection = ConnectionRef.ref(con)) {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() ? rs.getShort("index") : -1;
                }
            }
        } catch (SQLException e) {
            return -1;
        }
    }

    // Ranking

    public static short getRankingMatches(int id, ConnectionRef... con) {
        return getPlayerRanking("matches", id, con);
    }

    public static short getRankingWins(int id, ConnectionRef ... con) {
        return getPlayerRanking("wins", id, con);
    }

    public static short getRankingPoints(int id, ConnectionRef ... con) {
        return getPlayerRanking("points", id, con);
    }

    public static short getRankingMom(int id, ConnectionRef ... con) {
        return getPlayerRanking("mom", id, con);
    }

    public static short getRankingValidGoals(int id, ConnectionRef ... con) {
        return getPlayerRanking("valid_goals", id, con);
    }

    public static short getRankingValidAssists(int id, ConnectionRef ... con) {
        return getPlayerRanking("valid_assists", id, con);
    }

    public static short getRankingValidInterception(int id, ConnectionRef ... con) {
        return getPlayerRanking("valid_interception", id, con);
    }

    public static short getRankingValidShooting(int id, ConnectionRef ... con) {
        return getPlayerRanking("valid_shooting", id, con);
    }

    public static short getRankingValidStealing(int id, ConnectionRef ... con) {
        return getPlayerRanking("valid_stealing", id, con);
    }

    public static short getRankingValidTackling(int id, ConnectionRef ... con) {
        return getPlayerRanking("valid_tackling", id, con);
    }

    public static short getRankingAvgGoals(int id, ConnectionRef ... con) {
        return getPlayerRanking("avg_goals", id, con);
    }

    public static short getRankingAvgAssists(int id, ConnectionRef ... con) {
        return getPlayerRanking("avg_assists", id, con);
    }

    public static short getRankingAvgInterception(int id, ConnectionRef ... con) {
        return getPlayerRanking("avg_interception", id, con);
    }

    public static short getRankingAvgShooting(int id, ConnectionRef ... con) {
        return getPlayerRanking("avg_shooting", id, con);
    }

    public static short getRankingAvgStealing(int id, ConnectionRef ... con) {
        return getPlayerRanking("avg_stealing", id, con);
    }

    public static short getRankingAvgTackling(int id, ConnectionRef ... con) {
        return getPlayerRanking("avg_tackling", id, con);
    }

    public static short getRankingAvgVotePoints(int id, ConnectionRef ... con) {
        return getPlayerRanking("avg_vote_points", id, con);
    }

    public static short getRankingShooting(int id, ConnectionRef ... con) {
        return getPlayerRanking("shooting", id, con);
    }

    public static short getRankingStealing(int id, ConnectionRef ... con) {
        return getPlayerRanking("stealing", id, con);
    }

    public static short getRankingTackling(int id, ConnectionRef ... con) {
        return getPlayerRanking("tackling", id, con);
    }

    public static short getRankingTotalPoints(int id, ConnectionRef ... con) {
        return getPlayerRanking("total_points", id, con);
    }

    // Ranking Last Month

    public static short getRankingMonthMatches(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_matches", id, con);
    }

    public static short getRankingMonthWins(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_wins", id, con);
    }

    public static short getRankingMonthPoints(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_points", id, con);
    }

    public static short getRankingMonthMom(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_mom", id, con);
    }

    public static short getRankingMonthValidGoals(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_valid_goals", id, con);
    }

    public static short getRankingMonthValidAssists(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_valid_assists", id, con);
    }

    public static short getRankingMonthValidInterception(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_valid_interception", id, con);
    }

    public static short getRankingMonthValidShooting(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_valid_shooting", id, con);
    }

    public static short getRankingMonthValidStealing(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_valid_stealing", id, con);
    }

    public static short getRankingMonthValidTackling(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_valid_tackling", id, con);
    }

    public static short getRankingMonthAvgGoals(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_avg_goals", id, con);
    }

    public static short getRankingMonthAvgAssists(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_avg_assists", id, con);
    }

    public static short getRankingMonthAvgInterception(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_avg_interception", id, con);
    }

    public static short getRankingMonthAvgShooting(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_avg_shooting", id, con);
    }

    public static short getRankingMonthAvgStealing(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_avg_stealing", id, con);
    }

    public static short getRankingMonthAvgTackling(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_avg_tackling", id, con);
    }

    public static short getRankingMonthAvgVotePoints(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_avg_vote_points", id, con);
    }

    public static short getRankingMonthShooting(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_shooting", id, con);
    }

    public static short getRankingMonthStealing(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_stealing", id, con);
    }

    public static short getRankingMonthTackling(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_tackling", id, con);
    }

    public static short getRankingMonthTotalPoints(int id, ConnectionRef ... con) {
        return getPlayerRanking("month_total_points", id, con);
    }
}
