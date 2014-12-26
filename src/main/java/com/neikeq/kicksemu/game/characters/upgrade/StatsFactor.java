package com.neikeq.kicksemu.game.characters.upgrade;

public class StatsFactor {

    private final short running;
    private final short endurance;
    private final short agility;
    private final short ballControl;
    private final short dribbling;
    private final short stealing;
    private final short tackling;
    private final short heading;
    private final short shortShots;
    private final short longShots;
    private final short crossing;
    private final short shortPasses;
    private final short longPasses;
    private final short marking;
    private final short goalkeeping;
    private final short punching;
    private final short defense;

    public StatsFactor(int running, int endurance, int agility, int ballControl,
                       int dribbling, int stealing, int tackling, int heading,
                       int shortShots, int longShots, int crossing,
                       int shortPasses, int longPasses, int marking,
                       int goalkeeping, int punching, int defense) {
        this.running = (short)running;
        this.endurance = (short)endurance;
        this.agility = (short)agility;
        this.ballControl = (short)ballControl;
        this.dribbling = (short)dribbling;
        this.stealing = (short)stealing;
        this.tackling = (short)tackling;
        this.heading = (short)heading;
        this.shortShots = (short)shortShots;
        this.longShots = (short)longShots;
        this.crossing = (short)crossing;
        this.shortPasses = (short)shortPasses;
        this.longPasses = (short)longPasses;
        this.marking = (short)marking;
        this.goalkeeping = (short)goalkeeping;
        this.punching = (short)punching;
        this.defense = (short)defense;
    }

    public short getRunning() {
        return running;
    }

    public short getEndurance() {
        return endurance;
    }

    public short getAgility() {
        return agility;
    }

    public short getBallControl() {
        return ballControl;
    }

    public short getDribbling() {
        return dribbling;
    }

    public short getStealing() {
        return stealing;
    }

    public short getTackling() {
        return tackling;
    }

    public short getHeading() {
        return heading;
    }

    public short getShortShots() {
        return shortShots;
    }

    public short getLongShots() {
        return longShots;
    }

    public short getCrossing() {
        return crossing;
    }

    public short getShortPasses() {
        return shortPasses;
    }

    public short getLongPasses() {
        return longPasses;
    }

    public short getMarking() {
        return marking;
    }

    public short getGoalkeeping() {
        return goalkeeping;
    }

    public short getPunching() {
        return punching;
    }

    public short getDefense() {
        return defense;
    }
}
