package com.neikeq.kicksemu.game.sessions;

public class PlayerCache {

    private Integer owner;
    private Integer clubId;

    private Integer defaultHead;
    private Integer defaultShirts;
    private Integer defaultPants;
    private Integer defaultShoes;

    private Short animation;

    private String name;

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
}
