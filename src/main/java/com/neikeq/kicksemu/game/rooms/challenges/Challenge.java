package com.neikeq.kicksemu.game.rooms.challenges;

import com.neikeq.kicksemu.game.rooms.ClubRoom;

import java.util.Observable;

public class Challenge extends Observable {

    private int id = -1;
    private final ClubRoom redTeam;
    private final ClubRoom blueTeam;
    private boolean canceled = false;

    public void cancel() {
        canceled = true;
        notifyObservers();
    }

    public Challenge(ClubRoom redTeam, ClubRoom blueTeam) {
        this.redTeam = redTeam;
        this.blueTeam = blueTeam;
    }

    public ClubRoom getRedTeam() {
        return redTeam;
    }

    public ClubRoom getBlueTeam() {
        return blueTeam;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
