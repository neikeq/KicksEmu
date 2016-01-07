package com.neikeq.kicksemu.game.misc.tutorial;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.storage.ConnectionRef;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TutorialManager {

    private final static int REWARD_POINTS = 4000;

    public static void updateTutorial(Session session, ClientMessage msg) {
        int characterId = session.getPlayerId();
        msg.ignoreBytes(4);

        TutorialState suspiciousState = new TutorialState(msg.readByte(), msg.readByte(),
                msg.readByte(), msg.readByte());

        short result = 0;
        int reward = 0;

        if (UserInfo.hasCharacter(characterId, session.getUserId())) {
            if (suspiciousState.isValid()) {
                try (ConnectionRef con = ConnectionRef.ref()) {
                    TutorialState tutorialState = PlayerInfo.getTutorialState(characterId, con);

                    if (tutorialChanged(tutorialState, suspiciousState)) {
                        PlayerInfo.setTutorialState(tutorialState, characterId, con);
                    }

                    if (checkForReward(characterId, tutorialState)) {
                        reward = REWARD_POINTS;
                    }
                } catch (SQLException e) {
                    Output.println("Exception when updating tutorial: " +
                            e.getMessage(), Level.DEBUG);
                }
            }
        } else {
            result = -1; // System problem
        }

        session.send(MessageBuilder.updateTutorial(suspiciousState, reward, result));
    }

    private static boolean tutorialChanged(TutorialState storedState, TutorialState newState) {
        boolean updated;

        if (updated = areDifferent(newState.getDribbling(), storedState.getDribbling())) {
            storedState.setDribbling(newState.getDribbling());
        }

        if (updated |= areDifferent(newState.getPassing(), storedState.getPassing())) {
            storedState.setPassing(newState.getPassing());
        }

        if (updated |= areDifferent(newState.getShooting(), storedState.getShooting())) {
            storedState.setShooting(newState.getShooting());
        }

        if (updated |= areDifferent(newState.getDefense(), storedState.getDefense())) {
            storedState.setDefense(newState.getDefense());
        }

        return updated;
    }

    private static boolean areDifferent(byte tutorial, byte storedTutorial) {
        for (byte i = 0; i < 4; i++) {
            byte tutorialBit = (byte) ((tutorial >> i) & 1);
            byte storedBit = (byte) ((storedTutorial >> i) & 1);

            if (tutorialBit != storedBit) {
                return true;
            }
        }

        return false;
    }

    private static boolean checkForReward(int characterId, TutorialState state) {
        if (state.isTutorialFinished()) {
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
        final String query = "UPDATE characters SET points = points + ? WHERE id = ?";

        try (ConnectionRef con = ConnectionRef.ref();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, REWARD_POINTS);
            stmt.setInt(2, characterId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
