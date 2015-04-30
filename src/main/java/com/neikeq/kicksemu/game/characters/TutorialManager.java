package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.characters.types.TutorialState;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TutorialManager {

    private final static int REWARD_POINTS = 4000;

    public static void updateTutorial(Session session, ClientMessage msg) {
        int characterId = session.getPlayerId();
        msg.ignoreBytes(4);

        byte dribbling = msg.readByte();
        byte passing = msg.readByte();
        byte shooting = msg.readByte();
        byte defense = msg.readByte();

        byte result = 0;
        int reward = 0;

        if (UserInfo.hasCharacter(characterId, session.getUserId())) {
            if (areValid(dribbling, passing, shooting, defense)) {
                try (Connection con = MySqlManager.getConnection()) {
                    TutorialState tutorialState = PlayerInfo.getTutorialState(characterId, con);

                    boolean updated;

                    if (updated = !compare(dribbling, tutorialState.getDribbling())) {
                        tutorialState.setDribbling(dribbling);
                    }

                    if (updated |= !compare(passing, tutorialState.getPassing())) {
                        tutorialState.setPassing(passing);
                    }

                    if (updated |= !compare(shooting, tutorialState.getShooting())) {
                        tutorialState.setShooting(shooting);
                    }

                    if (updated |= !compare(defense, tutorialState.getDefense())) {
                        tutorialState.setDefense(defense);
                    }

                    if (updated) {
                        PlayerInfo.setTutorialState(tutorialState, characterId, con);
                    }

                    if (checkForReward(characterId, dribbling, passing, shooting, defense)) {
                        reward = REWARD_POINTS;
                    }
                } catch (SQLException ignored) {}
            }
        } else {
            result = (byte)255; // System problem
        }

        session.send(MessageBuilder.updateTutorial(dribbling, passing,
                shooting, defense, reward, result));
    }

    private static boolean areValid(byte dribbling, byte passing, byte shooting, byte defense) {
        return dribbling <= 15 && passing <= 15 && shooting <= 15 && defense <= 15;

    }

    private static boolean compare(byte tutorial, byte storedTutorial) {
        for (byte i = 0; i < 4; i++) {
            byte tutorialBit = (byte) ((tutorial >> i) & 1);
            byte storedBit = (byte) ((storedTutorial >> i) & 1);

            if (tutorialBit != storedBit) {
                return false;
            }
        }

        return true;
    }

    private static boolean checkForReward(int characterId, byte dribbling,
                                          byte passing, byte shooting, byte defense) {
        if (dribbling == 15 && passing == 15 &&
                shooting == 15 && defense == 15) {
            if (!PlayerInfo.getReceivedReward(characterId)) {
                if (giveReward(characterId)) {
                    PlayerInfo.setReceivedReward(true, characterId);
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean giveReward(int characterId) {
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
