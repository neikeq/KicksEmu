package com.neikeq.kicksemu.game.characters;

public class PlayerStats {

    private short running;
    private short endurance;
    private short agility;
    private short ballControl;
    private short dribbling;
    private short stealing;
    private short tackling;
    private short heading;
    private short shortShots;
    private short longShots;
    private short crossing;
    private short shortPasses;
    private short longPasses;
    private short marking;
    private short goalkeeping;
    private short punching;
    private short defense;

    public PlayerStats() {
        this.running = 0;
        this.endurance = 0;
        this.agility = 0;
        this.ballControl = 0;
        this.dribbling = 0;
        this.stealing = 0;
        this.tackling = 0;
        this.heading = 0;
        this.shortShots = 0;
        this.longShots = 0;
        this.crossing = 0;
        this.shortPasses = 0;
        this.longPasses = 0;
        this.marking = 0;
        this.goalkeeping = 0;
        this.punching = 0;
        this.defense = 0;
    }

    public PlayerStats(int running, int endurance, int agility, int ballControl,
                       int dribbling, int stealing, int tackling, int heading,
                       int shortShots, int longShots, int crossing,
                       int shortPasses, int longPasses, int marking,
                       int goalkeeping, int punching, int defense) {
        this.running = (short) running;
        this.endurance = (short) endurance;
        this.agility = (short) agility;
        this.ballControl = (short) ballControl;
        this.dribbling = (short) dribbling;
        this.stealing = (short) stealing;
        this.tackling = (short) tackling;
        this.heading = (short) heading;
        this.shortShots = (short) shortShots;
        this.longShots = (short) longShots;
        this.crossing = (short) crossing;
        this.shortPasses = (short) shortPasses;
        this.longPasses = (short) longPasses;
        this.marking = (short) marking;
        this.goalkeeping = (short) goalkeeping;
        this.punching = (short) punching;
        this.defense = (short) defense;
    }

    public PlayerStats(PlayerStats stats) {
        this.running = stats.getRunning();
        this.endurance = stats.getEndurance();
        this.agility = stats.getAgility();
        this.ballControl = stats.getBallControl();
        this.dribbling = stats.getDribbling();
        this.stealing = stats.getStealing();
        this.tackling = stats.getTackling();
        this.heading = stats.getHeading();
        this.shortShots = stats.getShortShots();
        this.longShots = stats.getLongShots();
        this.crossing = stats.getCrossing();
        this.shortPasses = stats.getShortPasses();
        this.longPasses = stats.getLongPasses();
        this.marking = stats.getMarking();
        this.goalkeeping = stats.getGoalkeeping();
        this.punching = stats.getPunching();
        this.defense = stats.getDefense();
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

    public void sumRunning(short running) {
        this.running += running;
    }

    public void sumEndurance(short endurance) {
        this.endurance += endurance;
    }

    public void sumAgility(short agility) {
        this.agility += agility;
    }

    public void sumBallControl(short ballControl) {
        this.ballControl += ballControl;
    }

    public void sumDribbling(short dribbling) {
        this.dribbling += dribbling;
    }

    public void sumStealing(short stealing) {
        this.stealing += stealing;
    }

    public void sumTackling(short tackling) {
        this.tackling += tackling;
    }

    public void sumHeading(short heading) {
        this.heading += heading;
    }

    public void sumShortShots(short shortShots) {
        this.shortShots += shortShots;
    }

    public void sumLongShots(short longShots) {
        this.longShots += longShots;
    }

    public void sumCrossing(short crossing) {
        this.crossing += crossing;
    }

    public void sumShortPasses(short shortPasses) {
        this.shortPasses += shortPasses;
    }

    public void sumLongPasses(short longPasses) {
        this.longPasses += longPasses;
    }

    public void sumMarking(short marking) {
        this.marking += marking;
    }

    public void sumGoalkeeping(short goalkeeping) {
        this.goalkeeping += goalkeeping;
    }

    public void sumPunching(short punching) {
        this.punching += punching;
    }

    public void sumDefense(short defense) {
        this.defense += defense;
    }
}
