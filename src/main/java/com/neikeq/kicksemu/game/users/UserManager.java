package com.neikeq.kicksemu.game.users;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PositionCodes;
import com.neikeq.kicksemu.game.characters.upgrade.CharacterUpgrade;
import com.neikeq.kicksemu.game.characters.PlayerStats;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.SQLException;

public class UserManager {

    public static void tcpPing(Session session, ClientMessage msg) {
        int ping = msg.readInt();

        session.setPing(ping);
        session.setPingState((byte)0);
    }

    public static void certifyExit(Session session) {
        ServerMessage response = MessageBuilder.certifyExit(true);
        session.send(response);

        session.close();
    }

    public static void instantExit(Session session) {
        ServerMessage response = MessageBuilder.instantExit();
        session.send(response);

        session.close();
    }

    public static void characterInfo(Session session) {
        int userId = session.getUserId();

        try (Connection con = MySqlManager.getConnection()) {
            int[] slots = new int[]{
                    UserInfo.getSlotOne(userId, con),
                    UserInfo.getSlotTwo(userId, con),
                    UserInfo.getSlotThree(userId, con)
            };

            for (short i = 0; i < slots.length; i++) {
                if (slots[i] > 0) {
                    session.send(MessageBuilder.characterInfo(slots[i], userId, i, con));
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void updateSettings(Session session, ClientMessage msg) {
        UserSettings settings = UserSettings.fromMessage(msg);

        byte result = 0;

        if (settings.isValid()) {
            UserInfo.setSettings(settings, session.getUserId());
        } else {
            result = (byte)255;
        }

        ServerMessage response = MessageBuilder.updateSettings(result);
        session.send(response);
    }

    public static void choiceCharacter(Session session, ClientMessage msg) {
        int charId = msg.readInt();
        int userId = session.getUserId();

        byte result = 0;

        if (UserInfo.hasCharacter(charId, userId) && CharacterUtils.characterExist(charId)) {
            if (!PlayerInfo.isBlocked(charId)) {
                session.setPlayerId(charId);
            } else {
                result = (byte)255; // System problem
            }
        } else {
            result = (byte)254; // Character does not exist
        }

        ServerMessage response = MessageBuilder.choiceCharacter(charId, result);
        session.send(response);
    }

    public static void upgradeCharacter(Session session, ClientMessage msg) {
        int userId = msg.readInt();
        int playerId = msg.readInt();
        short position = msg.readShort();

        if (session.getUserId() == userId && UserInfo.hasCharacter(playerId, userId)) {
            byte result = 0;

            try (Connection con = MySqlManager.getConnection()) {
                short currentPosition = PlayerInfo.getPosition(playerId, con);

                if (PositionCodes.isValidNewPosition(currentPosition, position)) {
                    PlayerInfo.setPosition(position, playerId, con);

                    PlayerStats stats = CharacterUpgrade.getInstance().getStats().get(position);

                    short remainStats = 0;

                    remainStats += PlayerInfo.setStatsRunning(stats.getRunning(), playerId, con);
                    remainStats += PlayerInfo.setStatsEndurance(stats.getEndurance(), playerId, con);
                    remainStats += PlayerInfo.setStatsAgility(stats.getAgility(), playerId, con);
                    remainStats += PlayerInfo.setStatsBallControl(stats.getBallControl(), playerId, con);
                    remainStats += PlayerInfo.setStatsDribbling(stats.getDribbling(), playerId, con);
                    remainStats += PlayerInfo.setStatsStealing(stats.getStealing(), playerId, con);
                    remainStats += PlayerInfo.setStatsTackling(stats.getTackling(), playerId, con);
                    remainStats += PlayerInfo.setStatsHeading(stats.getHeading(), playerId, con);
                    remainStats += PlayerInfo.setStatsShortShots(stats.getShortShots(), playerId, con);
                    remainStats += PlayerInfo.setStatsLongShots(stats.getLongShots(), playerId, con);
                    remainStats += PlayerInfo.setStatsCrossing(stats.getCrossing(), playerId, con);
                    remainStats += PlayerInfo.setStatsShortPasses(stats.getShortPasses(), playerId, con);
                    remainStats += PlayerInfo.setStatsLongPasses(stats.getLongPasses(), playerId, con);
                    remainStats += PlayerInfo.setStatsMarking(stats.getMarking(), playerId, con);
                    remainStats += PlayerInfo.setStatsGoalkeeping(stats.getGoalkeeping(), playerId, con);
                    remainStats += PlayerInfo.setStatsPunching(stats.getPunching(), playerId, con);
                    remainStats += PlayerInfo.setStatsDefense(stats.getDefense(), playerId, con);

                    PlayerInfo.setStatsPoints(remainStats, playerId, con);

                } else {
                    result = -1;
                }
            } catch (SQLException ignored) {}

            ServerMessage response = MessageBuilder.upgradeCharacter(result);
            session.send(response);
        }
    }
}
