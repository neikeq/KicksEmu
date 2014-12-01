package com.neikeq.kicksemu.game.users;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.CharacterUtils;
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
        UserInfo user = session.getUserInfo();

        int[] slots = new int[] {
                user.getSlotOne(),
                user.getSlotTwo(),
                user.getSlotThree()
        };

        for (short i = 0; i < slots.length; i++) {
            if (slots[i] > 0) {
                PlayerInfo character = new PlayerInfo(slots[i]);

                ServerMessage response = MessageBuilder.characterInfo(character, user, i);
                session.send(response);
            }
        }
    }

    public static void updateSettings(Session session, ClientMessage msg) {
        UserSettings settings = UserSettings.fromMessage(msg);

        byte result = 0;

        if (settings.isValid()) {
            session.getUserInfo().setSettings(settings);
        } else {
            result = (byte)255;
        }

        ServerMessage response = MessageBuilder.updateSettings(result);
        session.send(response);
    }

    public static void choiceCharacter(Session session, ClientMessage msg) {
        int characterId = msg.readInt();
        UserInfo user = session.getUserInfo();

        byte result = 0;

        if (user.hasCharacter(characterId) && CharacterUtils.characterExist(characterId)) {
            PlayerInfo character = new PlayerInfo(characterId);

            if (!character.isBlocked()) {
                session.setPlayerInfo(characterId);
            } else {
                result = (byte)255; // System problem
            }
        } else {
            result = (byte)254; // Character does not exist
        }

        ServerMessage response = MessageBuilder.choiceCharacter(characterId, result);
        session.send(response);
    }
}
