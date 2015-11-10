package com.neikeq.kicksemu.game.clubs;

public class ClubUniform {

    private final Uniform homeUniform;
    private final Uniform awayUniform;

    public Uniform getUniformByType(UniformType uniformType) {
        return uniformType == UniformType.HOME ? homeUniform : awayUniform;
    }

    public ClubUniform() {
        homeUniform = new Uniform();
        awayUniform = new Uniform();
    }


    public ClubUniform(int homeShirts, int homePants, int homeSocks, int homeWrist,
                       int awayShirts, int awayPants, int awaySocks, int awayWrist) {
        this();
        homeUniform.setShirts(homeShirts);
        homeUniform.setPants(homePants);
        homeUniform.setSocks(homeSocks);
        homeUniform.setWrist(homeWrist);
        awayUniform.setShirts(awayShirts);
        awayUniform.setPants(awayPants);
        awayUniform.setSocks(awaySocks);
        awayUniform.setWrist(awayWrist);
    }

    public Uniform getHomeUniform() {
        return homeUniform;
    }

    public Uniform getAwayUniform() {
        return awayUniform;
    }
}
