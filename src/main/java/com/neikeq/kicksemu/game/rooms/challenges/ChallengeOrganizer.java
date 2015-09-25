package com.neikeq.kicksemu.game.rooms.challenges;

import com.neikeq.kicksemu.game.rooms.ChallengeRoom;
import com.neikeq.kicksemu.game.rooms.ClubRoom;

import java.util.ArrayList;
import java.util.List;

public class ChallengeOrganizer {

    private static final Object LOCKER = new Object();
    private static final List<Challenge> CHALLENGES = new ArrayList<>();

    public static Challenge getChallengeById(Integer id) {
        synchronized (LOCKER) {
            return CHALLENGES.get(id);
        }
    }

    public static void add(ChallengeRoom challengeRoom, ClubRoom redTeam, ClubRoom blueTeam) {
        synchronized (LOCKER) {
            Challenge challenge = new Challenge(challengeRoom, redTeam, blueTeam);
            CHALLENGES.add(challenge);
            challenge.setId(CHALLENGES.indexOf(challenge));
            challengeRoom.setChallenge(challenge);
            redTeam.setChallengeId(challenge.getId());
            blueTeam.setChallengeId(challenge.getId());
        }
    }

    public static void remove(Challenge challenge) {
        synchronized (LOCKER) {
            CHALLENGES.remove(challenge);
        }
    }
}
