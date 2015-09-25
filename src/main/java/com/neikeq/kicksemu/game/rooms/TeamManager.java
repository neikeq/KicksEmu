package com.neikeq.kicksemu.game.rooms;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TeamManager {

    private static final Object TEAMS_LOCKER = new Object();

    private static final Map<Integer, ClubRoom> TEAMS = new HashMap<>();
    private static final int TEAMS_PER_PAGE = 10;

    public static void register(ClubRoom room) {
        synchronized (TEAMS_LOCKER) {
            if (!TEAMS.containsKey(room.getId())) {
                TEAMS.put(room.getId(), room);
            }
        }
    }

    public static void unregister(Integer id) {
        synchronized (TEAMS_LOCKER) {
            if (TEAMS.containsKey(id)) {
                ClubRoom room = TEAMS.get(id);
                int challengeTarget = room.getChallengeTarget();

                if (challengeTarget > 0) {
                    ClubRoom targetRoom = TEAMS.get(challengeTarget);

                    if (targetRoom != null && targetRoom.getChallengeTarget() == id) {
                        targetRoom.setChallengeTarget(0);
                    }
                }

                TEAMS.remove(id);
            }
        }
    }

    public static boolean isRegistered(Integer id) {
        synchronized (TEAMS_LOCKER) {
            return TEAMS.containsKey(id);
        }
    }

    /**
     * Returns a map containing the teams from the specified page.
     * @param page the page to get the teams from
     * @param excludedId the id of the team to exclude from the list
     * @return a map with a maximum length of {@value #TEAMS_PER_PAGE}
     * containing the teams from the specified page
     */
    public static Map<Integer, ClubRoom> getTeamsFromPage(int page, int excludedId) {
        Set<Integer> indexes = TEAMS.keySet();
        final Map<Integer, ClubRoom> pageRooms = new HashMap<>();

        int startIndex = page * TEAMS_PER_PAGE;

        indexes.stream().filter(id -> id != excludedId && id >= startIndex)
                .limit(TEAMS_PER_PAGE).forEach(id -> pageRooms.put(id, TEAMS.get(id)));

        return pageRooms;
    }
}
