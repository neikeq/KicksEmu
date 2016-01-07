package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.utils.table.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BonusInfo {

    private final int type;
    private final List<Integer> bonusOne;
    private final List<Integer> bonusTwo;
    private final byte expirationType;

    public boolean isIncompatibleWithExpiration(Expiration expiration) {
        return expiration.getType() != expirationType;
    }

    public boolean isIncompatibleWithBonuses(Optional<OptionInfo> maybeOne,
                                             Optional<OptionInfo> maybeTwo) {
        int bonusOneType = maybeOne.map(OptionInfo::getType).orElse(0);
        int bonusTwoType = maybeTwo.map(OptionInfo::getType).orElse(0);

        return !bonusOne.contains(bonusOneType) || !bonusTwo.contains(bonusTwoType);
    }

    private List<Integer> listFromStringArray(String[] strings) {
        List<Integer> list = new ArrayList<>();

        for (String str : strings) {
            list.add(Integer.valueOf(str));
        }

        return list;
    }

    public BonusInfo(Row row) throws ParseRowException {
        type = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        bonusOne = listFromStringArray(row.nextColumn().map(s -> s.split(",")).orElseThrow(ParseRowException::new));
        bonusTwo = listFromStringArray(row.nextColumn().map(s -> s.split(",")).orElseThrow(ParseRowException::new));
        expirationType = Byte.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
    }

    public int getType() {
        return type;
    }
}
