package com.neikeq.kicksemu.network.packets.in;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;

@FunctionalInterface
interface MessageEventHandler {

    void handle(Session session, ClientMessage msg);
}