package com.neikeq.kicksemu.game.misc.quests;

import com.neikeq.kicksemu.game.rooms.match.MatchResult;
import com.neikeq.kicksemu.game.rooms.match.TeamResult;
import com.neikeq.kicksemu.storage.ConnectionRef;

interface Quest {

    short check(int playerId, QuestState questState, MatchResult matchResult,
                     TeamResult teamResult, ConnectionRef... con);
}
