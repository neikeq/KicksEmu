package com.neikeq.kicksemu.game.misc.tutorial;

public class TutorialState {

    private byte dribbling;
    private byte passing;
    private byte shooting;
    private byte defense;

    public boolean isTutorialFinished() {
        return (getDribbling() == 15) && (getPassing() == 15) &&
                (getShooting() == 15) && (getDefense() == 15);
    }

    public boolean isValid() {
        return (getDribbling() <= 15) && (getPassing() <= 15) &&
                (getShooting() <= 15) && (getDefense() <= 15);
    }

    public TutorialState() {}

    public TutorialState(byte dribbling, byte passing, byte shooting, byte defense) {
        setDribbling(dribbling);
        setPassing(passing);
        setShooting(shooting);
        setDefense(defense);
    }

    public byte getDribbling() {
        return dribbling;
    }

    public byte getPassing() {
        return passing;
    }

    public byte getShooting() {
        return shooting;
    }

    public byte getDefense() {
        return defense;
    }

    public void setDribbling(byte dribbling) {
        this.dribbling = dribbling;
    }

    public void setPassing(byte passing) {
        this.passing = passing;
    }

    public void setShooting(byte shooting) {
        this.shooting = shooting;
    }

    public void setDefense(byte defense) {
        this.defense = defense;
    }
}
