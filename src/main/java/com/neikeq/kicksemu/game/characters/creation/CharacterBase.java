package com.neikeq.kicksemu.game.characters.creation;

public class CharacterBase {

    private int owner;

    private String name;

    private short statsPoints;

    private short animation;
    private short face;

    private int defaultHead;
    private int defaultShirts;
    private int defaultPants;
    private int defaultShoes;

    private short position;

    private short statsRunning;
    private short statsEndurance;
    private short statsAgility;
    private short statsBallControl;
    private short statsDribbling;
    private short statsStealing;
    private short statsTackling;
    private short statsHeading;
    private short statsShortShots;
    private short statsLongShots;
    private short statsCrossing;
    private short statsShortPasses;
    private short statsLongPasses;
    private short statsMarking;
    private short statsGoalkeeping;
    private short statsPunching;
    private short statsDefense;

    public int getTotalStats() {
        return statsPoints + statsRunning + statsEndurance + statsAgility + statsBallControl +
                statsDribbling + statsStealing + statsTackling + statsHeading + statsShortShots +
                statsLongShots + statsCrossing + statsShortPasses + statsLongPasses +
                statsMarking + statsGoalkeeping + statsPunching + statsDefense;
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

    public short getAnimation() {
        return animation;
    }

    public void setAnimation(short animation) {
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

    public short getStatsRunning() {
        return statsRunning;
    }

    public void setStatsRunning(short statsRunning) {
        this.statsRunning = statsRunning;
    }

    public short getStatsEndurance() {
        return statsEndurance;
    }

    public void setStatsEndurance(short statsEndurance) {
        this.statsEndurance = statsEndurance;
    }

    public short getStatsAgility() {
        return statsAgility;
    }

    public void setStatsAgility(short statsAgility) {
        this.statsAgility = statsAgility;
    }

    public short getStatsBallControl() {
        return statsBallControl;
    }

    public void setStatsBallControl(short statsBallControl) {
        this.statsBallControl = statsBallControl;
    }

    public short getStatsDribbling() {
        return statsDribbling;
    }

    public void setStatsDribbling(short statsDribbling) {
        this.statsDribbling = statsDribbling;
    }

    public short getStatsStealing() {
        return statsStealing;
    }

    public void setStatsStealing(short statsStealing) {
        this.statsStealing = statsStealing;
    }

    public short getStatsTackling() {
        return statsTackling;
    }

    public void setStatsTackling(short statsTackling) {
        this.statsTackling = statsTackling;
    }

    public short getStatsHeading() {
        return statsHeading;
    }

    public void setStatsHeading(short statsHeading) {
        this.statsHeading = statsHeading;
    }

    public short getStatsShortShots() {
        return statsShortShots;
    }

    public void setStatsShortShots(short statsShortShots) {
        this.statsShortShots = statsShortShots;
    }

    public short getStatsLongShots() {
        return statsLongShots;
    }

    public void setStatsLongShots(short statsLongShots) {
        this.statsLongShots = statsLongShots;
    }

    public short getStatsCrossing() {
        return statsCrossing;
    }

    public void setStatsCrossing(short statsCrossing) {
        this.statsCrossing = statsCrossing;
    }

    public short getStatsShortPasses() {
        return statsShortPasses;
    }

    public void setStatsShortPasses(short statsShortPasses) {
        this.statsShortPasses = statsShortPasses;
    }

    public short getStatsLongPasses() {
        return statsLongPasses;
    }

    public void setStatsLongPasses(short statsLongPasses) {
        this.statsLongPasses = statsLongPasses;
    }

    public short getStatsMarking() {
        return statsMarking;
    }

    public void setStatsMarking(short statsMarking) {
        this.statsMarking = statsMarking;
    }

    public short getStatsGoalkeeping() {
        return statsGoalkeeping;
    }

    public void setStatsGoalkeeping(short statsGoalkeeping) {
        this.statsGoalkeeping = statsGoalkeeping;
    }

    public short getStatsPunching() {
        return statsPunching;
    }

    public void setStatsPunching(short statsPunching) {
        this.statsPunching = statsPunching;
    }

    public short getStatsDefense() {
        return statsDefense;
    }

    public void setStatsDefense(short statsDefense) {
        this.statsDefense = statsDefense;
    }
}
