package com.neikeq.kicksemu.game.inventory;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.inventory.table.InventoryTable;
import com.neikeq.kicksemu.game.inventory.table.SkillInfo;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;

import java.util.Map;
import java.util.Optional;

public class InventoryManager {

    public static void activateSkill(Session session, ClientMessage msg) {
        int playerId = session.getPlayerId();
        int skillId = msg.readInt();

        byte result = 0;
        byte newIndex = 0;

        Map<Integer, Skill> skills = PlayerInfo.getInventorySkills(playerId);
        Skill skill = InventoryUtils.getByIdFromMap(skills, skillId);

        // If skill exists and skill is not yet activated
        if (skill != null && skill.getSelectionIndex() <= 0) {
            // Deactivate skills with the same group
            int groupSkill;
            while ((groupSkill = checkActivatedSkills(skills, skillId)) != -1) {
                deactivateSkill(session, groupSkill, skills);
            }

            // Activate skill
            newIndex = InventoryUtils.getSmallestMissingIndex(skills.values());
            skill.setSelectionIndex(newIndex);

            PlayerInfo.setInventorySkills(skills, playerId);
        } else {
            result = -2; // Skill does not exists
        }

        session.send(MessageBuilder.activateSkill(skillId, newIndex, result));
    }

    public static int checkActivatedSkills(Map<Integer, Skill> skills, int skillId) {
        short skillGroup = InventoryTable.getSkillInfo(si -> skillId == si.getId()).getGroup();

        Optional<Skill> result = skills.values().stream().filter(s -> {
            if (s.getSelectionIndex() > 0) {
                SkillInfo skillInfo = InventoryTable.getSkillInfo(si -> s.getId() == si.getId());
                return skillInfo.getGroup() / 100 == skillGroup / 100 && skillInfo.getId() != skillId;
            } else {
                return false;
            }
        }).findFirst();

        return result.isPresent() ? result.get().getId() : -1;
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
        Skill skill = InventoryUtils.getByIdFromMap(skills, skillId);

        // If skill exists and skill is activated
        if (skill != null && skill.getSelectionIndex() > 0) {
            // Deactivate skill
            skill.setSelectionIndex((byte)0);
            s.send(MessageBuilder.deactivateSkill(skillId, result));

            PlayerInfo.setInventorySkills(skills, playerId);
        } else {
            result = -2; // Skill does not exists
        }

        return result;
    }
}
