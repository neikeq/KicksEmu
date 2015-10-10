package com.neikeq.kicksemu.game.sessions;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.types.Animation;
import com.neikeq.kicksemu.game.inventory.products.Celebration;
import com.neikeq.kicksemu.game.inventory.products.DefaultClothes;
import com.neikeq.kicksemu.game.inventory.products.Item;
import com.neikeq.kicksemu.game.inventory.products.Skill;
import com.neikeq.kicksemu.game.inventory.products.Training;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionCache {

    private final Session parent;

    private DefaultClothes defaultClothes = null;
    private String name = null;
    private Integer owner = null;
    private Animation animation = null;
    private Short position = null;

    private Map<Integer, Item> items = null;
    private Map<Integer, Skill> skills = null;
    private Map<Integer, Celebration> celes = null;
    private Map<Integer, Training> learns = null;

    public void clear() {
        owner = null;
        defaultClothes = null;
        animation = null;
        position = null;
        name = null;

        if (items != null) {
            items.clear();
            items = null;
        }

        if (skills != null) {
            skills.clear();
            skills = null;
        }

        if (celes != null) {
            celes.clear();
            celes = null;
        }

        if (learns != null) {
            learns.clear();
            learns = null;
        }
    }

    public SessionCache(Session parent) {
        this.parent = parent;
    }

    public Integer getOwner(Connection ... con) {
        if (owner == null) {
            owner = PlayerInfo.getOwner(parent.getPlayerId(), con);
        }

        return owner;
    }

    public Animation getAnimation(Connection ... con) {
        if (animation == null) {
            animation = Animation.fromShort(PlayerInfo.getAnimation(parent.getPlayerId(), con));
        }

        return animation;
    }

    public String getName(Connection ... con) {
        if (name == null) {
            name = PlayerInfo.getName(parent.getPlayerId(), con);
        }

        return name;
    }

    public Map<Integer, Item> getItems(Connection ... con) {
        if (items == null) {
            items = PlayerInfo.getInventoryItems(parent.getPlayerId(), con);
        }

        items = items.values().stream()
                .filter(i ->
                        (i.getExpiration().isUsage() && i.getUsages() > 0) ||
                                (i.getExpiration().isDays() &&
                                        i.getTimestampExpire().after(DateUtils.getTimestamp())) ||
                                i.getExpiration().isPermanent())
                .collect(Collectors.toMap(Item::getInventoryId, i -> i,
                        (i1, i2) -> null, LinkedHashMap::new));

        return items;
    }

    public Map<Integer, Skill> getSkills(Connection ... con) {
        if (skills == null) {
            skills = PlayerInfo.getInventorySkills(parent, con);
        }

        skills = skills.values().stream()
                .filter(s -> s.getTimestampExpire().after(DateUtils.getTimestamp()) ||
                        s.getExpiration().isPermanent())
                .collect(Collectors.toMap(Skill::getInventoryId, s -> s,
                        (s1, s2) -> null, LinkedHashMap::new));

        return skills;
    }

    public Map<Integer, Celebration> getCeles(Connection ... con) {
        if (celes == null) {
            celes = PlayerInfo.getInventoryCelebration(parent.getPlayerId(), con);
        }

        celes = celes.values().stream()
                .filter(c -> c.getTimestampExpire().after(DateUtils.getTimestamp()) ||
                        c.getExpiration().isPermanent())
                .collect(Collectors.toMap(Celebration::getInventoryId, c -> c,
                        (c1, c2) -> null, LinkedHashMap::new));

        return celes;
    }

    public Map<Integer, Training> getLearns(Connection ... con) {
        if (learns == null) {
            learns = PlayerInfo.getInventoryTraining(parent.getPlayerId(), con);
        }

        return learns;
    }

    public DefaultClothes getDefaultClothes(Connection ... con) {
        if (defaultClothes == null) {
            defaultClothes = PlayerInfo.getDefaultClothes(parent.getPlayerId(), con);
        }

        return defaultClothes;
    }

    public Short getPosition(Connection ... con) {
        if (position == null) {
            position = PlayerInfo.getPosition(parent.getPlayerId(), con);
        }

        return position;
    }
}
