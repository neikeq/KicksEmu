package com.neikeq.kicksemu.game.misc.ignored;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;

public class IgnoredManager {

    private static final int IGNORED_LIST_LIMIT = 10;

    public static void ignoreList(Session session, ClientMessage msg) {
        byte page = msg.readByte();

        if (page >= 0) {
            IgnoredList ignoredPlayers = PlayerInfo.getIgnoredList(session.getPlayerId());
            session.send(MessageBuilder.ignoredList(ignoredPlayers.getIgnoredPlayers(), page));
        }
    }

    public static void blockPlayer(Session session, ClientMessage msg) {
        int targetId = msg.readInt();
        short result = blockPlayer(session, targetId);

        session.send(MessageBuilder.blockPlayer(targetId, result));
    }

    public static byte blockPlayer(Session session, int targetId) {
        int playerId = session.getPlayerId();

        if (CharacterUtils.characterExist(targetId)) {
            if (targetId != playerId) {
                IgnoredList ignoredList = PlayerInfo.getIgnoredList(playerId);

                if (ignoredList.size() < IGNORED_LIST_LIMIT) {
                    if (!ignoredList.containsPlayer(targetId)) {
                        ignoredList.addPlayer(targetId);

                        PlayerInfo.setIgnoredList(ignoredList, playerId);
                    } else {
                        return -4; // Already ignoring
                    }
                } else {
                    return -3; // Ignored list is full
                }
            } else {
                return -5; // Cannot ignore yourself
            }
        } else {
            return -2; // Player not found
        }

        return 0;
    }

    public static void unblockPlayer(Session session, ClientMessage msg) {
        int targetId = msg.readInt();
        short result = unblockPlayer(session, targetId);

        session.send(MessageBuilder.unblockPlayer(result));
    }

    public static byte unblockPlayer(Session session, int targetId) {
        int playerId = session.getPlayerId();

        IgnoredList ignoredList = PlayerInfo.getIgnoredList(playerId);

        if (ignoredList.containsPlayer(targetId)) {
            ignoredList.removePlayer(targetId);

            PlayerInfo.setIgnoredList(ignoredList, playerId);
        } else {
            return -4; // Player not found
        }

        return 0;
    }
}
