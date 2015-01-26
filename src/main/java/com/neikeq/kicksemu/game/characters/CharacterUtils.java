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

    public static void sumStatsByIndex(int index, short value, PlayerStats playerStats) {
        switch (index) {
            case 1:
                playerStats.sumRunning(value);
                break;
            case 2:
                playerStats.sumEndurance(value);
                break;
            case 3:
                playerStats.sumAgility(value);
                break;
            case 4:
                playerStats.sumBallControl(value);
                break;
            case 5:
                playerStats.sumDribbling(value);
                break;
            case 6:
                playerStats.sumStealing(value);
                break;
            case 7:
                playerStats.sumTackling(value);
                break;
            case 8:
                playerStats.sumHeading(value);
                break;
            case 9:
                playerStats.sumShortShots(value);
                break;
            case 10:
                playerStats.sumLongShots(value);
                break;
            case 11:
                playerStats.sumCrossing(value);
                break;
            case 12:
                playerStats.sumShortPasses(value);
                break;
            case 13:
                playerStats.sumLongPasses(value);
                break;
            case 14:
                playerStats.sumMarking(value);
                break;
            case 15:
                playerStats.sumGoalkeeping(value);
                break;
            case 16:
                playerStats.sumPunching(value);
                break;
            case 17:
                playerStats.sumDefense(value);
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

