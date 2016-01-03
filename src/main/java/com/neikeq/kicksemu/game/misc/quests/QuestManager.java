package com.neikeq.kicksemu.game.misc.quests;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.rooms.enums.VictoryResult;
import com.neikeq.kicksemu.game.rooms.match.MatchResult;
import com.neikeq.kicksemu.game.rooms.match.TeamResult;

import java.sql.Connection;

public class QuestManager {

    private static final Quest[] quests = {
            (playerId, questState, matchResult, teamResult, con) -> {
                short finishedQuest = -1;

                if (teamResult.getResult() != VictoryResult.NO_GAME) {
                    questState.decreaseRemainMatches();

                    if (questState.getRemainMatches() <= 0) {
                        finishedQuest = questState.getCurrentQuest();

                        PlayerInfo.sumPoints(1000, playerId, con);
                        questState.nextQuest((short) 5);
                    }

                    PlayerInfo.setQuestState(questState, playerId, con);
                }

                return finishedQuest;
            },
            (playerId, questState, matchResult, teamResult, con) -> {
                short finishedQuest = -1;

                if (teamResult.getResult() == VictoryResult.WIN) {
                    questState.decreaseRemainMatches();

                    if (questState.getRemainMatches() <= 0) {
                        finishedQuest = questState.getCurrentQuest();

                        PlayerInfo.sumPoints(2000, playerId, con);
                        questState.nextQuest((short) 1);
                    }

                    PlayerInfo.setQuestState(questState, playerId, con);
                }

                return finishedQuest;
            },
            (playerId, questState, matchResult, teamResult, con) -> {
                short finishedQuest = -1;

                if ((teamResult.getResult() != VictoryResult.NO_GAME) &&
                        (matchResult.getMom() == playerId)) {
                    questState.decreaseRemainMatches();

                    if (questState.getRemainMatches() <= 0) {
                        finishedQuest = questState.getCurrentQuest();

                        PlayerInfo.sumPoints(3000, playerId, con);
                        questState.nextQuest((short) 0);
                    }

                    PlayerInfo.setQuestState(questState, playerId, con);
                }

                return finishedQuest;
            }
    };

    public static short checkQuests(int playerId, MatchResult matchResult,
                                   RoomTeam team, Connection ... con) {
        QuestState questState = PlayerInfo.getQuestState(playerId, con);

        // If the player still has quests to complete
        if (questState.getCurrentQuest() < 4) {
            Quest currentQuest = quests[questState.getCurrentQuest() - 1];

            if (currentQuest != null) {
                TeamResult teamResult = (team == RoomTeam.RED) ?
                        matchResult.getRedTeam() : matchResult.getBlueTeam();
                return currentQuest.check(playerId, questState, matchResult, teamResult);
            }
        }

        return -1;
    }
}
