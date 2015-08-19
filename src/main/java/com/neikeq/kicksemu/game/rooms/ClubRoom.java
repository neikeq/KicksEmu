package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.game.rooms.enums.RoomState;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ClubRoom extends Room {

    @Override
    public RoomTeam swapPlayerTeam(int playerId, RoomTeam currentTeam) {
        return currentTeam;
    }

    @Override
    protected void addPlayerToTeam(int playerId) {
        addPlayerToRedTeam(playerId);
    }

    @Override
    protected void addPlayerToTeam(int playerId, RoomTeam team) {
        addPlayerToRedTeam(playerId);
    }

    @Override
    protected void addPlayerToBlueTeam(int playerId) {
        addPlayerToRedTeam(playerId);
    }

    @Override
    public void startCountdown() { }

    @Override
    public void cancelCountdown() { }

    @Override
    protected ServerMessage getRoomPlayerInfo(Session session, Connection... con) {
        return MessageBuilder.clubRoomPlayerInfo(session, this, con);
    }

    @Override
    protected void sendRoomInfo(Session session) {
        session.send(MessageBuilder.clubRoomInfo(this));
    }

    @Override
    public void sendHostInfo() {
        synchronized (locker) {
            if (state() == RoomState.COUNT_DOWN) {
                getCountdownTimeoutFuture().cancel(true);
                sendBroadcast(MessageBuilder.hostInfo(this)); // TODO must be clubHostInfo
            }
        }
    }

    @Override
    public boolean isObserver(int playerId) {
        return false;
    }

    @Override
    public List<Integer> getObservers() {
        return new ArrayList<>();
    }

    @Override
    protected void addObserver(int playerId) { }
}
