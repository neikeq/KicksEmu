package com.neikeq.kicksemu.game.clubs;

public class ClubUniform {

    private int homeShirts;
    private int homePants;
    private int homeSocks;
    private int homeWrist;

    private int awayShirts;
    private int awayPants;
    private int awaySocks;
    private int awayWrist;

    public ClubUniform() {
        this(0, 0, 0, 0, 0, 0, 0, 0);
    }


    public ClubUniform(int homeShirts, int homePants, int homeSocks, int homeWrist,
                       int awayShirts, int awayPants, int awaySocks, int awayWrist) {
        this.homeShirts = homeShirts;
        this.homePants = homePants;
        this.homeSocks = homeSocks;
        this.homeWrist = homeWrist;
        this.awayShirts = awayShirts;
        this.awayPants = awayPants;
        this.awaySocks = awaySocks;
        this.awayWrist = awayWrist;
    }

    public int getHomeShirts() {
        return homeShirts;
    }

    public int getHomePants() {
        return homePants;
    }

    public int getHomeSocks() {
        return homeSocks;
    }

    public int getHomeWrist() {
        return homeWrist;
    }

    public int getAwayShirts() {
        return awayShirts;
    }

    public int getAwayPants() {
        return awayPants;
    }

    public int getAwaySocks() {
        return awaySocks;
    }

    public int getAwayWrist() {
        return awayWrist;
    }
}
