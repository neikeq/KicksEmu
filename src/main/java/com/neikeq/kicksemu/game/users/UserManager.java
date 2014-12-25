package com.neikeq.kicksemu.game.users;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PositionCodes;
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

    /**
     * Handles character upgrading when a player reaches level 18.
     * TODO: Keep ignoring the stats sent by the client, but calculate them manually
     * and apply these changes to the character.
     */
    public static void upgradeCharacter(Session session, ClientMessage msg) {
        int userId = msg.readInt();
        int characterId = msg.readInt();
        short position = msg.readShort();
        // Ignores new stats sent by client

        if (session.getUserId() == userId && UserInfo.hasCharacter(characterId, userId)) {
            byte result = 0;

            short currentPosition = PlayerInfo.getPosition(characterId);

            if (PositionCodes.isValidNewPosition(currentPosition, position)) {
                PlayerInfo.setPosition(position, characterId);
            } else {
                result = -1;
            }

            ServerMessage response = MessageBuilder.upgradeCharacter(result);
            session.send(response);
        }
    }
}
