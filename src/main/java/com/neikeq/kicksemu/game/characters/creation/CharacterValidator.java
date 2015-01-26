package com.neikeq.kicksemu.game.characters.creation;

import com.neikeq.kicksemu.game.characters.PositionCodes;
import com.neikeq.kicksemu.game.inventory.ItemType;
import com.neikeq.kicksemu.game.inventory.table.InventoryTable;
import com.neikeq.kicksemu.game.inventory.table.ItemFree;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

class CharacterValidator {

    private static final String validSpecialChars = "()[]-_.,:;";

    private static final List<Short> VALID_POSITIONS =
            Arrays.asList(PositionCodes.FW, PositionCodes.MF, PositionCodes.DF);

    private static final List<Integer> VALID_FACES = Arrays.asList(
                    101, 102, 103, 104, 105, 106, 201, 202, 203, 301, 302,
                    303, 304, 401, 402, 403, 404, 405, 406, 407, 408, 409
            );

    private static final int NAME_MIN_LENGTH = 3;
    private static final int NAME_MAX_LENGTH = 14;

    private static final int STATS_GLOBAL = 490;
    private static final int MIN_STATS_POINTS = 0;
    private static final int MAX_STATS_POINTS = 10;

    // FW, MF, DF minimum values
    private static final byte[] MIN_RUNNING = new byte[] { 40, 40, 40 };
    private static final byte[] MIN_ENDURANCE = new byte[] { 30, 35, 30 };
    private static final byte[] MIN_AGILITY = new byte[] { 30, 30, 30 };
    private static final byte[] MIN_BALL_CONTROL= new byte[] { 30, 25, 25 };
    private static final byte[] MIN_DRIBBLING = new byte[] { 30, 30, 25 };
    private static final byte[] MIN_STEALING = new byte[] { 20, 25, 30 };
    private static final byte[] MIN_TACKLING = new byte[] { 15, 20, 30 };
    private static final byte[] MIN_HEADING = new byte[] { 35, 20, 35 };
    private static final byte[] MIN_SHORT_SHOTS = new byte[] { 30, 15, 15 };
    private static final byte[] MIN_LONG_SHOTS = new byte[] { 20, 30, 15 };
    private static final byte[] MIN_CROSSING = new byte[] { 55, 60, 50 };
    private static final byte[] MIN_SHORT_PASSES = new byte[] { 60, 65, 60 };
    private static final byte[] MIN_LONG_PASSES = new byte[] { 55, 55, 65 };
    private static final byte[] MIN_MARKING = new byte[] { 30, 30, 30 };
    private static final byte[] MIN_GOALKEEPING = new byte[] { 0, 0, 0 };
    private static final byte[] MIN_PUNCHING = new byte[] { 0, 0, 0 };
    private static final byte[] MIN_DEFENSE = new byte[] { 0, 0, 0 };

    public static byte validate(CharacterBase character) {
        byte result = CreationResult.SUCCESS;

        if (!isValidName(character.getName()) || nameAlreadyInUse(character.getName())) {
            result = CreationResult.NAME_ALREADY_IN_USE;
        } else if (!containsValidStats(character) || !isValidPosition(character.getPosition())) {
            result = CreationResult.INVALID_CHARACTER;
        } else if (!containsValidItems(character) || !isValidFace(character) ||
                !isValidAnimation(character)) {
            result = CreationResult.SYSTEM_PROBLEM;
        }

        return result;
    }

    private static boolean isValidPosition(short position) {
        return VALID_POSITIONS.contains(position);
    }

    private static boolean isValidName(String name) {
        return (name.length() <= NAME_MAX_LENGTH && name.length() >= NAME_MIN_LENGTH) ||
                !nameContainsInvalidChar(name);
    }

    private static boolean nameContainsInvalidChar(String name) {
        for (char current : name.toLowerCase().toCharArray()) {
            if (!Character.isAlphabetic(current) && !Character.isDigit(current) &&
                    !validSpecialChars.contains(String.valueOf(current))) {
                return true;
            }
        }

        return false;
    }

    private static boolean nameAlreadyInUse(String name) {
        String query = "SELECT count(*) FROM characters WHERE name = ?";

        try (Connection connection = MySqlManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);

            try (ResultSet result = stmt.executeQuery()) {
                return result.next() && result.getInt(1) > 0;
            }

        } catch (SQLException e) {
            return true;
        }
    }

    private static boolean containsValidStats(CharacterBase character) {
        int index = character.getPosition() / 10 - 1;

        return character.getTotalStats() == STATS_GLOBAL &&
                character.getStatsPoints() <= MAX_STATS_POINTS &&
                character.getStatsPoints() >= MIN_STATS_POINTS &&
                character.getStatsRunning() >= MIN_RUNNING[index] &&
                character.getStatsEndurance() >= MIN_ENDURANCE[index] &&
                character.getStatsAgility() >= MIN_AGILITY[index] &&
                character.getStatsBallControl() >= MIN_BALL_CONTROL[index] &&
                character.getStatsDribbling() >= MIN_DRIBBLING[index] &&
                character.getStatsStealing() >= MIN_STEALING[index] &&
                character.getStatsTackling() >= MIN_TACKLING[index] &&
                character.getStatsHeading() >= MIN_HEADING[index] &&
                character.getStatsShortShots() >= MIN_SHORT_SHOTS[index] &&
                character.getStatsLongShots() >= MIN_LONG_SHOTS[index] &&
                character.getStatsCrossing() >= MIN_CROSSING[index] &&
                character.getStatsShortPasses() >= MIN_SHORT_PASSES[index] &&
                character.getStatsLongPasses() >= MIN_LONG_PASSES[index] &&
                character.getStatsMarking() >= MIN_MARKING[index] &&
                character.getStatsGoalkeeping() >= MIN_GOALKEEPING[index] &&
                character.getStatsPunching() >= MIN_PUNCHING[index] &&
                character.getStatsDefense() >= MIN_DEFENSE[index];
    }

    private static boolean containsValidItems(CharacterBase character) {
        ItemFree head = InventoryTable.getItemFree(itemFree ->
                itemFree.getId() == character.getDefaultHead() &&
                        itemFree.getType() == ItemType.HEAD.toInt());
        ItemFree shirt = InventoryTable.getItemFree(itemFree ->
                itemFree.getId() == character.getDefaultShirts() &&
                        itemFree.getType() == ItemType.SHIRTS.toInt());
        ItemFree pant = InventoryTable.getItemFree(itemFree ->
                itemFree.getId() == character.getDefaultPants() &&
                        itemFree.getType() == ItemType.PANTS.toInt());
        ItemFree shoes = InventoryTable.getItemFree(itemFree ->
                itemFree.getId() == character.getDefaultShoes() &&
                        itemFree.getType() == ItemType.SHOES.toInt());

        return head != null && shirt != null && pant != null && shoes != null;
    }

    private static boolean isValidFace(CharacterBase character) {
        return VALID_FACES.contains(Integer.valueOf(character.getFace()));
    }

    // TODO create animation and faces enumerator or constants
    private static boolean isValidAnimation(CharacterBase character) {
        return (character.getAnimation() == 1 && character.getFace() < 400) ||
                (character.getAnimation() == 2 && character.getFace() > 400);
    }
}
