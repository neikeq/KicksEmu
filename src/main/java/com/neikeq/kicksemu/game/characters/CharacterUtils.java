package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.ItemType;
import com.neikeq.kicksemu.game.inventory.table.InventoryTable;
import com.neikeq.kicksemu.game.inventory.table.ItemInfo;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public static Item getItemInUseByType(ItemType type, int playerId) {
        switch (type) {
            case HEAD:
                return PlayerInfo.getItemHead(playerId);
            case GLASSES:
                return PlayerInfo.getItemGlasses(playerId);
            case SHIRTS:
                return PlayerInfo.getItemShirts(playerId);
            case PANTS:
                return PlayerInfo.getItemPants(playerId);
            case GLOVES:
                return PlayerInfo.getItemGlove(playerId);
            case SHOES:
                return PlayerInfo.getItemShoes(playerId);
            case SOCKS:
                return PlayerInfo.getItemSocks(playerId);
            case WRIST:
                return PlayerInfo.getItemWrist(playerId);
            case ARM:
                return PlayerInfo.getItemArm(playerId);
            case KNEE:
                return PlayerInfo.getItemKnee(playerId);
            case EAR:
                return PlayerInfo.getItemEar(playerId);
            case NECK:
                return PlayerInfo.getItemNeck(playerId);
            case MASK:
                return PlayerInfo.getItemMask(playerId);
            case MUFFLER:
                return PlayerInfo.getItemMuffler(playerId);
            case PACKAGE:
                return PlayerInfo.getItemPackage(playerId);
            default:
                return null;
        }
    }

    public static void updateItemsInUse(Item itemIn, int playerId) {
        ItemInfo itemInfo = InventoryTable.getItemInfo(o ->
                o.getId() == itemIn.getId());

        Item itemOut = getItemInUseByType(ItemType.fromInt(itemInfo.getType()), playerId);

        if (itemOut != null) {
            itemOut.deactivateGracefully(playerId);
        }

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

