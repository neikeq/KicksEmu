package com.neikeq.kicksemu.game.characters.deletion;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.storage.MySqlManager;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.utils.Password;

import java.security.NoSuchAlgorithmException;
import java.security.Timestamp;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

public class CharacterRemover {

    private static class RemoverResult {
        protected static byte SUCCESS = 0;
        protected static byte TIME_LIMIT = (byte)253;
        protected static byte WRONG_PASSWORD = (byte)254;
        protected static byte SYSTEM_PROBLEM = (byte)255;
    }

    public static void removeCharacter(Session session, ClientMessage msg) {
        int charId = msg.readInt();
        String password = msg.readString(20);

        UserInfo user = session.getUserInfo();
        int slot = user.characterSlot(charId);

        byte result = RemoverResult.SUCCESS;

        try {
            if (slot >= 0 && Password.validate(password, user.getPassword())) {
                if (limitTimeExpired(user)) {
                    if (remove(charId)) {
                        clearOwnerSlot(user, slot);
                        updateExpireTime(user);
                    } else {
                        result = RemoverResult.SYSTEM_PROBLEM;
                    }
                } else {
                    result = RemoverResult.TIME_LIMIT;
                }
            } else {
                result = RemoverResult.WRONG_PASSWORD;
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            result = RemoverResult.SYSTEM_PROBLEM;
        }

        String removalDate = DateUtils.dateToString(user.getLastCharDeletion());

        ServerMessage response = MessageBuilder.removeCharacter(charId, removalDate, result);
        session.send(response);
    }

    public static boolean limitTimeExpired(UserInfo user) {
        java.sql.Timestamp lastDeletionDate = user.getLastCharDeletion();

        return lastDeletionDate == null || DateUtils.getTimestamp().after(lastDeletionDate);
    }

    public static boolean remove(int characterId) {
        String query = "DELETE FROM characters WHERE id = ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, characterId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static void clearOwnerSlot(UserInfo user, int slot) {
        user.setSlotWithIndex(slot, 0);
    }

    public static void updateExpireTime(UserInfo user) {
        java.sql.Date expireDate = DateUtils.addDays(DateUtils.getSqlDate(), 7);

        user.setLastCharDeletion(expireDate);
    }
}
