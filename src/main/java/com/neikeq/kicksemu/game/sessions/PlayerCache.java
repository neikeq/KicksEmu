package com.neikeq.kicksemu.game.sessions;

import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;

import java.util.Map;

public class PlayerCache {

    private Integer owner;
    private Integer clubId;

    private Integer defaultHead;
    private Integer defaultShirts;
    private Integer defaultPants;
    private Integer defaultShoes;

    private Short animation;

    private String name;

    private Map<Integer, Item> items;
    private Map<Integer, Skill> skills;
    private Map<Integer, Celebration> celes;
    private Map<Integer, Training> learns;

    public void clear() {
        owner = null;
        clubId = null;
        defaultHead = null;
        defaultShirts = null;
        defaultPants = null;
        defaultShoes = null;
        animation = null;
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

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public Integer getClubId() {
        return clubId;
    }

    public void setClubId(Integer clubId) {
        this.clubId = clubId;
    }

    public Integer getDefaultHead() {
        return defaultHead;
    }

    public void setDefaultHead(Integer defaultHead) {
        this.defaultHead = defaultHead;
    }

    public Integer getDefaultShirts() {
        return defaultShirts;
    }

    public void setDefaultShirts(Integer defaultShirts) {
        this.defaultShirts = defaultShirts;
    }

    public Integer getDefaultPants() {
        return defaultPants;
    }

    public void setDefaultPants(Integer defaultPants) {
        this.defaultPants = defaultPants;
    }

    public Integer getDefaultShoes() {
        return defaultShoes;
    }

    public void setDefaultShoes(Integer defaultShoes) {
        this.defaultShoes = defaultShoes;
    }

    public Short getAnimation() {
        return animation;
    }

    public void setAnimation(Short animation) {
        this.animation = animation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, Item> getItems() {
        return items;
    }

    public void setItems(Map<Integer, Item> items) {
        this.items = items;
    }

    public Map<Integer, Skill> getSkills() {
        return skills;
    }

    public void setSkills(Map<Integer, Skill> skills) {
        this.skills = skills;
    }

    public Map<Integer, Celebration> getCeles() {
        return celes;
    }

    public void setCeles(Map<Integer, Celebration> celes) {
        this.celes = celes;
    }

    public Map<Integer, Training> getLearns() {
        return learns;
    }

    public void setLearns(Map<Integer, Training> learns) {
        this.learns = learns;
    }
}
