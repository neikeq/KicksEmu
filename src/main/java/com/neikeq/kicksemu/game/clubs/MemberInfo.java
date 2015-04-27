package com.neikeq.kicksemu.game.clubs;

import com.neikeq.kicksemu.storage.SqlUtils;

import java.sql.Connection;

public class MemberInfo {

    private static final String TABLE = "club_members";

    public static MemberRole getRole(int id, Connection... con) {
        String role = SqlUtils.getString("role", TABLE, id, con);
        return role.isEmpty() ? MemberRole.MEMBER : MemberRole.valueOf(role);
    }

    public static int getBackNumber(int id, Connection ... con) {
        return SqlUtils.getInt("back_number", TABLE, id, con);
    }
}
