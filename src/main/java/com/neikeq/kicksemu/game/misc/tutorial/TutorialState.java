package com.neikeq.kicksemu.game.misc.tutorial;

public class TutorialState {

    private byte dribbling = 0;
    private byte passing = 0;
    private byte shooting = 0;
    private byte defense = 0;

    public TutorialState() {}

    public TutorialState(byte dribbling, byte passing, byte shooting, byte defense) {
        this.setDribbling(dribbling);
        this.setPassing(passing);
        this.setShooting(shooting);
        this.setDefense(defense);
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
