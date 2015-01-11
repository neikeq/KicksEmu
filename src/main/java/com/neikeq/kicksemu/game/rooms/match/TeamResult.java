package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

public class TeamResult {

    private final short result;
    private final short goals;
    private final short assists;
    private final short blocks;
    private final short shots;
    private final short steals;
    private final short tackles;
    private final short votePoints;
    private final short ballControl;

    public void appendResult(ServerMessage msg) {
        msg.append(result);
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

    public TeamResult(short result, short goals, short assists, short blocks, short shots,
                      short steals, short tackles, short votePoints, short ballControl) {
        this.result = result;
        this.goals = goals;
        this.assists = assists;
        this.blocks = blocks;
        this.shots = shots;
        this.steals = steals;
        this.tackles = tackles;
        this.votePoints = votePoints;
        this.ballControl = ballControl;
    }
}
