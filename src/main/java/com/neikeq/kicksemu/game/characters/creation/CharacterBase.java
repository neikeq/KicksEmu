package com.neikeq.kicksemu.game.characters.creation;

import com.neikeq.kicksemu.game.characters.types.Animation;
import com.neikeq.kicksemu.game.characters.types.PlayerStats;

class CharacterBase {

    private int owner;

    private int defaultHead;
    private int defaultShirts;
    private int defaultPants;
    private int defaultShoes;

    private short statsPoints;
    private short face;
    private short position;

    private String name;

    private Animation animation;
    private PlayerStats stats;

    public int getTotalStats() {
        return statsPoints + stats.getRunning() + stats.getEndurance() + stats.getAgility() +
                stats.getBallControl() + stats.getDribbling() + stats.getStealing() + stats.getTackling() +
                stats.getHeading() + stats.getShortShots() + stats.getLongShots() + stats.getCrossing() +
                stats.getShortPasses() + stats.getLongPasses() + stats.getMarking() + stats.getGoalkeeping() +
                stats.getPunching() + stats.getDefense();
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getStatsPoints() {
        return statsPoints;
    }

    public void setStatsPoints(short statsPoints) {
        this.statsPoints = statsPoints;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public short getFace() {
        return face;
    }

    public void setFace(short face) {
        this.face = face;
    }

    public int getDefaultHead() {
        return defaultHead;
    }

    public void setDefaultHead(int defaultHead) {
        this.defaultHead = defaultHead;
    }

    public int getDefaultShirts() {
        return defaultShirts;
    }

    public void setDefaultShirts(int defaultShirts) {
        this.defaultShirts = defaultShirts;
    }

    public int getDefaultPants() {
        return defaultPants;
    }

    public void setDefaultPants(int defaultPants) {
        this.defaultPants = defaultPants;
    }

    public int getDefaultShoes() {
        return defaultShoes;
    }

    public void setDefaultShoes(int defaultShoes) {
        this.defaultShoes = defaultShoes;
    }

    public short getPosition() {
        return position;
    }

    public void setPosition(short position) {
        this.position = position;
    }

    public PlayerStats getStats() {
        return stats;
    }

    public void setStats(PlayerStats stats) {
        this.stats = stats;
    }
}
