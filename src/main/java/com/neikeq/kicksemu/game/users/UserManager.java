package com.neikeq.kicksemu.game.users;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PositionCodes;
import com.neikeq.kicksemu.game.characters.upgrade.CharacterUpgrade;
import com.neikeq.kicksemu.game.characters.upgrade.StatsFactor;
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
        int characterId = msg.readInt();
        short position = msg.readShort();

        if (session.getUserId() == userId && UserInfo.hasCharacter(characterId, userId)) {
            byte result = 0;

            short currentPosition = PlayerInfo.getPosition(characterId);

            if (PositionCodes.isValidNewPosition(currentPosition, position)) {
                PlayerInfo.setPosition(position, characterId);

                StatsFactor stats = CharacterUpgrade.getInstance().getStats().get(position);

                PlayerInfo.setStatsRunning(stats.getRunning(), characterId);
                PlayerInfo.setStatsEndurance(stats.getEndurance(), characterId);
                PlayerInfo.setStatsAgility(stats.getAgility(), characterId);
                PlayerInfo.setStatsBallControl(stats.getBallControl(), characterId);
                PlayerInfo.setStatsDribbling(stats.getDribbling(), characterId);
                PlayerInfo.setStatsStealing(stats.getStealing(), characterId);
                PlayerInfo.setStatsTackling(stats.getTackling(), characterId);
                PlayerInfo.setStatsHeading(stats.getHeading(), characterId);
                PlayerInfo.setStatsShortShots(stats.getShortShots(), characterId);
                PlayerInfo.setStatsLongShots(stats.getLongShots(), characterId);
                PlayerInfo.setStatsCrossing(stats.getCrossing(), characterId);
                PlayerInfo.setStatsShortPasses(stats.getShortPasses(), characterId);
                PlayerInfo.setStatsLongPasses(stats.getLongPasses(), characterId);
                PlayerInfo.setStatsMarking(stats.getMarking(), characterId);
                PlayerInfo.setStatsGoalkeeping(stats.getGoalkeeping(), characterId);
                PlayerInfo.setStatsPunching(stats.getPunching(), characterId);
                PlayerInfo.setStatsDefense(stats.getDefense(), characterId);

            } else {
                result = -1;
            }

            ServerMessage response = MessageBuilder.upgradeCharacter(result);
            session.send(response);
        }
    }
}
