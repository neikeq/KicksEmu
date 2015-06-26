package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.game.inventory.types.ItemType;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.utils.DateUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Item implements Product {

    private static final Map<ItemType, ActivationCallback> activationCallbacks = new HashMap<>();

    private final int id;
    private final int inventoryId;
    private final Expiration expiration;
    private final int bonusOne;
    private final int bonusTwo;
    private short usages;
    private final Timestamp timestampExpire;
    private boolean selected;
    private final boolean visible;

    public Item() {
        this(0, 0, 0, 0, 0, (short)0, DateUtils.getTimestamp(), false, false);
    }

    public Item(int id, int inventoryId, int expiration, int bonusOne, int bonusTwo,
                short usages, Timestamp expire, boolean selected, boolean visible) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.expiration = Expiration.fromInt(expiration);
        this.bonusOne = bonusOne;
        this.bonusTwo = bonusTwo;
        this.usages = usages;
        this.timestampExpire = expire;
        this.selected = selected;
        this.visible = visible;
    }

    public void deactivateGracefully(ItemType type, Session session) {
        if (isSelected()) {
            this.selected = false;
            PlayerInfo.setInventoryItem(this, session.getPlayerId());

            ActivationCallback callback = activationCallbacks.get(type);

            if (callback != null) {
                callback.onItemActivation(this, session);
            }
        }
    }

    public void activateGracefully(ItemType type, Session session) {
        if (!isSelected()) {
            this.selected = true;
            PlayerInfo.setInventoryItem(this, session.getPlayerId());

            ActivationCallback callback = activationCallbacks.get(type);

            if (callback != null) {
                callback.onItemActivation(this, session);
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public Expiration getExpiration() {
        return expiration;
    }

    public int getBonusOne() {
        return bonusOne;
    }

    public int getBonusTwo() {
        return bonusTwo;
    }

    public short getUsages() {
        return usages;
    }

    public void sumUsages(short value) {
        usages += value;
    }

    public Timestamp getTimestampExpire() {
        return timestampExpire;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isVisible() {
        return visible;
    }

    @FunctionalInterface
    interface ActivationCallback {

        void onItemActivation(Item item, Session session);
    }

    static {
        activationCallbacks.put(ItemType.SKILL_SLOT, (Item item, Session session) -> {
            CharacterManager.sendSkillList(session);
        });
    }
}
