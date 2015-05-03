package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.utils.Password;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class CharacterRemover {

    private static class RemoverResult {
        static final byte SUCCESS = 0;
        static final byte TIME_LIMIT = (byte)253;
        static final byte WRONG_PASSWORD = (byte)254;
        static final byte SYSTEM_PROBLEM = (byte)255;
    }

    public static void removeCharacter(Session session, ClientMessage msg) {
        int userId = session.getUserId();
        int charId = msg.readInt();
        char[] password = msg.readChars(20);

        int slot = UserInfo.characterSlot(charId, userId);

        byte result = RemoverResult.SUCCESS;

        try {
            if (slot >= 0 && Password.validate(password, UserInfo.getPassword(userId))) {
                // Overwrite password for security
                Arrays.fill(password, '\0');

                if (limitTimeExpired(userId)) {
                    UserInfo.clearSlotByIndex(slot, userId);
                    updateExpireTime(userId);
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

        session.send(MessageBuilder.removeCharacter(charId, removalDate, result));
    }

    private static boolean limitTimeExpired(int userId) {
        java.sql.Timestamp lastDeletionDate = UserInfo.getLastCharDeletion(userId);

        return lastDeletionDate == null || DateUtils.getTimestamp().after(lastDeletionDate);
    }

    private static void updateExpireTime(int userId) {
        java.sql.Date expireDate = DateUtils.addDays(DateUtils.getSqlDate(), 7);

        UserInfo.setLastCharDeletion(expireDate, userId);
    }
}
