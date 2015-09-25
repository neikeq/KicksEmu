package com.neikeq.kicksemu.game.rooms.challenges;

import com.neikeq.kicksemu.game.rooms.ChallengeRoom;
import com.neikeq.kicksemu.game.rooms.ClubRoom;

import java.util.Observable;

public class Challenge extends Observable {

    private int id = -1;
    private final ClubRoom redTeam;
    private final ClubRoom blueTeam;
    private final ChallengeRoom room;
    private boolean canceled = false;

    public void cancel() {
        canceled = true;
        setChanged();
        notifyObservers();
    }

    public Challenge(ChallengeRoom room, ClubRoom redTeam, ClubRoom blueTeam) {
        this.redTeam = redTeam;
        this.blueTeam = blueTeam;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
