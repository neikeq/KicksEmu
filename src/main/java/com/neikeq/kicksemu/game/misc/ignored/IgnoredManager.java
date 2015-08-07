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

        IgnoredList ignoredPlayers = PlayerInfo.getIgnoredList(session.getPlayerId());

        session.send(MessageBuilder.ignoredList(ignoredPlayers.getIgnoredPlayers(), page));
    }

    public static void blockPlayer(Session session, ClientMessage msg) {
        int targetId = msg.readInt();
        int playerId = session.getPlayerId();

        byte result = 0;

        if (CharacterUtils.characterExist(targetId)) {
            if (targetId != playerId) {
                IgnoredList ignoredList = PlayerInfo.getIgnoredList(playerId);

                if (ignoredList.size() < IGNORED_LIST_LIMIT) {
                    if (!ignoredList.containsPlayer(targetId)) {
                        ignoredList.addPlayer(targetId);

                        PlayerInfo.setIgnoredList(ignoredList, playerId);
                    } else {
                        result = (byte) -4; // Already ignoring
                    }
                } else {
                    result = (byte)  -3; // Ignored list is full
                }
            } else {
                result = (byte) -5; // Cannot ignore yourself
            }
        } else {
            result = (byte) -2; // Player not found
        }

        session.send(MessageBuilder.blockPlayer(targetId, result));
    }

    public static void unblockPlayer(Session session, ClientMessage msg) {
        int targetId = msg.readInt();
        int playerId = session.getPlayerId();

        byte result = 0;

        IgnoredList ignoredList = PlayerInfo.getIgnoredList(playerId);

        if (ignoredList.containsPlayer(targetId)) {
            ignoredList.removePlayer(targetId);

            PlayerInfo.setIgnoredList(ignoredList, playerId);
        } else {
            result = (byte) -4; // Player not found
        }

        session.send(MessageBuilder.unblockPlayer(result));
    }
}
