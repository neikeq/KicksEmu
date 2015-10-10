package com.neikeq.kicksemu.network.packets.in;

import com.neikeq.kicksemu.game.sessions.Session;

@FunctionalInterface
interface MessageEventHandler {

    void handle(Session session, ClientMessage msg);
}