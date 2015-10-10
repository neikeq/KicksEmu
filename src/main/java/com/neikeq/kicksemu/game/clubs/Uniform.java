package com.neikeq.kicksemu.game.clubs;

public class Uniform {

    private int shirts;
    private int pants;
    private int socks;
    private int wrist;

    public Uniform() {
        this(0, 0, 0, 0);
    }

    public Uniform(int shirts, int pants, int socks, int wrist) {
        this.setShirts(shirts);
        this.setPants(pants);
        this.setSocks(socks);
        this.setWrist(wrist);
    }

    public int getShirts() {
        return shirts;
    }

    public void setShirts(int shirts) {
        this.shirts = shirts;
    }

    public int getPants() {
        return pants;
    }

    public void setPants(int pants) {
        this.pants = pants;
    }

    public int getSocks() {
        return socks;
    }

    public void setSocks(int socks) {
        this.socks = socks;
    }

    public int getWrist() {
        return wrist;
    }

    public void setWrist(int wrist) {
        this.wrist = wrist;
    }
}
