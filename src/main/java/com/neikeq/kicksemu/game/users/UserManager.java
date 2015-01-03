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

public class UserManager {
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

        int[] slots = new int[] {
                UserInfo.getSlotOne(userId),
                UserInfo.getSlotTwo(userId),
                UserInfo.getSlotThree(userId)
        };

        for (short i = 0; i < slots.length; i++) {
            if (slots[i] > 0) {
                ServerMessage response = MessageBuilder.characterInfo(slots[i], userId, i);
                session.send(response);
            }
        }
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

            short currentPosition = PlayerInfo.getPosition(playerId);

            if (PositionCodes.isValidNewPosition(currentPosition, position)) {
                PlayerInfo.setPosition(position, playerId);

                PlayerStats stats = CharacterUpgrade.getInstance().getStats().get(position);

                short remainStats = 0;

                remainStats += PlayerInfo.setStatsRunning(stats.getRunning(), playerId);
                remainStats += PlayerInfo.setStatsEndurance(stats.getEndurance(), playerId);
                remainStats += PlayerInfo.setStatsAgility(stats.getAgility(), playerId);
                remainStats += PlayerInfo.setStatsBallControl(stats.getBallControl(), playerId);
                remainStats += PlayerInfo.setStatsDribbling(stats.getDribbling(), playerId);
                remainStats += PlayerInfo.setStatsStealing(stats.getStealing(), playerId);
                remainStats += PlayerInfo.setStatsTackling(stats.getTackling(), playerId);
                remainStats += PlayerInfo.setStatsHeading(stats.getHeading(), playerId);
                remainStats += PlayerInfo.setStatsShortShots(stats.getShortShots(), playerId);
                remainStats += PlayerInfo.setStatsLongShots(stats.getLongShots(), playerId);
                remainStats += PlayerInfo.setStatsCrossing(stats.getCrossing(), playerId);
                remainStats += PlayerInfo.setStatsShortPasses(stats.getShortPasses(), playerId);
                remainStats += PlayerInfo.setStatsLongPasses(stats.getLongPasses(), playerId);
                remainStats += PlayerInfo.setStatsMarking(stats.getMarking(), playerId);
                remainStats += PlayerInfo.setStatsGoalkeeping(stats.getGoalkeeping(), playerId);
                remainStats += PlayerInfo.setStatsPunching(stats.getPunching(), playerId);
                remainStats += PlayerInfo.setStatsDefense(stats.getDefense(), playerId);

                PlayerInfo.setStatsPoints(remainStats, playerId);

            } else {
                result = -1;
            }

            ServerMessage response = MessageBuilder.upgradeCharacter(result);
            session.send(response);
        }
    }
}
