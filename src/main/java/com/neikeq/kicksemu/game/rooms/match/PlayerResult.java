package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

public class PlayerResult extends StatisticsCarrier {

    private final int playerId;
    private final short votePoints;

    private int experience;
    private int points;

    public boolean hasReward() {
        return experience > 0 || points > 0;
    }

    public void appendResult(ServerMessage msg) {
        msg.writeInt(playerId);
        msg.writeInt(experience);
        msg.writeInt(points);
        msg.writeShort(goals);
        msg.writeShort(assists);
        msg.writeShort(blocks);
        msg.writeShort(shots);
        msg.writeShort(steals);
        msg.writeShort(tackles);
        msg.writeZeros(10);
        msg.writeShort(votePoints);
        msg.writeZeros(2);
        msg.writeShort(ballControl);
    }

    public static PlayerResult fromMessage(ClientMessage msg) {
        int playerId = msg.readInt();
        msg.ignoreBytes(8);
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

        return new PlayerResult(playerId, goals, assists, blocks, shots,
                steals, tackles, votePoints, ballControl);
    }

    public PlayerResult() {
        this(0, (short) 0, (short) 0, (short) 0, (short) 0,
                (short) 0, (short) 0, (short) 0, (short) 0);
    }

    public PlayerResult(int playerId, short goals, short assists, short blocks, short shots,
                        short steals, short tackles, short votePoints, short ballControl) {
        super(goals, assists, blocks, shots, steals, tackles, ballControl);
        this.playerId = playerId;
        this.votePoints = votePoints;
        this.experience = 0;
        this.points = 0;
    }

    public int getPlayerId() {
        return playerId;
    }

    public short getVotePoints() {
        return votePoints;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
