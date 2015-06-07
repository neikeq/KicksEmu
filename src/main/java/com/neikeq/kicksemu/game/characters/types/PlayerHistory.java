package com.neikeq.kicksemu.game.characters.types;

public class PlayerHistory {

    private int matches = 0;
    private int wins = 0;
    private int draws = 0;
    private int mom = 0;
    private int validGoals = 0;
    private int validAssists = 0;
    private int validInterception = 0;
    private int validShooting = 0;
    private int validStealing = 0;
    private int validTackling = 0;
    private int shooting = 0;
    private int stealing = 0;
    private int tackling = 0;
    private int totalPoints = 0;

    public PlayerHistory() {}

    public PlayerHistory(int matches, int wins, int draws, int mom,
                         int validGoals, int validAssists, int validInterception,
                         int validShooting, int validStealing, int validTackling,
                         int shooting, int stealing, int tackling, int totalPoints) {
        this.matches = matches;
        this.wins = wins;
        this.draws = draws;
        this.mom = mom;
        this.validGoals = validGoals;
        this.validAssists = validAssists;
        this.validInterception = validInterception;
        this.validShooting = validShooting;
        this.validStealing = validStealing;
        this.validTackling = validTackling;
        this.shooting = shooting;
        this.stealing = stealing;
        this.tackling = tackling;
        this.totalPoints = totalPoints;
    }

    public int getMatches() {
        return matches;
    }

    public void sumMatches(int matches) {
        this.matches += matches;
    }

    public int getWins() {
        return wins;
    }

    public void sumWins(int wins) {
        this.wins += wins;
    }

    public int getDraws() {
        return draws;
    }

    public void sumDraws(int draws) {
        this.draws += draws;
    }

    public int getMom() {
        return mom;
    }

    public void sumMom(int mom) {
        this.mom += mom;
    }

    public int getValidGoals() {
        return validGoals;
    }

    public void sumValidGoals(int validGoals) {
        this.validGoals += validGoals;
    }

    public int getValidAssists() {
        return validAssists;
    }

    public void sumValidAssists(int validAssists) {
        this.validAssists += validAssists;
    }

    public int getValidInterception() {
        return validInterception;
    }

    public void sumValidInterception(int validInterception) {
        this.validInterception += validInterception;
    }

    public int getValidShooting() {
        return validShooting;
    }

    public void sumValidShooting(int validShooting) {
        this.validShooting += validShooting;
    }

    public int getValidStealing() {
        return validStealing;
    }

    public void sumValidStealing(int validStealing) {
        this.validStealing += validStealing;
    }

    public int getValidTackling() {
        return validTackling;
    }

    public void sumValidTackling(int validTackling) {
        this.validTackling += validTackling;
    }

    public int getShooting() {
        return shooting;
    }

    public void sumShooting(int shooting) {
        this.shooting += shooting;
    }

    public int getStealing() {
        return stealing;
    }

    public void sumStealing(int stealing) {
        this.stealing += stealing;
    }

    public int getTackling() {
        return tackling;
    }

    public void sumTackling(int tackling) {
        this.tackling += tackling;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void sumTotalPoints(int totalPoints) {
        this.totalPoints += totalPoints;
    }
}
