package com.neikeq.kicksemu.game.rooms.challenges;

import com.neikeq.kicksemu.game.rooms.ChallengeRoom;
import com.neikeq.kicksemu.game.rooms.ClubRoom;

import java.util.HashMap;
import java.util.Map;

public class ChallengeOrganizer {

    private static final Object LOCKER = new Object();
    private static final Map<Integer, Challenge> CHALLENGES = new HashMap<>();

    public static Challenge getChallengeById(Integer id) {
        synchronized (LOCKER) {
            return CHALLENGES.get(id);
        }
    }

    public static void add(ChallengeRoom challengeRoom, ClubRoom redTeam, ClubRoom blueTeam) {
        synchronized (LOCKER) {
            Challenge challenge = new Challenge(getSmallestMissingIndex(),
                    challengeRoom, redTeam, blueTeam);
            CHALLENGES.put(challenge.getId(), challenge);
            challengeRoom.setChallenge(challenge);
            redTeam.setChallengeId(challenge.getId());
            blueTeam.setChallengeId(challenge.getId());
        }
    }

    public static void remove(Challenge challenge) {
        synchronized (LOCKER) {
            CHALLENGES.remove(challenge.getId());
        }
    }

    /**
     * Returns the smallest missing key in rooms map.<br>
     * Required to get an id for new rooms.
     */
    private static int getSmallestMissingIndex() {
        synchronized (LOCKER) {
            int i;

            for (i = 1; i <= CHALLENGES.size(); i++) {
                if (!CHALLENGES.containsKey(i)) {
                    return i;
                }
            }

            return i;
        }
    }
}
