package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.table.InventoryTable;
import com.neikeq.kicksemu.game.inventory.table.OptionInfo;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class CharacterUtils {

    public static void setTrainingStatsByIndex(int index, short value, int playerId) {
        switch (index) {
            case 1:
                PlayerInfo.setTrainingRunning(value, playerId);
                break;
            case 2:
                PlayerInfo.setTrainingEndurance(value, playerId);
                break;
            case 3:
                PlayerInfo.setTrainingAgility(value, playerId);
                break;
            case 4:
                PlayerInfo.setTrainingBallControl(value, playerId);
                break;
            case 5:
                PlayerInfo.setTrainingDribbling(value, playerId);
                break;
            case 6:
                PlayerInfo.setTrainingStealing(value, playerId);
                break;
            case 7:
                PlayerInfo.setTrainingTackling(value, playerId);
                break;
            case 8:
                PlayerInfo.setTrainingHeading(value, playerId);
                break;
            case 9:
                PlayerInfo.setTrainingShortShots(value, playerId);
                break;
            case 10:
                PlayerInfo.setTrainingLongShots(value, playerId);
                break;
            case 11:
                PlayerInfo.setTrainingCrossing(value, playerId);
                break;
            case 12:
                PlayerInfo.setTrainingShortPasses(value, playerId);
                break;
            case 13:
                PlayerInfo.setTrainingLongPasses(value, playerId);
                break;
            case 14:
                PlayerInfo.setTrainingMarking(value, playerId);
                break;
            case 15:
                PlayerInfo.setTrainingGoalkeeping(value, playerId);
                break;
            case 16:
                PlayerInfo.setTrainingPunching(value, playerId);
                break;
            case 17:
                PlayerInfo.setTrainingDefense(value, playerId);
                break;
        }
    }

    public static void setBonusStatsByIndex(int index, short value, int playerId) {
        switch (index) {
            case 1:
                PlayerInfo.setBonusRunning(value, playerId);
                break;
            case 2:
                PlayerInfo.setBonusEndurance(value, playerId);
                break;
            case 3:
                PlayerInfo.setBonusAgility(value, playerId);
                break;
            case 4:
                PlayerInfo.setBonusBallControl(value, playerId);
                break;
            case 5:
                PlayerInfo.setBonusDribbling(value, playerId);
                break;
            case 6:
                PlayerInfo.setBonusStealing(value, playerId);
                break;
            case 7:
                PlayerInfo.setBonusTackling(value, playerId);
                break;
            case 8:
                PlayerInfo.setBonusHeading(value, playerId);
                break;
            case 9:
                PlayerInfo.setBonusShortShots(value, playerId);
                break;
            case 10:
                PlayerInfo.setBonusLongShots(value, playerId);
                break;
            case 11:
                PlayerInfo.setBonusCrossing(value, playerId);
                break;
            case 12:
                PlayerInfo.setBonusShortPasses(value, playerId);
                break;
            case 13:
                PlayerInfo.setBonusLongPasses(value, playerId);
                break;
            case 14:
                PlayerInfo.setBonusMarking(value, playerId);
                break;
            case 15:
                PlayerInfo.setBonusGoalkeeping(value, playerId);
                break;
            case 16:
                PlayerInfo.setBonusPunching(value, playerId);
                break;
            case 17:
                PlayerInfo.setBonusDefense(value, playerId);
                break;
        }
    }

    public static void setItemInUseByType(int type, int inventoryId, int playerId) {
        switch (type) {
            case 101:
                PlayerInfo.setItemHead(inventoryId, playerId);
                break;
            case 102:
                PlayerInfo.setItemGlasses(inventoryId, playerId);
                break;
            case 103:
                PlayerInfo.setItemShirts(inventoryId, playerId);
                break;
            case 104:
                PlayerInfo.setItemPants(inventoryId, playerId);
                break;
            case 105:
                PlayerInfo.setItemGlove(inventoryId, playerId);
                break;
            case 106:
                PlayerInfo.setItemShoes(inventoryId, playerId);
                break;
            case 107:
                PlayerInfo.setItemSocks(inventoryId, playerId);
                break;
            case 111:
                PlayerInfo.setItemWrist(inventoryId, playerId);
                break;
            case 112:
                PlayerInfo.setItemArm(inventoryId, playerId);
                break;
            case 113:
                PlayerInfo.setItemKnee(inventoryId, playerId);
                break;
            case 121:
                PlayerInfo.setItemEar(inventoryId, playerId);
                break;
            case 122:
                PlayerInfo.setItemNeck(inventoryId, playerId);
                break;
            case 124:
                PlayerInfo.setItemMask(inventoryId, playerId);
                break;
            case 125:
                PlayerInfo.setItemMuffler(inventoryId, playerId);
                break;
            case 126:
                PlayerInfo.setItemPackage(inventoryId, playerId);
                break;
            default:
        }
    }

    public static int getItemInUseByType(int type, int playerId) {
        switch (type) {
            case 101:
                return PlayerInfo.getItemHead(playerId);
            case 102:
                return PlayerInfo.getItemGlasses(playerId);
            case 103:
                return PlayerInfo.getItemShirts(playerId);
            case 104:
                return PlayerInfo.getItemPants(playerId);
            case 105:
                return PlayerInfo.getItemGlove(playerId);
            case 106:
                return PlayerInfo.getItemShoes(playerId);
            case 107:
                return PlayerInfo.getItemSocks(playerId);
            case 111:
                return PlayerInfo.getItemWrist(playerId);
            case 112:
                return PlayerInfo.getItemArm(playerId);
            case 113:
                return PlayerInfo.getItemKnee(playerId);
            case 121:
                return PlayerInfo.getItemEar(playerId);
            case 122:
                return PlayerInfo.getItemNeck(playerId);
            case 124:
                return PlayerInfo.getItemMask(playerId);
            case 125:
                return PlayerInfo.getItemMuffler(playerId);
            case 126:
                return PlayerInfo.getItemPackage(playerId);
            default:
                return -1;
        }
    }

    public static void updateItemsInUse(int inventoryId, Map<Integer, Item> items, int playerId) {
        OptionInfo optionInfo = InventoryTable.getOptionInfo(o ->
                o.getId() == items.get(inventoryId).getId());

        Item itemOut = items.get(getItemInUseByType(optionInfo.getType(), playerId));

        if (itemOut != null) {
            itemOut.deactivateGracefully(playerId);
        }

        Item itemIn = items.get(inventoryId);

        if (itemIn != null) {
            itemIn.activateGracefully(playerId);
        }
    }

    public static boolean characterExist(int characterId) {
        String query = "SELECT id FROM characters WHERE id = ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, characterId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            return false;
        }
    }

    public static int getCharacterIdByName(String name) {
        String query = "SELECT id FROM characters WHERE name = ?";

        try (Connection con = MySqlManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1;
                }
            }

        } catch (SQLException e) {
            return -1;
        }
    }

    public static short statsUpToHundred(short stats, int add) {
        if (add < 0) return (short)add;

        short i;
        for (i = 0; i < add; i++) {
            if (stats < 100) {
                stats++;
            } else {
                break;
            }
        }

        return i;
    }
}

