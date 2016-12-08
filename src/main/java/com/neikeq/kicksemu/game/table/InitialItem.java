package com.neikeq.kicksemu.game.table;

import com.neikeq.kicksemu.game.characters.types.Position;
import com.neikeq.kicksemu.game.inventory.types.Expiration;
import com.neikeq.kicksemu.utils.table.Row;

import java.util.stream.IntStream;

public class InitialItem {

    private final short id;
    private final int itemId;
    private final Expiration expiration;
    private final int bonusOne;
    private final int bonusTwo;
    private final short position;

    public InitialItem(Row row) throws ParseRowException {
        row.ignoreColumn();
        id = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        itemId = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        try {
            expiration = Expiration.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        } catch (RuntimeException e) {
            throw new ParseRowException(e.getMessage());
        }
        bonusOne = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        bonusTwo = Integer.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));
        position = Short.valueOf(row.nextColumn().orElseThrow(ParseRowException::new));

        final int[] validPositions = {0, Position.FW, Position.MF, Position.DF};

        if (!IntStream.of(validPositions).anyMatch(pos -> pos == getPosition())) {
            throw new ParseRowException("Invalid position.");
        }
    }

    public short getId() {
        return id;
    }

    public int getItemId() {
        return itemId;
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

    public int getPosition() {
        return position;
    }
}
