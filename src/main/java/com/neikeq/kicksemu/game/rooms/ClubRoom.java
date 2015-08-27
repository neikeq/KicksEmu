package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.clubs.MemberInfo;
import com.neikeq.kicksemu.game.rooms.enums.RoomAccessType;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ClubRoom extends Room {

    @Override
    public void tryJoinRoom(Session session, String password) {
        int playerId = session.getPlayerId();

        byte result = 0;

        synchronized (locker) {
            if (isNotFull()) {
                if (!isPlaying()) {
                    if (MemberInfo.getClubId(playerId) == getId()) {
                        // Check password (moderators can bypass this)
                        if (getAccessType() != RoomAccessType.PASSWORD ||
                                password.equals(getPassword()) ||
                                PlayerInfo.isModerator(playerId)) {
                            short level = PlayerInfo.getLevel(playerId);

                            // If player level is allowed in room settings
                            if (isLevelAllowed(level)) {
                                // Join the room
                                addPlayer(session);
                            } else {
                                result = (byte) -8; // Invalid level
                            }
                        } else {
                            result = (byte) -5; // Wrong password
                        }
                    } else {
                        result = (byte) -9; // Not in the players list (not a member of the club)
                    }
                } else {
                    result = (byte) -6; // Match already started
                }
            } else {
                result = (byte) -4; // Room is full
            }
        }

        if (result != 0) {
            session.send(MessageBuilder.joinRoom(this, session.getPlayerId(), result));
        }
    }

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
    protected ServerMessage getLeaveRoom(int playerId, RoomLeaveReason reason) {
        return MessageBuilder.clubLeaveRoom(playerId, reason);
    }

    @Override
    protected ServerMessage getJoinRoom(Room room, int playerId, byte result) {
        return MessageBuilder.clubJoinRoom(room, result);
    }

    @Override
    protected void sendRoomInfo(Session session) {
        session.send(MessageBuilder.clubRoomInfo(this));
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
