package com.neikeq.kicksemu.game.sessions;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.types.Animation;
import com.neikeq.kicksemu.game.inventory.products.Celebration;
import com.neikeq.kicksemu.game.inventory.products.DefaultClothes;
import com.neikeq.kicksemu.game.inventory.products.Item;
import com.neikeq.kicksemu.game.inventory.products.Skill;
import com.neikeq.kicksemu.game.inventory.products.Training;
import com.neikeq.kicksemu.storage.ConnectionRef;
import com.neikeq.kicksemu.utils.DateUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionCache {

    private final Session parent;

    private DefaultClothes defaultClothes;
    private String name;
    private Integer owner;
    private Animation animation;
    private Short position;

    private Map<Integer, Item> items;
    private Map<Integer, Skill> skills;
    private Map<Integer, Celebration> celebrations;
    private Map<Integer, Training> learns;

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

        if (celebrations != null) {
            celebrations.clear();
            celebrations = null;
        }

        if (learns != null) {
            learns.clear();
            learns = null;
        }
    }

    public SessionCache(Session parent) {
        this.parent = parent;
    }

    public Integer getOwner(ConnectionRef ... con) {
        if (owner == null) {
            owner = PlayerInfo.getOwner(parent.getPlayerId(), con);
        }

        return owner;
    }

    public Animation getAnimation(ConnectionRef ... con) {
        if (animation == null) {
            animation = Animation.fromShort(PlayerInfo.getAnimation(parent.getPlayerId(), con));
        }

        return animation;
    }

    public String getName(ConnectionRef ... con) {
        if (name == null) {
            name = PlayerInfo.getName(parent.getPlayerId(), con);
        }

        return name;
    }

    public Map<Integer, Item> getItems(ConnectionRef ... con) {
        if (items == null) {
            items = PlayerInfo.getInventoryItems(parent.getPlayerId(), con);
        }

        items = items.values().stream()
                .filter(i ->
                        (i.getExpiration().isUsage() && (i.getUsages() > 0)) ||
                                (i.getExpiration().isDays() &&
                                        i.getTimestampExpire().after(DateUtils.getTimestamp())) ||
                                i.getExpiration().isPermanent())
                .collect(Collectors.toMap(Item::getInventoryId, i -> i,
                        (i1, i2) -> null, LinkedHashMap::new));

        return items;
    }

    public void addItem(int inventoryId, Item item) {
        if (items != null) {
            items.put(inventoryId, item);
        }
    }

    public Map<Integer, Skill> getSkills(ConnectionRef ... con) {
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

    public void addSkill(int inventoryId, Skill skill) {
        if (skills != null) {
            skills.put(inventoryId, skill);
        }
    }

    public Map<Integer, Celebration> getCelebrations(ConnectionRef ... con) {
        if (celebrations == null) {
            celebrations = PlayerInfo.getInventoryCelebration(parent.getPlayerId(), con);
        }

        celebrations = celebrations.values().stream()
                .filter(c -> c.getTimestampExpire().after(DateUtils.getTimestamp()) ||
                        c.getExpiration().isPermanent())
                .collect(Collectors.toMap(Celebration::getInventoryId, c -> c,
                        (c1, c2) -> null, LinkedHashMap::new));

        return celebrations;
    }

    public void addCele(int inventoryId, Celebration cele) {
        if (celebrations != null) {
            celebrations.put(inventoryId, cele);
        }
    }

    public Map<Integer, Training> getLearns(ConnectionRef ... con) {
        if (learns == null) {
            learns = PlayerInfo.getInventoryTraining(parent.getPlayerId(), con);
        }

        return learns;
    }

    public void addLearn(int inventoryId, Training learn) {
        if (learns != null) {
            learns.put(inventoryId, learn);
        }
    }

    public DefaultClothes getDefaultClothes(ConnectionRef ... con) {
        if (defaultClothes == null) {
            defaultClothes = PlayerInfo.getDefaultClothes(parent.getPlayerId(), con);
        }

        return defaultClothes;
    }

    public Short getPosition(ConnectionRef ... con) {
        if (position == null) {
            position = PlayerInfo.getPosition(parent.getPlayerId(), con);
        }

        return position;
    }
}
