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
import java.util.Optional;
import java.util.stream.Collectors;

public class SessionCache {

    private final Session parent;

    private Optional<DefaultClothes> defaultClothes = Optional.empty();
    private Optional<String> name = Optional.empty();
    private Optional<Integer> owner = Optional.empty();
    private Optional<Animation> animation = Optional.empty();
    private Optional<Short> position = Optional.empty();

    private Optional<Map<Integer, Item>> items = Optional.empty();
    private Optional<Map<Integer, Skill>> skills = Optional.empty();
    private Optional<Map<Integer, Celebration>> celebrations = Optional.empty();
    private Optional<Map<Integer, Training>> learns = Optional.empty();

    public void clear() {
        owner = Optional.empty();
        defaultClothes = Optional.empty();
        animation = Optional.empty();
        position = Optional.empty();
        name = Optional.empty();

        items = items.flatMap(i -> {
            i.clear();
            return Optional.empty();
        });

        skills = skills.flatMap(i -> {
            i.clear();
            return Optional.empty();
        });

        celebrations = celebrations.flatMap(i -> {
            i.clear();
            return Optional.empty();
        });

        learns = learns.flatMap(i -> {
            i.clear();
            return Optional.empty();
        });
    }

    public SessionCache(Session parent) {
        this.parent = parent;
    }

    public Integer getOwner(ConnectionRef ... con) {
        owner = Optional.of(owner.orElse(PlayerInfo.getOwner(parent.getPlayerId(), con)));
        return owner.get();
    }

    public Animation getAnimation(ConnectionRef ... con) {
        animation = Optional.of(animation
                .orElse(Animation.fromShort(PlayerInfo.getAnimation(parent.getPlayerId(), con))));
        return animation.get();
    }

    public String getName(ConnectionRef ... con) {
        name = Optional.of(name.orElse(PlayerInfo.getName(parent.getPlayerId(), con)));
        return name.get();
    }

    public Map<Integer, Item> getItems(ConnectionRef ... con) {
        items = Optional.of(items.orElse(PlayerInfo.getInventoryItems(parent.getPlayerId(), con)))
                .map(i -> i.values().stream()
                        .filter(it ->
                                (it.getExpiration().isUsage() && (it.getUsages() > 0)) ||
                                        (it.getExpiration().isDays() &&
                                                it.getTimestampExpire()
                                                        .after(DateUtils.getTimestamp())) ||
                                        it.getExpiration().isPermanent())
                            .collect(Collectors.toMap(Item::getInventoryId, it -> it,
                                    (i1, i2) -> null, LinkedHashMap::new)));
        return items.get();
    }

    public void addItem(int inventoryId, Item item) {
        items.ifPresent(i -> i.put(inventoryId, item));
    }

    public Map<Integer, Skill> getSkills(ConnectionRef ... con) {
        skills = Optional.of(skills.orElse(PlayerInfo.getInventorySkills(parent, con)))
                .map(i -> i.values().stream()
                        .filter(s -> s.getTimestampExpire().after(DateUtils.getTimestamp()) ||
                                s.getExpiration().isPermanent())
                        .collect(Collectors.toMap(Skill::getInventoryId, s -> s,
                                (s1, s2) -> null, LinkedHashMap::new)));
        return skills.get();
    }

    public void addSkill(int inventoryId, Skill skill) {
        skills.ifPresent(i -> i.put(inventoryId, skill));
    }

    public Map<Integer, Celebration> getCelebrations(ConnectionRef ... con) {
        celebrations = Optional
                .of(celebrations
                        .orElse(PlayerInfo.getInventoryCelebration(parent.getPlayerId(), con)))
                .map(i -> i.values().stream()
                        .filter(c -> c.getTimestampExpire().after(DateUtils.getTimestamp()) ||
                                c.getExpiration().isPermanent())
                        .collect(Collectors.toMap(Celebration::getInventoryId, c -> c,
                                (c1, c2) -> null, LinkedHashMap::new)));
        return celebrations.get();
    }

    public void addCele(int inventoryId, Celebration cele) {
        celebrations.ifPresent(i -> i.put(inventoryId, cele));
    }

    public Map<Integer, Training> getLearns(ConnectionRef ... con) {
        learns = Optional.of(learns
                .orElse(PlayerInfo.getInventoryTraining(parent.getPlayerId(), con)));
        return learns.get();
    }

    public void addLearn(int inventoryId, Training learn) {
        learns.ifPresent(i -> i.put(inventoryId, learn));
    }

    public DefaultClothes getDefaultClothes(ConnectionRef ... con) {
        defaultClothes = Optional.of(defaultClothes
                .orElse(PlayerInfo.getDefaultClothes(parent.getPlayerId(), con)));
        return defaultClothes.get();
    }

    public Short getPosition(ConnectionRef ... con) {
        position = Optional.of(position.orElse(PlayerInfo.getPosition(parent.getPlayerId(), con)));
        return position.get();
    }
}
