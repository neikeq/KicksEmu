package com.neikeq.kicksemu.game.characters.creation;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharacterCreator {

    public static void createCharacter(Session session, ClientMessage msg) {
        byte result;

        int accountId = msg.readInt();

        if (session.getUserInfo().getId() == accountId) {
            if (session.getUserInfo().hasEmptySlot()) {
                CharacterBase character = characterFromMessage(msg, accountId);

                result = CharacterValidator.validation(character);

                if (result == CreationResult.SUCCESS) {
                    int resultId = create(character);

                    if (resultId <= 0 || !setCharacterOwner(resultId, session.getUserInfo())) {
                        result = CreationResult.SYSTEM_PROBLEM;
                    }
                }
            } else {
                result = CreationResult.CHARACTERS_LIMIT;
            }
        } else {
            result = CreationResult.SYSTEM_PROBLEM;
        }

        ServerMessage response = MessageBuilder.createCharacter(result);
        session.send(response);
    }

    private static int create(CharacterBase character) {
        int characterId = -1;

        try (Connection con = MySqlManager.getConnection()) {
            String creation_query = "INSERT INTO characters (owner, name, position," +
                    " animation, face, default_head, default_shirts, default_pants," +
                    " default_shoes, stats_points, stats_running, stats_endurance," +
                    " stats_agility, stats_ball_control, stats_dribbling, stats_stealing," +
                    " stats_tackling, stats_heading, stats_short_shots, stats_long_shots," +
                    " stats_crossing, stats_short_passes, stats_long_passes, stats_marking," +
                    " stats_goalkeeping, stats_punching, stats_defense, inventory_items," +
                    " inventory_training, inventory_skills, inventory_celebration, friends)" +
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

            try (PreparedStatement stmt = con.prepareStatement(creation_query)) {
                stmt.setInt(1, character.getOwner());
                stmt.setString(2, character.getName());
                stmt.setShort(3, character.getPosition());
                stmt.setShort(4, character.getAnimation());
                stmt.setShort(5, character.getFace());
                stmt.setInt(6, character.getDefaultHead());
                stmt.setInt(7, character.getDefaultShirts());
                stmt.setInt(8, character.getDefaultPants());
                stmt.setInt(9, character.getDefaultShoes());
                stmt.setShort(10, character.getStatsPoints());
                stmt.setShort(11, character.getStatsRunning());
                stmt.setShort(12, character.getStatsEndurance());
                stmt.setShort(13, character.getStatsAgility());
                stmt.setShort(14, character.getStatsBallControl());
                stmt.setShort(15, character.getStatsDribbling());
                stmt.setShort(16, character.getStatsStealing());
                stmt.setShort(17, character.getStatsTackling());
                stmt.setShort(18, character.getStatsHeading());
                stmt.setShort(19, character.getStatsShortShots());
                stmt.setShort(20, character.getStatsLongShots());
                stmt.setShort(21, character.getStatsCrossing());
                stmt.setShort(22, character.getStatsShortPasses());
                stmt.setShort(23, character.getStatsLongPasses());
                stmt.setShort(24, character.getStatsMarking());
                stmt.setShort(25, character.getStatsGoalkeeping());
                stmt.setShort(26, character.getStatsPunching());
                stmt.setShort(27, character.getStatsDefense());
                stmt.setString(28, "");
                stmt.setString(29, "");
                stmt.setString(30, "");
                stmt.setString(31, "");
                stmt.setString(32, "");

                stmt.executeUpdate();
            }

            String owner_query = "SELECT id FROM characters WHERE id = LAST_INSERT_ID();";

            try (PreparedStatement stmt2 = con.prepareStatement(owner_query)) {
                try (ResultSet rs  = stmt2.executeQuery()) {
                    if (rs.next()) {
                        characterId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            characterId = -1;
        }

        return characterId;
    }

    private static boolean setCharacterOwner(int characterId, UserInfo owner) {
        switch (owner.getFirstEmptySlot()) {
            case 0:
                return owner.setSlotOne(characterId);
            case 1:
                return owner.setSlotTwo(characterId);
            case 2:
                return owner.setSlotThree(characterId);
            default:
                // Should not happen. We checked the owner has at least one empty slot.
                Output.println("Problem to add character: " + characterId +
                        "to owner:" + owner.getId(), Level.WARNING);
                return false;
        }
    }

    private static CharacterBase characterFromMessage(ClientMessage msg, int accountId) {
        CharacterBase character = new CharacterBase();

        character.setOwner(accountId);
        character.setName(msg.readString(15));
        character.setStatsPoints(msg.readShort());
        msg.ignoreBytes(2);
        character.setAnimation(msg.readShort());
        character.setFace(msg.readShort());
        character.setDefaultHead(msg.readInt());
        character.setDefaultShirts(msg.readInt());
        character.setDefaultPants(msg.readInt());
        character.setDefaultShoes(msg.readInt());
        character.setPosition(msg.readShort());
        msg.ignoreBytes(6);
        character.setStatsRunning(msg.readShort());
        character.setStatsEndurance(msg.readShort());
        character.setStatsAgility(msg.readShort());
        character.setStatsBallControl(msg.readShort());
        character.setStatsDribbling(msg.readShort());
        character.setStatsStealing(msg.readShort());
        character.setStatsTackling(msg.readShort());
        character.setStatsHeading(msg.readShort());
        character.setStatsShortShots(msg.readShort());
        character.setStatsLongShots(msg.readShort());
        character.setStatsCrossing(msg.readShort());
        character.setStatsShortPasses(msg.readShort());
        character.setStatsLongPasses(msg.readShort());
        character.setStatsMarking(msg.readShort());
        character.setStatsGoalkeeping(msg.readShort());
        character.setStatsPunching(msg.readShort());
        character.setStatsDefense(msg.readShort());

        return character;
    }
}
