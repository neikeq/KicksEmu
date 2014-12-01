package com.neikeq.kicksemu.network.packets.in.handle;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;

@FunctionalInterface
public interface MessageEventHandler {
    void handle(Session session, ClientMessage msg);
}