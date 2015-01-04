package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class InventoryUtils {

    public static Timestamp expirationToTimestamp(Expiration expiration) {
        int days = expiration.toInt() % 1000;

        return DateUtils.toTimestamp(DateUtils.addDays(DateUtils.getSqlDate(), days));
    }

    public static byte getSmallestMissingIndex(Collection<Skill> skills) {
        List<Byte> indexes = new ArrayList<>();

        indexes.addAll(skills.stream().map(Skill::getSelectionIndex)
                .collect(Collectors.toList()));

        for (byte i = 1; i <= skills.size() + 1; i++) {
            if (!indexes.contains(i)) {
                return i;
            }
        }

        return 1;
    }

    public static int getSmallestMissingId(Collection<Skill> skills) {
        List<Integer> ids = new ArrayList<>();

        ids.addAll(skills.stream().map(Skill::getInventoryId).collect(Collectors.toList()));

        for (int i = 0; i < skills.size() + 1; i++) {
            if (!ids.contains(i)) {
                return i;
            }
        }

        return 1;
    }

    public static Skill getByIdFromMap(Map<Integer, Skill> skills, int skillId) {
        Optional<Skill> skill = skills.values().stream()
                .filter(s -> s.getId() == skillId).findFirst();
        return skill.isPresent() ? skill.get() : null;
    }
}
