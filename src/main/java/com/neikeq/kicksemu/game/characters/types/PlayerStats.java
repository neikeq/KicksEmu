package com.neikeq.kicksemu.game.characters.types;

import com.neikeq.kicksemu.game.characters.CharacterUtils;
import com.neikeq.kicksemu.utils.mutable.MutableInteger;

public class PlayerStats {

    private short running = 0;
    private short endurance = 0;
    private short agility = 0;
    private short ballControl = 0;
    private short dribbling = 0;
    private short stealing = 0;
    private short tackling = 0;
    private short heading = 0;
    private short shortShots = 0;
    private short longShots = 0;
    private short crossing = 0;
    private short shortPasses = 0;
    private short longPasses = 0;
    private short marking = 0;
    private short goalkeeping = 0;
    private short punching = 0;
    private short defense = 0;

    private static short sumStatsUpToHundred(int value, short current, MutableInteger statsPoints) {
        short add = CharacterUtils.statsUpToHundred(current, value);
        statsPoints.add(value - add);

        return add;
    }

    public static void sumStats(PlayerStats add, int factor, PlayerStats stats,
                                MutableInteger statsPoints) {
        stats.sumRunning(sumStatsUpToHundred(add.getRunning() * factor,
                stats.getRunning(), statsPoints));
        stats.sumEndurance(sumStatsUpToHundred(add.getEndurance() * factor,
                stats.getEndurance(), statsPoints));
        stats.sumAgility(sumStatsUpToHundred(add.getAgility() * factor,
                stats.getAgility(), statsPoints));
        stats.sumBallControl(sumStatsUpToHundred(add.getBallControl() * factor,
                stats.getBallControl(), statsPoints));
        stats.sumDribbling(sumStatsUpToHundred(add.getDribbling() * factor,
                stats.getDribbling(), statsPoints));
        stats.sumStealing(sumStatsUpToHundred(add.getStealing() * factor,
                stats.getStealing(), statsPoints));
        stats.sumTackling(sumStatsUpToHundred(add.getTackling() * factor,
                stats.getTackling(), statsPoints));
        stats.sumHeading(sumStatsUpToHundred(add.getHeading() * factor,
                stats.getHeading(), statsPoints));
        stats.sumShortShots(sumStatsUpToHundred(add.getShortShots() * factor,
                stats.getShortShots(), statsPoints));
        stats.sumLongShots(sumStatsUpToHundred(add.getLongShots() * factor,
                stats.getLongShots(), statsPoints));
        stats.sumCrossing(sumStatsUpToHundred(add.getCrossing() * factor,
                stats.getCrossing(), statsPoints));
        stats.sumShortPasses(sumStatsUpToHundred(add.getShortPasses() * factor,
                stats.getShortPasses(), statsPoints));
        stats.sumLongPasses(sumStatsUpToHundred(add.getLongPasses() * factor,
                stats.getLongPasses(), statsPoints));
        stats.sumMarking(sumStatsUpToHundred(add.getMarking() * factor,
                stats.getMarking(), statsPoints));
        stats.sumGoalkeeping(sumStatsUpToHundred(add.getGoalkeeping() * factor,
                stats.getGoalkeeping(), statsPoints));
        stats.sumPunching(sumStatsUpToHundred(add.getPunching() * factor,
                stats.getPunching(), statsPoints));
        stats.sumDefense(sumStatsUpToHundred(add.getDefense() * factor,
                stats.getDefense(), statsPoints));
    }

    public static PlayerStats fromArray(short[] stats) {
        if (stats.length < 17) {
            return null;
        }

        return new PlayerStats(stats[0], stats[1], stats[2], stats[3], stats[4],
                stats[5], stats[6], stats[7], stats[8], stats[9], stats[10], stats[11],
                stats[12], stats[13], stats[14], stats[15], stats[16]);
    }

    public PlayerStats() {}

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
