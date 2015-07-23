package com.neikeq.kicksemu.game.misc.quests;

import com.neikeq.kicksemu.game.rooms.match.MatchResult;
import com.neikeq.kicksemu.game.rooms.match.TeamResult;

import java.sql.Connection;

interface Quest {

    void check(int playerId, QuestState questState, MatchResult matchResult,
                     TeamResult teamResult, Connection... con);
}
