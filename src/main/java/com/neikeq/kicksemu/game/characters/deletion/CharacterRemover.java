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
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CharacterRemover {

    private static class RemoverResult {
        protected static byte SUCCESS = 0;
        protected static byte TIME_LIMIT = (byte)253;
        protected static byte WRONG_PASSWORD = (byte)254;
        protected static byte SYSTEM_PROBLEM = (byte)255;
    }

    public static void removeCharacter(Session session, ClientMessage msg) {
        int userId = session.getUserId();
        int charId = msg.readInt();
        String password = msg.readString(20);

        int slot = UserInfo.characterSlot(charId, userId);

        byte result = RemoverResult.SUCCESS;

        try {
            if (slot >= 0 && Password.validate(password, UserInfo.getPassword(userId))) {
                if (limitTimeExpired(userId)) {
                    if (remove(charId)) {
                        clearOwnerSlot(userId, slot);
                        updateExpireTime(userId);
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

        String removalDate = DateUtils.dateToString(UserInfo.getLastCharDeletion(userId));

        ServerMessage response = MessageBuilder.removeCharacter(charId, removalDate, result);
        session.send(response);
    }

    public static boolean limitTimeExpired(int userId) {
        java.sql.Timestamp lastDeletionDate = UserInfo.getLastCharDeletion(userId);

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

    public static void clearOwnerSlot(int userId, int slot) {
        UserInfo.setSlotWithIndex(slot, 0, userId);
    }

    public static void updateExpireTime(int userId) {
        java.sql.Date expireDate = DateUtils.addDays(DateUtils.getSqlDate(), 7);

        UserInfo.setLastCharDeletion(expireDate, userId);
    }
}
