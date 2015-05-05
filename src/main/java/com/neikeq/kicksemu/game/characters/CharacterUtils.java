package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.characters.types.PlayerStats;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.ItemType;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.game.table.ItemInfo;
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

    private static Item getItemInUseByType(ItemType type, int playerId) {
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
            case SODA:
                return PlayerInfo.getItemInUseByType(ItemType.SODA, playerId);
            case SKILL_SLOT:
                return PlayerInfo.getItemInUseByType(ItemType.SKILL_SLOT, playerId);
            case CHARACTER_SLOT:
                return PlayerInfo.getItemInUseByType(ItemType.CHARACTER_SLOT, playerId);
            case STATS_RESET:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_RESET, playerId);
            case CASH_TICKET:
                return PlayerInfo.getItemInUseByType(ItemType.CASH_TICKET, playerId);
            case POINTS_TICKET:
                return PlayerInfo.getItemInUseByType(ItemType.POINTS_TICKET, playerId);
            case CLUB_SPONSORSHIP:
                return PlayerInfo.getItemInUseByType(ItemType.CLUB_SPONSORSHIP, playerId);
            case RANDOM_BOX:
                return PlayerInfo.getItemInUseByType(ItemType.RANDOM_BOX, playerId);
            case STATS_211:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_211, playerId);
            case STATS_212:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_212, playerId);
            case STATS_213:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_213, playerId);
            case STATS_214:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_214, playerId);
            case STATS_215:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_215, playerId);
            case STATS_216:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_216, playerId);
            case STATS_217:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_217, playerId);
            case STATS_218:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_218, playerId);
            case STATS_219:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_219, playerId);
            case STATS_220:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_220, playerId);
            case STATS_221:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_221, playerId);
            case STATS_222:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_222, playerId);
            case STATS_223:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_223, playerId);
            case CLUB_UNIFORM:
                return PlayerInfo.getItemInUseByType(ItemType.CLUB_UNIFORM, playerId);
            case EMBLEM:
                return PlayerInfo.getItemInUseByType(ItemType.EMBLEM, playerId);
            case BACK_NUMBER:
                return PlayerInfo.getItemInUseByType(ItemType.BACK_NUMBER, playerId);
            case CLUB_RENAME:
                return PlayerInfo.getItemInUseByType(ItemType.CLUB_RENAME, playerId);
            case MEMBERS_SLOTS:
                return PlayerInfo.getItemInUseByType(ItemType.MEMBERS_SLOTS, playerId);
            case CLUB_BROADCAST:
                return PlayerInfo.getItemInUseByType(ItemType.CLUB_BROADCAST, playerId);
            case BACK_NUMBER_COLOR:
                return PlayerInfo.getItemInUseByType(ItemType.BACK_NUMBER_COLOR, playerId);
            default:
                return null;
        }
    }

    public static void updateItemsInUse(Item itemIn, int playerId) {
        ItemInfo itemInfo = TableManager.getItemInfo(o ->
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
        final String query = "SELECT id FROM characters WHERE id = ?";

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
        final String query = "SELECT id FROM characters WHERE name = ?";

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

