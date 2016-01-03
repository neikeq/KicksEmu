package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.game.rooms.enums.RoomAccessType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RoomManager {

    public static final Object ROOMS_LOCKER = new Object();

    private static final Map<Integer, Room> ROOMS = new HashMap<>();
    private static final int ROOMS_PER_PAGE = 5;

    public static Room getRoomById(Integer id) {
        synchronized (ROOMS_LOCKER) {
            return ROOMS.get(id);
        }
    }

    public static void addRoom(Room room) {
        synchronized (ROOMS_LOCKER) {
            if (!ROOMS.containsKey(room.getId())) {
                ROOMS.put(room.getId(), room);
            }
        }
    }

    public static void removeRoom(Integer id) {
        synchronized (ROOMS_LOCKER) {
            ROOMS.remove(id);
        }
    }

    public static int roomsCount() {
        synchronized (ROOMS_LOCKER) {
            return ROOMS.size();
        }
    }

    /**
     * Returns the smallest missing key in rooms map.<br>
     * Required to get an id for new rooms.
     */
    public static int getSmallestMissingIndex() {
        synchronized (ROOMS_LOCKER) {
            int i;

            for (i = 1; i <= ROOMS.size(); i++) {
                if (!ROOMS.containsKey(i)) {
                    return i;
                }
            }

            return i;
        }
    }

    /**
     * Returns a map containing the rooms from the specified page.
     * @param page the page to get the rooms from
     * @return a map with a maximum length of {@value #ROOMS_PER_PAGE}
     * containing the rooms from the specified page
     */
    public static Map<Integer, Room> getRoomsFromPage(int page) {
        Set<Integer> indexes = ROOMS.keySet();
        final Map<Integer, Room> pageRooms = new HashMap<>();

        int startIndex = page * ROOMS_PER_PAGE;

        indexes.stream().filter(id -> id >= startIndex).limit(ROOMS_PER_PAGE)
                .forEach(id -> pageRooms.put(id, ROOMS.get(id)));

        return pageRooms;
    }

    /**
     * Return a waiting room (without password) whose level requirements
     * allow this player level, or null if no room is found.
     * If more than one room is found, returns the room with more players.
     * @param level the player level
     * @return the waiting room or null if no room was found for this player
     */
    public static Room getQuickRoom(short level) {
        List<Room> freeRooms = ROOMS.values().stream()
                .filter(r -> r.isWaiting() && (r.getAccessType() != RoomAccessType.PASSWORD) &&
                        r.isNotFull() && r.isLevelAllowed(level))
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.sort(freeRooms, (r1, r2) ->
                Byte.compare(r2.getCurrentSize(), r1.getCurrentSize()));

        return !freeRooms.isEmpty() ? freeRooms.get(0) : null;
    }
}
