package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

import java.util.Arrays;
import java.util.List;

public class StatusMessage {

    // List of censured words
    private static final List<String> censuredWords = Arrays.asList(new String[] {
            // Censured words go here
    });

    public static void statusMessage(Session session, ClientMessage msg) {
        String statusMessage = msg.readString(35);

        byte result = 0;

        if (containsCensuredWord(statusMessage)) {
            result = (byte)254;
        }

        if (result == 0) {
            PlayerInfo.setStatusMessage(statusMessage, session.getPlayerId());
        }

        session.send(MessageBuilder.changeStatusMessage(statusMessage, result));
    }

    private static boolean containsCensuredWord(String message) {
        for (String word : message.split(" ")) {
            if (!word.isEmpty() && censuredWords.contains(word)) {
                return true;
            }
        }

        return false;
    }
}
