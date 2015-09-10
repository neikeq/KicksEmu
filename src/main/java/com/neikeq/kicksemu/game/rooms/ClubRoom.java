package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.clubs.MemberInfo;
import com.neikeq.kicksemu.game.rooms.enums.RoomAccessType;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.enums.RoomState;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ClubRoom extends Room {

    { super.getRoomLobby().setTeamChatEnabled(false); }

    private byte wins = 0;
    private int challengeTarget = 0;
    private int challengeId = -1;

    @Override
    public void removeRoom() {
        synchronized (locker) {
            if (TeamManager.isRegistered(getId())) {
                TeamManager.unregister(getId());
            }
            super.removeRoom();
        }
    }

    @Override
    public void tryJoinRoom(Session session, String password) {
        int playerId = session.getPlayerId();

        byte result = 0;

        synchronized (locker) {
            if (isNotFull()) {
                if (isWaiting()) {
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
    protected void onPlayerLeaved(int playerId, RoomLeaveReason reason) {
        super.onPlayerLeaved(playerId, reason);
            TeamManager.unregister(getId());
    }

    @Override
    protected ServerMessage roomPlayerInfoMessage(Session session, Connection... con) {
        return MessageBuilder.clubRoomPlayerInfo(session, this, con);
    }

    @Override
    protected ServerMessage leaveRoomMessage(int playerId, RoomLeaveReason reason) {
        return MessageBuilder.clubLeaveRoom(playerId, reason);
    }

    @Override
    protected ServerMessage roomMasterMessage(int master) {
        return MessageBuilder.clubRoomCaptain(master);
    }

    @Override
    protected ServerMessage joinRoomMessage(Room room, int playerId, byte result) {
        return MessageBuilder.clubJoinRoom(room, result);
    }

    @Override
    protected ServerMessage roomInfoMessage() {
        return MessageBuilder.clubRoomInfo(this);
    }

    public byte onChallengeRequest(int fromId) {
        synchronized (locker) {
            if (!isChallenging()) {
                setChallengeTarget(fromId);
                ServerMessage request = MessageBuilder.clubChallengeTeam(fromId, false, (byte) 0);
                getPlayers().get(getMaster()).sendAndFlush(request);

                return 0;
            }

            return -6; // The club is applying for a match
        }
    }

    public byte onChallengeResponse(int fromId, boolean accepted) {
        synchronized (locker) {
            if (challengeTarget == fromId) {
                if (!accepted) {
                    setChallengeTarget(0);
                }

                byte result = accepted ?
                        (byte) 0 : // Challenge request accepted
                        -4; // The opponent club refused the request
                ServerMessage response = MessageBuilder.clubChallengeResponse(fromId,
                        accepted, result);
                getPlayers().get(getMaster()).sendAndFlush(response);

                return 0;
            }

            return -6; // Failed to create the match room... Invalid requester target.
        }
    }

    public void quitChallenge() {
        getPlayers().values().forEach(session -> {
            session.send(roomInfoMessage());
            session.send(roomMasterMessage(getMaster()));
            sendRoomPlayersInfo(session);
        });
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

    public boolean isWaiting() {
        // TODO this should be temporal, until I find a way to display the APPLYING icon
        return super.isWaiting() || isChallenging();
    }

    public byte getWins() {
        return wins;
    }

    public void setWins(byte wins) {
        this.wins = wins;
    }

    public boolean isChallenging() {
        return state() == RoomState.APPLYING;
    }

    public int getChallengeTarget() {
        return challengeTarget;
    }

    public void setChallengeTarget(int challengeTarget) {
        if (challengeTarget != 0) {
            setState(RoomState.APPLYING);
        } else if (state() == RoomState.APPLYING) {
            setState(RoomState.WAITING);
        }

        this.challengeTarget = challengeTarget;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }
}
