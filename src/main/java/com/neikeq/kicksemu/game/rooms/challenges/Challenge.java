package com.neikeq.kicksemu.game.rooms.challenges;

import com.neikeq.kicksemu.game.rooms.ChallengeRoom;
import com.neikeq.kicksemu.game.rooms.ClubRoom;

import java.util.Observable;

public class Challenge extends Observable {

    private final int id;
    private final ClubRoom redTeam;
    private final ClubRoom blueTeam;
    private final ChallengeRoom room;
    private boolean canceled;

    public void cancel() {
        canceled = true;
        setChanged();
        notifyObservers();
    }

    public Challenge(int id, ChallengeRoom room, ClubRoom redTeam, ClubRoom blueTeam) {
        this.id = id;
        this.redTeam = redTeam;
        this.blueTeam = blueTeam;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public ClubRoom getRedTeam() {
        return redTeam;
    }

    public ClubRoom getBlueTeam() {
        return blueTeam;
    }

    public ChallengeRoom getRoom() {
        return room;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
