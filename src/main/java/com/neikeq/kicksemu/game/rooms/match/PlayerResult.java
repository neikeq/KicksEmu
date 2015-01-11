package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

public class PlayerResult {

    private final int playerId;
    private final short goals;
    private final short assists;
    private final short blocks;
    private final short shots;
    private final short steals;
    private final short tackles;
    private final short votePoints;
    private final short ballControl;

    private int experience;
    private int points;

    public void appendResult(ServerMessage msg) {
        msg.append(playerId);
        msg.append(experience);
        msg.append(points);
        msg.append(goals);
        msg.append(assists);
        msg.append(blocks);
        msg.append(shots);
        msg.append(steals);
        msg.append(tackles);
        msg.appendZeros(10);
        msg.append(votePoints);
        msg.appendZeros(2);
        msg.append(ballControl);
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

    public PlayerResult(int playerId, short goals, short assists, short blocks, short shots,
                        short steals, short tackles, short votePoints, short ballControl) {
        this.playerId = playerId;
        this.goals = goals;
        this.assists = assists;
        this.blocks = blocks;
        this.shots = shots;
        this.steals = steals;
        this.tackles = tackles;
        this.votePoints = votePoints;
        this.ballControl = ballControl;
        this.experience = 0;
        this.points = 0;
    }

    public int getPlayerId() {
        return playerId;
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

    public short getVotePoints() {
        return votePoints;
    }

    public short getBallControl() {
        return ballControl;
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
