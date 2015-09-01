package com.neikeq.kicksemu.game.rooms;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TeamManager {

    public static final Object TEAMS_LOCKER = new Object();

    private static final Map<Integer, Room> TEAMS = new HashMap<>();
    private static final int TEAMS_PER_PAGE = 10;

    public static void register(Room room) {
        synchronized (TEAMS_LOCKER) {
            if (!TEAMS.containsKey(room.getId())) {
                TEAMS.put(room.getId(), room);
            }
        }
    }

    public static void unregister(Integer id) {
        synchronized (TEAMS_LOCKER) {
            TEAMS.remove(id);
        }
    }

    public static boolean isRegistered(Integer id) {
        return TEAMS.containsKey(id);
    }

    /**
     * Returns a map containing the teams from the specified page.
     * @param page the page to get the teams from
     * @return a map with a maximum length of {@value #TEAMS_PER_PAGE}
     * containing the teams from the specified page
     */
    public static Map<Integer, Room> getTeamsFromPage(int page) {
        Set<Integer> indexes = TEAMS.keySet();
        Map<Integer, Room> pageRooms = new HashMap<>();

        int startIndex = page * TEAMS_PER_PAGE;
        int i = 0;

        for (int index : indexes) {
            if (i >= startIndex) {
                pageRooms.put(index, TEAMS.get(index));

                if (pageRooms.size() > TEAMS_PER_PAGE) {
                    break;
                }
            }

            i++;
        }

        return pageRooms;
    }
}
