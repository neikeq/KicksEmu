package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.game.rooms.enums.VictoryResult;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

public class TeamResult extends StatisticsCarrier {

    private final VictoryResult result;
    private final short votePoints;

    public void appendResult(ServerMessage msg) {
        msg.writeShort(getResult().toShort());
        msg.writeShort(getGoals());
        msg.writeShort(getAssists());
        msg.writeShort(getBlocks());
        msg.writeShort(getShots());
        msg.writeShort(getSteals());
        msg.writeShort(getTackles());
        msg.writeZeros(10);
        msg.writeShort(votePoints);
        msg.writeZeros(2);
        msg.writeShort(getBallControl());
    }

    public static TeamResult fromMessage(ClientMessage msg) {
        short result = msg.readShort();
        short goals = msg.readShort();
        short assists = msg.readShort();
        short blocks = msg.readShort();
        short shots = msg.readShort();
        short steals = msg.readShort();
        short tackles = msg.readShort();
        msg.ignoreBytes(10);
        short votePoints = msg.readShort();
        msg.ignoreBytes(2);
        short ballControl = msg.readShort();

        return new TeamResult(result, goals, assists, blocks, shots, steals,
                tackles, votePoints, ballControl);
    }

    public TeamResult() {
        result = VictoryResult.NO_GAME;
        votePoints = 0;
    }

    public TeamResult(short result, short goals, short assists, short blocks, short shots,
                      short steals, short tackles, short votePoints, short ballControl) {
        super(goals, assists, blocks, shots, steals, tackles, ballControl);
        this.result = VictoryResult.fromShort(result);
        this.votePoints = votePoints;
    }

    public VictoryResult getResult() {
        return result;
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
