package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;

import java.util.Map;

public class InventoryManager {

    public static void activateSkill(Session session, ClientMessage msg) {
        int playerId = session.getPlayerId();
        int skillId = msg.readInt();

        byte result = 0;
        byte newIndex = 0;

        Map<Integer, Skill> skills = PlayerInfo.getInventorySkills(playerId);
        Skill skill = (Skill) InventoryUtils.getByIdFromMap(skills, skillId);

        // If skill exists and skill is not yet activated
        if (skill != null && skill.getSelectionIndex() <= 0) {
            // Activate skill
            newIndex = InventoryUtils.getSmallestMissingIndex(skills.values());
            skill.setSelectionIndex(newIndex);

            PlayerInfo.setInventorySkills(skills, playerId);
        } else {
            result = -2; // Skill does not exists
        }

        session.send(MessageBuilder.activateSkill(skillId, newIndex, result));
    }

    public static void deactivateSkill(Session session, ClientMessage msg) {
        int skillId = msg.readInt();

        byte result = deactivateSkill(session, skillId,
                PlayerInfo.getInventorySkills(session.getPlayerId()));

        if (result != 0) {
            session.send(MessageBuilder.deactivateSkill(skillId, result));
        }
    }

    public static byte deactivateSkill(Session s, int skillId, Map<Integer, Skill> skills) {
        byte result = 0;

        int playerId = s.getPlayerId();
        Skill skill = (Skill) InventoryUtils.getByIdFromMap(skills, skillId);

        // If skill exists and skill is activated
        if (skill != null && skill.getSelectionIndex() > 0) {
            // Deactivate skill
            skill.setSelectionIndex((byte) 0);
            s.send(MessageBuilder.deactivateSkill(skillId, result));

            PlayerInfo.setInventorySkills(skills, playerId);
        } else {
            result = -2; // Skill does not exists
        }

        return result;
    }

    public static void activateCele(Session session, ClientMessage msg) {
        int playerId = session.getPlayerId();
        int celeId = msg.readInt();

        byte result = 0;
        byte newIndex = 0;

        Map<Integer, Celebration> celes = PlayerInfo.getInventoryCelebration(playerId);
        Celebration cele = (Celebration) InventoryUtils.getByIdFromMap(celes, celeId);

        // If cele exists and cele is not yet activated
        if (cele != null && cele.getSelectionIndex() <= 0) {
            // Activate skill
            newIndex = InventoryUtils.getSmallestMissingIndex(celes.values());

            if (newIndex <= 5) {
                cele.setSelectionIndex(newIndex);

                PlayerInfo.setInventoryCelebration(celes, playerId);
            } else {
                result = -3;
            }
        } else {
            result = -2; // Cele does not exists
        }

        session.send(MessageBuilder.activateCele(celeId, newIndex, result));
    }

    public static void deactivateCele(Session session, ClientMessage msg) {
        int celeId = msg.readInt();

        byte result = deactivateCele(session, celeId,
                PlayerInfo.getInventoryCelebration(session.getPlayerId()));

        if (result != 0) {
            session.send(MessageBuilder.deactivateCele(celeId, result));
        }
    }

    public static byte deactivateCele(Session s, int celeId, Map<Integer, Celebration> celes) {
        byte result = 0;

        int playerId = s.getPlayerId();
        Celebration cele = (Celebration) InventoryUtils.getByIdFromMap(celes, celeId);

        // If cele exists and cele is activated
        if (cele != null && cele.getSelectionIndex() > 0) {
            // Deactivate cele
            cele.setSelectionIndex((byte) 0);
            s.send(MessageBuilder.deactivateCele(celeId, result));

            PlayerInfo.setInventoryCelebration(celes, playerId);
        } else {
            result = -2; // Cele does not exists
        }

        return result;
    }
}
