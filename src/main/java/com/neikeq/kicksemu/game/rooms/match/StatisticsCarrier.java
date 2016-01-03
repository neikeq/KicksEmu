package com.neikeq.kicksemu.game.rooms.match;

public class StatisticsCarrier {

    protected final short goals;
    protected final short assists;
    protected final short blocks;
    protected final short shots;
    protected final short steals;
    protected final short tackles;
    protected final short ballControl;

    public StatisticsCarrier() {
        this((short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0, (short) 0);
    }

    public StatisticsCarrier(short goals, short assists, short blocks, short shots,
                             short steals, short tackles, short ballControl) {
        this.goals = goals;
        this.assists = assists;
        this.blocks = blocks;
        this.shots = shots;
        this.steals = steals;
        this.tackles = tackles;
        this.ballControl = ballControl;
    }

    public short getGoals() {
        return goals;
    }

    public short getAssists() {
        return assists;
    }

    public short getBlocks() {
        return blocks;
    }

    public short getShots() {
        return shots;
    }

    public short getSteals() {
        return steals;
    }

    public short getTackles() {
        return tackles;
    }

    public short getBallControl() {
        return ballControl;
    }
}
