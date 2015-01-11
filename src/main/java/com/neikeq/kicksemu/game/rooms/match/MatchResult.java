package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.network.packets.in.ClientMessage;

import java.util.ArrayList;
import java.util.List;

public class MatchResult {

    private final int mom;
    private final short countdown;
    private final boolean goldenTime;
    private final boolean experience;
    private final TeamResult redTeam;
    private final TeamResult blueTeam;
    private final List<PlayerResult> redPlayers;
    private final List<PlayerResult> bluePlayers;

    public static MatchResult fromMessage(ClientMessage msg, int redTeamSize, int blueTeamSize) {
        int mom = msg.readInt();
        TeamResult redTeam = TeamResult.fromMessage(msg);
        TeamResult blueTeam = TeamResult.fromMessage(msg);
        List<PlayerResult> redPlayers = new ArrayList<>();
        List<PlayerResult> bluePlayers = new ArrayList<>();

        for (int i = 0; i < redTeamSize; i++) {
            redPlayers.add(PlayerResult.fromMessage(msg));
        }

        msg.ignoreBytes(40 * (5 - redTeamSize));

        for (int i = 0; i < blueTeamSize; i++) {
            bluePlayers.add(PlayerResult.fromMessage(msg));
        }

        msg.ignoreBytes(40 * (5 - blueTeamSize));

        short countdown = msg.readShort();
        boolean goldenTime = msg.readBoolean();
        boolean experience = msg.readBoolean();

        return new MatchResult(mom, countdown, goldenTime, experience, redTeam,
                blueTeam, redPlayers, bluePlayers);
    }

    public MatchResult(int mom, short countdown, boolean goldenTime,
                       boolean experience, TeamResult redTeam, TeamResult blueTeam,
                       List<PlayerResult> redPlayers, List<PlayerResult> bluePlayers) {
        this.mom = mom;
        this.countdown = countdown;
        this.goldenTime = goldenTime;
        this.experience = experience;
        this.redTeam = redTeam;
        this.blueTeam = blueTeam;
        this.redPlayers = redPlayers;
        this.bluePlayers = bluePlayers;
    }

    public int getMom() {
        return mom;
    }

    public short getCountdown() {
        return countdown;
    }

    public boolean isGoldenTime() {
        return goldenTime;
    }

    public boolean isExperience() {
        return experience;
    }

    public TeamResult getRedTeam() {
        return redTeam;
    }

    public TeamResult getBlueTeam() {
        return blueTeam;
    }

    public List<PlayerResult> getRedPlayers() {
        return redPlayers;
    }

    public List<PlayerResult> getBluePlayers() {
        return bluePlayers;
    }
}
