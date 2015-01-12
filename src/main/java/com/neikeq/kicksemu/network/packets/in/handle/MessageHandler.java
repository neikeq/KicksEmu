package com.neikeq.kicksemu.network.packets.in.handle;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserManager;
import com.neikeq.kicksemu.network.packets.MessageId;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;

import java.util.HashMap;
import java.util.Map;

public abstract class MessageHandler {
    private static Map<Integer, MessageEventHandler> events;

    MessageHandler() {
        defineEvents();
    }

    private void defineEvents() {
        events = new HashMap<>();

        events.put(MessageId.UPDATE_SETTINGS, UserManager::updateSettings);
        events.put(MessageId.TCP_PING, UserManager::tcpPing);
    }

    public boolean handle(Session session, ClientMessage msg) {
        MessageEventHandler event = events.get(msg.getMessageId());

        if (event != null) {
            event.handle(session, msg);
            return true;
        }

        return false;
    }
}
