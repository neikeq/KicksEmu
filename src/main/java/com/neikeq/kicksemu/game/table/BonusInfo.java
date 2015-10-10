package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.utils.table.Row;

import java.util.ArrayList;
import java.util.List;

public class BonusInfo {

    private final int type;
    private final List<Integer> bonusOne;
    private final List<Integer> bonusTwo;
    private final byte expirationType;

    public boolean isIncompatibleWithExpiration(Expiration expiration) {
        return expiration.getType() != getExpirationType();
    }

    public boolean isIncompatibleWithBonuses(OptionInfo one, OptionInfo two) {
        int bonusOneType = one == null ? 0 : one.getType();
        int bonusTwoType = two == null ? 0 : two.getType();

        return !getBonusOne().contains(bonusOneType) ||
                !getBonusTwo().contains(bonusTwoType);
    }

    private List<Integer> listFromStringArray(String[] strings) {
        List<Integer> list = new ArrayList<>();

        for (String str : strings) {
            list.add(Integer.valueOf(str));
        }

        return list;
    }

    public BonusInfo(Row row) {
        type = Integer.valueOf(row.nextColumn());
        bonusOne = listFromStringArray(row.nextColumn().split(","));
        bonusTwo = listFromStringArray(row.nextColumn().split(","));
        expirationType = Byte.valueOf(row.nextColumn());
    }

    public int getType() {
        return type;
    }

    public List<Integer> getBonusOne() {
        return bonusOne;
    }

    public List<Integer> getBonusTwo() {
        return bonusTwo;
    }

    public byte getExpirationType() {
        return expirationType;
    }
}
