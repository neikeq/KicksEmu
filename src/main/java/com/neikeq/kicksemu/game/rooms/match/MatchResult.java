package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MatchResult {

    private final int mom;
    private final short countdown;
    private final boolean goldenTime;
    private final boolean experience;
    private final TeamResult redTeam;
    private final TeamResult blueTeam;
    private final List<PlayerResult> players;

    public Optional<TeamResult> getTeamResult(Optional<RoomTeam> maybeTeam) {
        return maybeTeam.map(team -> (team == RoomTeam.RED) ? redTeam : blueTeam);
    }

    public static MatchResult fromMessage(ClientMessage msg, Set<Integer> roomPlayers) {
        int mom = msg.readInt();
        TeamResult redTeam = TeamResult.fromMessage(msg);
        TeamResult blueTeam = TeamResult.fromMessage(msg);
        List<PlayerResult> players = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int playerId = msg.readInt();

            if ((playerId <= 0) || !roomPlayers.contains(playerId)) {
                msg.ignoreBytes(36);
                continue;
            }

            players.add(PlayerResult.fromMessage(msg, playerId));
        }

        short countdown = msg.readShort();
        boolean goldenTime = msg.readBoolean();
        boolean experience = msg.readBoolean();

        return new MatchResult(mom, countdown, goldenTime, experience, redTeam,
                blueTeam, players);
    }

    private MatchResult(int mom, short countdown, boolean goldenTime,
                        boolean experience, TeamResult redTeam, TeamResult blueTeam,
                        List<PlayerResult> players) {
        this.mom = mom;
        this.countdown = countdown;
        this.goldenTime = goldenTime;
        this.experience = experience;
        this.redTeam = redTeam;
        this.blueTeam = blueTeam;
        this.players = players;
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

    public List<PlayerResult> getPlayers() {
        return players;
    }
}
