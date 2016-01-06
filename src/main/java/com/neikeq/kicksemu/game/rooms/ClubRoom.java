package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.clubs.MemberInfo;
import com.neikeq.kicksemu.game.lobby.RoomLobby;
import com.neikeq.kicksemu.game.rooms.challenges.Challenge;
import com.neikeq.kicksemu.game.rooms.challenges.ChallengeOrganizer;
import com.neikeq.kicksemu.game.rooms.challenges.WinStreakCache;
import com.neikeq.kicksemu.game.rooms.enums.RoomAccessType;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.enums.RoomSize;
import com.neikeq.kicksemu.game.rooms.enums.RoomState;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.storage.ConnectionRef;
import com.neikeq.kicksemu.utils.mutable.MutableInteger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClubRoom extends Room {

    public static final byte REQUIRED_TEAM_PLAYERS = 4;

    { super.getRoomLobby().setTeamChatEnabled(false); }

    private byte totalWins;
    private byte winStreak;
    private int challengeTarget;
    private int challengeId = -1;
    private int totalLevels;

    private final WinStreakCache winStreakCache = new WinStreakCache((byte) 0, getPlayers().keySet());

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

        short result = 0;

        synchronized (locker) {
            if (isNotFull()) {
                if (isWaiting() && !TeamManager.isRegistered(getId())) {
                    if (MemberInfo.getClubId(playerId) == getId()) {
                        // Check password (moderators can bypass this)
                        if ((getAccessType() != RoomAccessType.PASSWORD) ||
                                password.equals(getPassword()) ||
                                PlayerInfo.isModerator(playerId)) {
                            short level = PlayerInfo.getLevel(playerId);

                            // If player level is allowed in room settings
                            if (isLevelAllowed(level)) {
                                // Join the room
                                addPlayer(session);
                            } else {
                                result = -8; // Invalid level
                            }
                        } else {
                            result = -5; // Wrong password
                        }
                    } else {
                        result = -9; // Not in the players list (not a member of the club)
                    }
                } else {
                    result = -6; // Match already started
                }
            } else {
                result = -4; // Room is full
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
    public void startCountdown() {}

    @Override
    public void cancelCountdown() {}

    @Override
    protected void onPlayerJoined(Session session) {
        synchronized (locker) {
            super.onPlayerJoined(session);
            updateTotalLevels();

            if ((getPlayers().size() == REQUIRED_TEAM_PLAYERS) && (winStreakCache.getWins() > 0)) {
                if (winStreakCache.matchesTeam(getPlayers().keySet())) {
                    setWinStreak(winStreakCache.getWins());
                }
            }
        }
    }

    @Override
    protected void onPlayerLeaved(Session session, RoomLeaveReason reason) {
        synchronized (locker) {
            if (challengeId > 0) {
                Challenge challenge = ChallengeOrganizer.getChallengeById(challengeId);
                if (challenge != null) {
                    challenge.cancel();
                }
            }

            super.onPlayerLeaved(session, reason);
            TeamManager.unregister(getId());

            updateTotalLevels();

            if ((getPlayers().size() == (REQUIRED_TEAM_PLAYERS - 1)) && (winStreak > 0)) {
                winStreakCache.setWins(winStreak);
                winStreakCache.setPlayers(getPlayers().keySet());
                setWinStreak((byte) 0);
            }
        }
    }

    @Override
    void onHostLeaved(int playerId) {}

    @Override
    protected ServerMessage roomPlayerInfoMessage(Session session, ConnectionRef... con) {
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
    protected ServerMessage joinRoomMessage(Room room, int playerId, short result) {
        return MessageBuilder.clubJoinRoom(room, result);
    }

    @Override
    protected ServerMessage roomInfoMessage() {
        return MessageBuilder.clubRoomInfo(this);
    }

    public byte onChallengeRequest(int from) {
        synchronized (locker) {
            if (!isChallenging()) {
                setChallengeTarget(from);
                ServerMessage request = MessageBuilder.clubChallengeTeam(from, false, (short) 0);
                getPlayer(getMaster()).sendAndFlush(request);

                return 0;
            }

            return -6; // The club is applying for a match
        }
    }

    public short onChallengeResponse(int fromId, boolean accepted) {
        synchronized (locker) {
            if (challengeTarget == fromId) {
                if (!accepted) {
                    setChallengeTarget(0);
                }

                short result = accepted ?
                        (short) 0 : // Challenge request accepted
                        -4; // The opponent club refused the request
                broadcast(MessageBuilder.clubChallengeResponse(fromId, accepted, result));

                return 0;
            }

            return -6; // Failed to create the match room... Invalid requester target.
        }
    }

    public void onQuitChallenge() {
        setChallengeId(-1);
        setState(RoomState.WAITING);
        broadcast(MessageBuilder.clubCancelChallenge());

        if (TeamManager.isRegistered(getId())) {
            final short page = 0;
            broadcast(MessageBuilder.clubTeamList(
                    TeamManager.getTeamsFromPage(page, getId()), this, page));
        }
    }

    private void updateTotalLevels() {
        final MutableInteger totalLevels = new MutableInteger();

        try (ConnectionRef con = ConnectionRef.ref()) {
            getPlayers().keySet().forEach(playerId ->
                    totalLevels.sum(PlayerInfo.getLevel(playerId, con)));
        } catch (SQLException e) {
            Output.println("Failed to calculate club room total levels: " + e.getMessage(),
                    Level.DEBUG);
        }

        this.totalLevels = totalLevels.get();
    }

    public byte getLevelGapDifferenceTo(Optional<ClubRoom> maybeRoom) {
        if (maybeRoom.isPresent()) {
            ClubRoom room = maybeRoom.get();

            final byte maxDiff = 3;

            byte difference = (byte) ((room.totalLevels - totalLevels) / 10);

            if (Math.abs(difference) > maxDiff) {
                difference = (byte) (Math.signum(difference) * maxDiff);
            }

            return difference;
        }

        return 0;
    }

    @Override
    public boolean isObserver(int playerId) {
        return false;
    }

    @Override
    public boolean isWaiting() {
        if (challengeId > 0) {
            Challenge challenge = ChallengeOrganizer.getChallengeById(challengeId);
            if (challenge != null) {
                return challenge.getRoom().isWaiting();
            }
        }
        // TODO this should be temporal, until I find a way to display the APPLYING icon
        return super.isWaiting() || isChallenging();
    }

    public boolean isChallenging() {
        return state() == RoomState.APPLYING;
    }

    @Override
    public RoomLobby getRoomLobby() {
        if (challengeId > 0) {
            Challenge challenge = ChallengeOrganizer.getChallengeById(challengeId);
            if (challenge != null) {
                return challenge.getRoom().getRoomLobby();
            }
        }
        return super.getRoomLobby();
    }

    @Override
    public List<Integer> getObservers() {
        return new ArrayList<>();
    }

    @Override
    protected void addObserver(int playerId) {}

    void onStateChanged() {
        if ((state() == RoomState.WAITING) && !getDisconnectedPlayers().isEmpty()) {
            // Notify players to remove disconnected player definitely
            getDisconnectedPlayers().forEach(playerId -> broadcast(
                    MessageBuilder.clubLeaveRoom(playerId, RoomLeaveReason.DISCONNECTED)));
            getDisconnectedPlayers().clear();
        }
    }

    public byte getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(byte totalWins) {
        if (totalWins >= 0) {
            this.totalWins = totalWins;
            broadcast(MessageBuilder.clubUpdateWins(this));
        }
    }

    public byte getWinStreak() {
        return winStreak;
    }

    public void setWinStreak(byte winStreak) {
        this.winStreak = winStreak;
    }

    public int getChallengeTarget() {
        return challengeTarget;
    }

    public void setChallengeTarget(int challengeTarget) {
        if (challengeTarget != 0) {
            setState(RoomState.APPLYING);
            winStreakCache.setWins((byte) 0);
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

        if (challengeId <= 0) {
            winStreakCache.setWins((byte) 0);
        }
    }

    public ClubRoom() {
        setMaxSize(RoomSize.SIZE_2V2);
    }
}
