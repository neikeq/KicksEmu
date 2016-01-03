package com.neikeq.kicksemu.game.characters;

import com.neikeq.kicksemu.game.characters.types.PlayerStats;
import com.neikeq.kicksemu.game.characters.types.Position;
import com.neikeq.kicksemu.game.inventory.products.Item;
import com.neikeq.kicksemu.game.inventory.types.ItemType;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.game.table.ItemInfo;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharacterUtils {

    private static final List<ItemType> DEACTIVATION_EXCEPTIONS = new ArrayList<>(Arrays.asList(
            ItemType.SKILL_SLOT, ItemType.CHARACTER_SLOT
    ));

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

    private static Item getItemInUseByType(ItemType type, Session session) {
        switch (type) {
            case HEAD:
                return PlayerInfo.getItemHead(session);
            case GLASSES:
                return PlayerInfo.getItemGlasses(session);
            case SHIRTS:
                return PlayerInfo.getItemShirts(session);
            case PANTS:
                return PlayerInfo.getItemPants(session);
            case GLOVES:
                return PlayerInfo.getItemGlove(session);
            case SHOES:
                return PlayerInfo.getItemShoes(session);
            case SOCKS:
                return PlayerInfo.getItemSocks(session);
            case WRIST:
                return PlayerInfo.getItemWrist(session);
            case ARM:
                return PlayerInfo.getItemArm(session);
            case KNEE:
                return PlayerInfo.getItemKnee(session);
            case EAR:
                return PlayerInfo.getItemEar(session);
            case NECK:
                return PlayerInfo.getItemNeck(session);
            case MASK:
                return PlayerInfo.getItemMask(session);
            case MUFFLER:
                return PlayerInfo.getItemMuffler(session);
            case PACKAGE:
                return PlayerInfo.getItemPackage(session);
            case SODA:
                return PlayerInfo.getItemInUseByType(ItemType.SODA, session);
            case SKILL_SLOT:
                return PlayerInfo.getItemInUseByType(ItemType.SKILL_SLOT, session);
            case CHARACTER_SLOT:
                return PlayerInfo.getItemInUseByType(ItemType.CHARACTER_SLOT, session);
            case STATS_RESET:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_RESET, session);
            case CASH_TICKET:
                return PlayerInfo.getItemInUseByType(ItemType.CASH_TICKET, session);
            case POINTS_TICKET:
                return PlayerInfo.getItemInUseByType(ItemType.POINTS_TICKET, session);
            case CLUB_SPONSORSHIP:
                return PlayerInfo.getItemInUseByType(ItemType.CLUB_SPONSORSHIP, session);
            case RANDOM_BOX:
                return PlayerInfo.getItemInUseByType(ItemType.RANDOM_BOX, session);
            case STATS_211:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_211, session);
            case STATS_212:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_212, session);
            case STATS_213:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_213, session);
            case STATS_214:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_214, session);
            case STATS_215:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_215, session);
            case STATS_216:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_216, session);
            case STATS_217:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_217, session);
            case STATS_218:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_218, session);
            case STATS_219:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_219, session);
            case STATS_220:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_220, session);
            case STATS_221:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_221, session);
            case STATS_222:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_222, session);
            case STATS_223:
                return PlayerInfo.getItemInUseByType(ItemType.STATS_223, session);
            case CLUB_UNIFORM:
                return PlayerInfo.getItemInUseByType(ItemType.CLUB_UNIFORM, session);
            case EMBLEM:
                return PlayerInfo.getItemInUseByType(ItemType.EMBLEM, session);
            case BACK_NUMBER:
                return PlayerInfo.getItemInUseByType(ItemType.BACK_NUMBER, session);
            case CLUB_RENAME:
                return PlayerInfo.getItemInUseByType(ItemType.CLUB_RENAME, session);
            case MEMBERS_SLOTS:
                return PlayerInfo.getItemInUseByType(ItemType.MEMBERS_SLOTS, session);
            case CLUB_BROADCAST:
                return PlayerInfo.getItemInUseByType(ItemType.CLUB_BROADCAST, session);
            case BACK_NUMBER_COLOR:
                return PlayerInfo.getItemInUseByType(ItemType.BACK_NUMBER_COLOR, session);
            default:
                return null;
        }
    }

    public static void updateItemsInUse(Item itemIn, Session session) {
        ItemInfo itemInfo = TableManager.getItemInfo(o ->
                o.getId() == itemIn.getId());

        ItemType itemType = ItemType.fromInt(itemInfo.getType());

        if (!DEACTIVATION_EXCEPTIONS.contains(itemType)) {
            Item itemOut = getItemInUseByType(itemType, session);

            if (itemOut != null) {
                itemOut.deactivateGracefully(itemType, session);
            }
        }

        if (itemIn != null) {
            itemIn.activateGracefully(itemType, session);
        }
    }

    public static boolean shouldUpdatePosition(int characterId) {
        return (PlayerInfo.getLevel(characterId) >= 18) &&
                !Position.isAdvancedPosition(PlayerInfo.getPosition(characterId));
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
                return rs.next() ? rs.getInt("id") : -1;
            }

        } catch (SQLException e) {
            return -1;
        }
    }

    public static short statsUpToHundred(short stats, int add) {
        if (add < 0) return (short) add;

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

