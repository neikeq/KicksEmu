package com.neikeq.kicksemu.game.characters;

public class TutorialState {

    private byte dribbling;
    private byte passing;
    private byte shooting;
    private byte defense;

    public TutorialState() {
        this((byte)0, (byte)0, (byte)0, (byte)0);
    }

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
