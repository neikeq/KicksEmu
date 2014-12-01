package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TutorialManager {

    private final static int REWARD_POINTS = 4000;

    public static void updateTutorial(Session session, ClientMessage msg) {
        int characterId = msg.readInt();
        byte dribbling = msg.readByte();
        byte passing = msg.readByte();
        byte shooting = msg.readByte();
        byte defense = msg.readByte();

        byte result = 0;
        int reward = 0;

        if (session.getUserInfo().hasCharacter(characterId)) {
            if (areValid(dribbling, passing, shooting, defense)) {
                PlayerInfo character = new PlayerInfo(characterId);

                byte storedDribbling = character.getTutorialDribbling();
                byte storedPassing = character.getTutorialPassing();
                byte storedShooting = character.getTutorialShooting();
                byte storedDefense = character.getTutorialDefense();

                if (!compare(dribbling, storedDribbling)) {
                    character.setTutorialDribbling(dribbling);
                }

                if (!compare(passing, storedPassing)) {
                    character.setTutorialPassing(passing);
                }

                if (!compare(shooting, storedShooting)) {
                    character.setTutorialShooting(shooting);
                }

                if (!compare(defense, storedDefense)) {
                    character.setTutorialDefense(defense);
                }

                if (checkForReward(character, dribbling, passing, shooting, defense)) {
                    reward = REWARD_POINTS;
                }
            }
        } else {
            result = (byte)255; // System problem
        }

        ServerMessage response = MessageBuilder.updateTutorial(dribbling, passing,
                shooting, defense, reward, result);
        session.send(response);
    }

    public static boolean areValid(byte dribbling, byte passing, byte shooting, byte defense) {
        return dribbling <= 15 && passing <= 15 && shooting <= 15 && defense <= 15;

    }

    public static boolean compare(byte tutorial, byte storedTutorial) {
        for (byte i = 0; i < 4; i++) {
            byte tutorialBit = (byte) ((tutorial >> i) & 1);
            byte storedBit = (byte) ((storedTutorial >> i) & 1);

            if (tutorialBit != storedBit) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkForReward(PlayerInfo character, byte dribbling,
                                      byte passing, byte shooting, byte defense) {
        if (dribbling == 15 && passing == 15 &&
                shooting == 15 && defense == 15) {
            if (!character.getReceivedReward()) {
                if (giveReward(character.getId())) {
                    character.setReceivedReward(true);
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean giveReward(int characterId) {
        String query = "UPDATE characters SET points = points + ? WHERE id = ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, REWARD_POINTS);
            stmt.setInt(2, characterId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
