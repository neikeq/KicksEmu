package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.game.rooms.challenges.Challenge;
import com.neikeq.kicksemu.game.rooms.challenges.ChallengeOrganizer;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ChallengeRoom extends Room implements Observer {

    private final Challenge challenge;

    @Override
    public void removeRoom() {
        synchronized (locker) {
            if (challenge != null) {
                ChallengeOrganizer.remove(challenge);

                challenge.getRedTeam().quitChallenge();
                challenge.getBlueTeam().quitChallenge();
            }
        }
    }

    public void addPlayersFromRooms(ClubRoom redTeam, ClubRoom blueTeam) {
        synchronized (locker) {
            getPlayers().putAll(redTeam.getPlayers());
            getPlayers().putAll(blueTeam.getPlayers());
            getRedTeam().addAll(redTeam.getRedTeam());
            getBlueTeam().addAll(redTeam.getRedTeam());
            getPlayers().values().forEach(this::onPlayerJoined);
        }
    }

    public void removePlayer(Session session, RoomLeaveReason reason) {
        synchronized (locker) {
            onPlayerLeaved(session, reason);
        }
    }

    @Override
    public RoomTeam swapPlayerTeam(int playerId, RoomTeam currentTeam) {
        return currentTeam;
    }

    @Override
    protected void onPlayerLeaved(Session session, RoomLeaveReason reason) {
        synchronized (locker) {
            super.onPlayerLeaved(session, reason);
            removeRoom();
        }
    }

    @Override
    protected void notifyAboutNewPlayer(Session session) { }

    @Override
    protected ServerMessage roomInfoMessage() {
        return MessageBuilder.challengeRoomInfo(this);
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

    public boolean isTraining() {
        return false;
    }

    public void setMaster(int master) {
        if (getMaster() == 0) {
            super.setMaster(master);
        }
    }

    public void setHost(int host) {
        if (getHost() == 0) {
            super.setHost(host);
        }
    }

    public ChallengeRoom(Challenge challenge) {
        super();
        this.challenge = challenge;
        setId(challenge.getId());
    }

    @Override
    public void update(Observable observable, Object o) {
        Challenge challenge = (Challenge) observable;

        if (challenge.isCanceled()) {
            removeRoom();
        }
    }
}
