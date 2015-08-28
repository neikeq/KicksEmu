package com.neikeq.kicksemu.network.packets.out;

import com.neikeq.kicksemu.game.characters.types.PlayerHistory;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.misc.quests.QuestState;
import com.neikeq.kicksemu.game.chat.MessageType;
import com.neikeq.kicksemu.game.clubs.ClubInfo;
import com.neikeq.kicksemu.game.clubs.MemberInfo;
import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.rooms.match.MatchResult;
import com.neikeq.kicksemu.game.rooms.match.PlayerResult;
import com.neikeq.kicksemu.game.servers.ServerType;
import com.neikeq.kicksemu.game.sessions.AuthResult;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.game.users.UserSettings;
import com.neikeq.kicksemu.network.packets.MessageId;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.storage.MySqlManager;
import com.neikeq.kicksemu.storage.SqlUtils;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.utils.GameEvents;
import com.neikeq.kicksemu.utils.Strings;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MessageBuilder {
    
    public static ServerMessage certifyLogin(int sessionId, int userId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CERTIFY_LOGIN);

        MessageUtils.appendResult(result, msg);

        if (result == AuthResult.SUCCESS) {
            msg.writeInt(sessionId);

            UserSettings settings = UserInfo.getSettings(userId);

            msg.writeByte(settings.getCamera());
            msg.writeBool(settings.getShadows());
            msg.writeByte(settings.getNames());
            msg.writeByte(settings.getVolEffects());
            msg.writeByte(settings.getVolMusic());
            msg.writeBool(settings.getInvites());
            msg.writeBool(settings.getWhispers());
            msg.writeInt(settings.getCountry());

            msg.writeString(DateUtils.dateToString(UserInfo.getLastCharDeletion(userId)), 19);
        } else {
            // Request the client to close the connection
            msg.setShort(0, (short) -1);
        }

        return msg;
    }

    public static ServerMessage instantLogin(int sessionId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.INSTANT_LOGIN);

        MessageUtils.appendResult(result, msg);

        msg.writeInt(sessionId);

        return msg;
    }

    public static ServerMessage instantExit() {
        ServerMessage msg = new ServerMessage(MessageId.INSTANT_EXIT);

        MessageUtils.appendResult((byte)0, msg);

        msg.writeZeros(2);

        // Request the client to close the connection
        msg.setShort(0, (short) -1);

        return msg;
    }

    public static ServerMessage certifyExit() {
        ServerMessage msg = new ServerMessage(MessageId.CERTIFY_EXIT);

        MessageUtils.appendResult((byte) 0, msg);

        // Request the client to close the connection
        msg.setShort(0, (short) -1);

        return msg;
    }

    public static ServerMessage characterInfo(int playerId, int ownerId,
                                              short slot, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.CHARACTER_INFO);

        boolean blocked = PlayerInfo.isBlocked(playerId, con);

        MessageUtils.appendResult((byte) (blocked ? -2 : 0), msg);

        if (!blocked) {
            msg.writeInt(ownerId);
            msg.writeInt(playerId);
            msg.writeShort(slot);
            msg.writeString(PlayerInfo.getName(playerId, con), 15);

            MessageUtils.appendQuestInfo(playerId, msg, con);
            MessageUtils.appendTutorialInfo(playerId, msg, con);

            msg.writeZeros(3);

            MessageUtils.appendCharacterInfo(playerId, msg, con);

            msg.writeZeros(2);
            msg.writeShort(PlayerInfo.getAnimation(playerId, con));
            msg.writeShort(PlayerInfo.getFace(playerId, con));

            MessageUtils.appendDefaultClothes(playerId, msg, con);

            msg.writeShort(PlayerInfo.getPosition(playerId, con));
            msg.writeZeros(6);

            MessageUtils.appendStats(playerId, msg, con);

            msg.writeZeros(4);

            MessageUtils.appendItemsInUse(playerId, msg, con);
        }

        return msg;
    }

    public static ServerMessage createCharacter(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CREATE_CHARACTER);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage choiceCharacter(int characterId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CHOICE_CHARACTER);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeInt(characterId);
        }

        return msg;
    }

    public static ServerMessage removeCharacter(int characterId, String date, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.REMOVE_CHARACTER);

        MessageUtils.appendResult(result, msg);

        msg.writeInt(characterId);
        msg.writeString(date, 20);

        return msg;
    }

    public static ServerMessage serverList(List<Short> servers, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.SERVER_LIST);

        MessageUtils.appendResult(result, msg);

        msg.writeShort((short) servers.size());

        for (short serverId : servers) {
            final String query = "SELECT name, online, max_users, connected_users, " +
                    "address, port FROM servers WHERE id = ? LIMIT 1";

            try (Connection con = MySqlManager.getConnection();
                 PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setInt(1, serverId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        msg.writeShort(serverId);
                        msg.writeString(rs.getString("name"), 30);
                        msg.writeBool(rs.getBoolean("online"));
                        msg.writeShort(rs.getShort("max_users"));
                        msg.writeShort(rs.getShort("connected_users"));
                        msg.writeString(rs.getString("address"), 16);
                        msg.writeShort(rs.getShort("port"));
                        msg.writeZeros(20);
                    }
                }
            } catch (SQLException ignored) {}
        }

        return msg;
    }

    public static ServerMessage serverInfo(short serverId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.SERVER_INFO);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            final String query = "SELECT type, min_level, max_level, address, port " +
                    "FROM servers WHERE id = ? LIMIT 1";

            try (Connection con = MySqlManager.getConnection();
                 PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setInt(1, serverId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        msg.writeShort(serverId);
                        msg.writeShort(ServerType.valueOf(rs.getString("type")).toShort());
                        msg.writeShort(rs.getShort("min_level"));
                        msg.writeShort(rs.getShort("max_level"));
                        msg.writeString(rs.getString("address"), 16);
                        msg.writeShort(rs.getShort("port"));
                    }
                }
            } catch (SQLException ignored) {}
        }

        return msg;
    }

    public static ServerMessage upgradeCharacter(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.UPGRADE_CHARACTER);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage updateTutorial(byte dribbling, byte passing, byte shooting,
                                               byte defense, int reward, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.UPDATE_TUTORIAL);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeByte(dribbling);
            msg.writeByte(passing);
            msg.writeByte(shooting);
            msg.writeByte(defense);
            msg.writeInt(reward);
        }

        return msg;
    }

    public static ServerMessage gameLogin(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.GAME_LOGIN);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage gameExit(InetSocketAddress clientIp, int characterId) {
        ServerMessage msg = new ServerMessage(MessageId.INSTANT_EXIT);

        MessageUtils.appendResult((byte)0, msg);

        msg.writeInt(characterId);
        msg.writeString(clientIp.getAddress().getHostAddress(), 16);
        msg.writeZeros(2);

        // Request the client to close the connection
        msg.setShort(0, (short) -1);

        return msg;
    }

    public static ServerMessage udpConfirm(boolean result) {
        ServerMessage msg = new ServerMessage(MessageId.UDP_CONFIRM);

        MessageUtils.appendResult((byte) (result ? 0 : -3), msg);

        return msg;
    }

    public static ServerMessage playerInfo(int playerId, byte result, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_INFO);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            int clubId = MemberInfo.getClubId(playerId, con);

            msg.writeInt(playerId);
            msg.writeZeros(54);
            msg.writeString(PlayerInfo.getName(playerId, con), 15);
            msg.writeString(ClubInfo.getName(clubId), 15);
            msg.writeString(PlayerInfo.getStatusMessage(playerId, con), 35);

            MessageUtils.appendQuestInfo(playerId, msg, con);
            MessageUtils.appendTutorialInfo(playerId, msg, con);

            msg.writeZeros(24);

            MessageUtils.appendCharacterInfo(playerId, msg, con);

            msg.writeZeros(2);
            msg.writeShort(PlayerInfo.getAnimation(playerId, con));
            msg.writeShort(PlayerInfo.getFace(playerId, con));

            MessageUtils.appendDefaultClothes(playerId, msg, con);

            msg.writeShort(PlayerInfo.getPosition(playerId, con));
            msg.writeZeros(6);

            // Stats
            MessageUtils.appendStats(playerId, msg, con);
            MessageUtils.appendStatsTraining(playerId, msg, con);
            MessageUtils.appendStatsBonus(playerId, msg, con);

            // History
            MessageUtils.appendHistory(playerId, msg, con);
            MessageUtils.appendHistoryMonth(playerId, msg, con);

            // Ranking
            MessageUtils.appendRanking(playerId, msg, con);
            MessageUtils.appendRankingLastMonth(playerId, msg, con);
            MessageUtils.appendInventoryItemsInUse(playerId, msg, con);
            MessageUtils.appendClubUniform(clubId, msg, con);
        }

        return msg;
    }

    public static ServerMessage itemList(Map<Integer, Item> items, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ITEM_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeShort((short) items.size());

            for (Item item : items.values()) {
                MessageUtils.appendInventoryItem(item, msg);
            }
        }

        return msg;
    }

    public static ServerMessage trainingList(Map<Integer, Training> trainings, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.TRAINING_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeShort((short) trainings.size());

            for (Training training : trainings.values()) {
                MessageUtils.appendInventoryTraining(training, msg);
            }
        }

        return msg;
    }

    public static ServerMessage skillList(Map<Integer, Skill> skills, byte slots, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.SKILL_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeByte(slots);
            msg.writeShort((short) skills.size());

            for (Skill skill : skills.values()) {
                MessageUtils.appendInventorySkill(skill, msg);
            }
        }

        return msg;
    }

    public static ServerMessage celebrationList(Map<Integer, Celebration> celebs, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CELEBRATION_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeShort((short) celebs.size());

            for (Celebration celebration : celebs.values()) {
                MessageUtils.appendInventoryCelebration(celebration, msg);
            }
        }

        return msg;
    }

    public static ServerMessage friendList(List<Integer> friends, byte page) {
        ServerMessage msg = new ServerMessage(MessageId.FRIENDS_LIST);

        MessageUtils.appendResult((byte)0, msg);

        msg.writeByte(page);

        for (int friendId : friends) {
            msg.writeInt(friendId);
            msg.writeString(PlayerInfo.getName(friendId), 15);
            msg.writeShort(PlayerInfo.getLevel(friendId));
            msg.writeByte((byte) PlayerInfo.getPosition(friendId));

            byte status;
            short server = 0;
            short location = 0;
            int userId = PlayerInfo.getOwner(friendId);

            if (!ServerManager.isPlayerConnected(friendId)) {
                server = UserInfo.getServer(userId);

                status = (byte)(server > 0 && UserInfo.getOnline(userId) == friendId ? 1 : 0);
            } else {
                status = 2;
            }

            switch (status) {
                case 1:
                    location = server;
                    break;
                case 2:
                    location = (short)ServerManager.getSessionById(friendId).getRoomId();
                    break;
                case 3:
                    location = (short)ServerManager.getSessionById(friendId).getRoomId();
                    break;
                default:
            }

            msg.writeByte(status);
            msg.writeShort(location);
        }

        return msg;
    }

    public static ServerMessage friendRequest(int playerId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.FRIEND_REQUEST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeInt(playerId);
            msg.writeString(PlayerInfo.getName(playerId), 15);
        }

        return msg;
    }

    public static ServerMessage friendResponse(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.FRIEND_RESPONSE);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage deleteFriend(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.DELETE_FRIEND);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage clubInfo(int playerId) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_INFO);

        MessageUtils.appendResult((byte) 0, msg);

        int clubId = MemberInfo.getClubId(playerId);

        msg.writeString(ClubInfo.getName(clubId), 15);
        msg.writeShort(ClubInfo.getMembersCount(clubId));
        msg.writeShort(ClubInfo.getMembersLimit(clubId));

        int manager = ClubInfo.getManager(clubId);
        msg.writeString(PlayerInfo.getName(manager), 15);

        List<Integer> captains = ClubInfo.getCaptains(clubId);

        for (int i = 0; i < 2; i++) {
            if (i >= captains.size()) {
                msg.writeZeros(15);
            } else {
                msg.writeString(PlayerInfo.getName(captains.get(i)), 15);
            }
        }

        msg.writeInt(clubId > 0 ? ClubInfo.getClubPoints(clubId) : 0);

        return msg;
    }

    public static ServerMessage clubMembers(int playerId, byte page) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_MEMBERS);

        MessageUtils.appendResult((byte) 0, msg);

        msg.writeByte(page);

        int clubId = MemberInfo.getClubId(playerId);

        List<Integer> membersForPage = ClubInfo.getMembers(clubId, page * 10, 10);

        membersForPage.forEach(memberId -> {
            msg.writeInt(memberId);
            msg.writeString(PlayerInfo.getName(memberId), 15);
            msg.writeShort(PlayerInfo.getLevel(memberId));
            msg.writeByte((byte) PlayerInfo.getPosition(memberId));

            byte status;
            short server = 0;
            short location = 0;
            int userId = PlayerInfo.getOwner(memberId);

            if (!ServerManager.isPlayerConnected(memberId)) {
                server = UserInfo.getServer(userId);

                status = (byte) (server > 0 && UserInfo.getOnline(userId) == memberId ? 1 : 0);
            } else {
                status = 2;
            }

            switch (status) {
                case 1:
                    location = server;
                    break;
                case 2:
                    location = (short) ServerManager.getSessionById(memberId).getRoomId();
                    break;
                case 3:
                    location = (short) ServerManager.getSessionById(memberId).getRoomId();
                    break;
                default:
            }

            msg.writeByte(status);
            msg.writeShort(location);
        });

        return msg;
    }

    public static ServerMessage ignoredList(List<Integer> ignoredPlayers, byte page) {
        ServerMessage msg = new ServerMessage(MessageId.IGNORED_LIST);

        MessageUtils.appendResult((byte)0, msg);

        msg.writeByte(page);

        for (int playerId : ignoredPlayers) {
            msg.writeInt(playerId);
            msg.writeString(PlayerInfo.getName(playerId), 15);
            msg.writeByte((byte) PlayerInfo.getPosition(playerId));
        }

        return msg;
    }

    public static ServerMessage blockPlayer(int playerId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.BLOCK_PLAYER);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeInt(playerId);
            msg.writeString(PlayerInfo.getName(playerId), 15);
        }

        return msg;
    }

    public static ServerMessage unblockPlayer(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.UNBLOCK_PLAYER);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage statusMessage(String statusMessage, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.STATUS_MESSAGE);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeString(statusMessage, statusMessage.length());
        }

        return msg;
    }

    public static ServerMessage roomList(Map<Integer, Room> rooms, short page, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeShort(page);

            for (Room room : rooms.values()) {
                msg.writeByte((byte) room.getAccessType().toInt());
                msg.writeBool(room.isPlaying());
                msg.writeShort((short) room.getId());
                msg.writeString(room.getName(), 46);
                msg.writeByte(room.getMinLevel());
                msg.writeByte(room.getMaxLevel());
                msg.writeByte((byte) room.getMaxSize().toInt());
                msg.writeByte(room.getCurrentSize());

                // Red team positions

                int i = 0;

                for (short position : room.getRedTeamPositions()) {
                    msg.writeByte((byte) position);
                    i++;
                }

                // Fill the remain spaces if red team is not full
                msg.writeZeros(5 - i);

                // Blue team positions

                i = 0;

                for (short position : room.getBlueTeamPositions()) {
                    msg.writeByte((byte) position);
                    i++;
                }

                // Fill the remain spaces if blue team is not full
                msg.writeZeros(5 - i);
            }
        }

        return msg;
    }

    public static ServerMessage createRoom(short roomId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CREATE_ROOM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeShort(roomId);
        }

        return msg;
    }

    public static ServerMessage joinRoom(Room room, int playerId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.JOIN_ROOM);

        MessageUtils.appendResult(result, msg);

        if (result == 0 && room != null) {
            msg.writeShort((short) room.getId());

            RoomTeam team = room.getPlayerTeam(playerId);
            short teamIndex = team != null ? (short)team.toInt() : -1;
            msg.writeShort(teamIndex);
            msg.writeShort(teamIndex);
        }

        return msg;
    }

    public static ServerMessage quickJoinRoom(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.QUICK_JOIN_ROOM);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage nextTip(String tip, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.NEXT_TIP);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeBool(GameEvents.isGoldenTime());
            msg.writeBool(GameEvents.isClubTime());

            msg.writeString(tip, tip.length() > 120 ? 120 : tip.length());
        }

        return msg;
    }

    public static ServerMessage roomInfo(Room room) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_INFO);

        MessageUtils.appendResult((byte) 0, msg);

        if (room != null) {
            msg.writeByte((byte) room.getAccessType().toInt());
            msg.writeShort((short) room.getId());
            msg.writeString(room.getName(), 45);
            msg.writeString(room.getPassword(), 5);
            msg.writeInt(room.getMaster());
            msg.writeByte((byte) room.getRoomMode().toInt());
            msg.writeByte(room.getMinLevel());
            msg.writeByte(room.getMaxLevel());
            msg.writeShort((short) room.getMap().toInt());
            msg.writeShort((short) room.getBall().toInt());
            msg.writeByte((byte) room.getMaxSize().toInt());
        }

        return msg;
    }

    public static ServerMessage roomPlayerInfo(Session session, Room room, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_PLAYER_INFO);

        if (session != null) {
            int playerId = session.getPlayerId();
            int ownerId = PlayerInfo.getOwner(playerId, con);
            int clubId = MemberInfo.getClubId(playerId, con);

            RoomTeam team = room.getPlayerTeam(playerId);
            short teamIndex = team != null ? (short)team.toInt() : -1;

            msg.writeBool(true, 2);
            msg.writeInt(playerId);
            msg.writeString(PlayerInfo.getName(playerId, con), 15);
            msg.writeString(ClubInfo.getName(clubId, con), 15);
            msg.writeShort(teamIndex);
            msg.writeBool(room.isObserver(playerId));
            msg.writeBool(false); // pc room
            msg.writeBool(false);
            msg.writeShort((short) UserInfo.getSettings(ownerId).getCountry());
            msg.writeInt(session.getPingRay());
            msg.writeString(session.getRemoteAddress().getAddress().getHostAddress(), 16);
            msg.writeShort((short) session.getUdpPort());

            MessageUtils.appendCharacterInfo(playerId, msg, con);

            msg.writeZeros(2);
            msg.writeShort(PlayerInfo.getAnimation(playerId, con));
            msg.writeShort(PlayerInfo.getFace(playerId, con));

            MessageUtils.appendDefaultClothes(playerId, msg, con);

            msg.writeShort(PlayerInfo.getPosition(playerId, con));
            msg.writeZeros(1);
            msg.writeBool(clubId > 0); // is club member
            msg.writeByte((byte) MemberInfo.getRole(playerId, con).toInt());
            msg.writeZeros(3);
            msg.writeByte((byte) 0);
            msg.writeByte((byte) 0);
            msg.writeByte((byte) 0);

            // Stats
            MessageUtils.appendStats(playerId, msg, con);
            MessageUtils.appendStatsTraining(playerId, msg, con);
            MessageUtils.appendStatsBonus(playerId, msg, con);

            MessageUtils.appendInventoryItemsInUse(playerId, msg, con);
            MessageUtils.appendClubUniform(clubId, msg, con);
            MessageUtils.appendInventorySkillsInUse(playerId, msg, con);
            MessageUtils.appendInventoryCelebrationsInUse(playerId, msg, con);
        }

        return msg;
    }

    public static ServerMessage leaveRoom(int playerId, RoomLeaveReason reason) {
        ServerMessage msg = new ServerMessage(MessageId.LEAVE_ROOM);

        msg.writeShort(reason.toShort());
        msg.writeInt(playerId);

        return msg;
    }

    public static ServerMessage roomMaster(int playerId) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_MASTER);

        msg.writeZeros(2);
        msg.writeInt(playerId);

        return msg;
    }

    public static ServerMessage swapTeam(int playerId, RoomTeam newTeam) {
        ServerMessage msg = new ServerMessage(MessageId.SWAP_TEAM);

        MessageUtils.appendResult((byte)0, msg);

        msg.writeInt(playerId);
        msg.writeZeros(2);
        msg.writeShort((short) newTeam.toInt());

        return msg;
    }

    public static ServerMessage roomMap(short map) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_MAP);

        MessageUtils.appendResult((byte) 0, msg);

        msg.writeShort(map);

        return msg;
    }

    public static ServerMessage roomBall(short ball) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_BALL);

        MessageUtils.appendResult((byte) 0, msg);

        msg.writeShort(ball);

        return msg;
    }

    public static ServerMessage roomSettings(Room room, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_SETTINGS);

        MessageUtils.appendResult(result, msg);

        if (room != null) {
            msg.writeShort((short) room.getAccessType().toInt());
            msg.writeString(room.getName(), 45);
            msg.writeString(room.getPassword(), 4);
            msg.writeZeros(1);
            msg.writeByte((byte) room.getRoomMode().toInt());
            msg.writeByte(room.getMinLevel());
            msg.writeByte(room.getMaxLevel());
            msg.writeByte((byte) room.getMaxSize().toInt());
        }

        return msg;
    }

    public static ServerMessage kickPlayer(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.KICK_PLAYER);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage lobbyList(Integer[] players, byte page,
                                          byte result, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.LOBBY_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeByte(page);

            try {
                Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

                String array = Strings.repeatAndSplit("?", ", ", players.length);

                final String query = "SELECT name, level, position, status_message" +
                        " FROM characters WHERE" +
                        " id IN(" + array + ") ORDER BY FIELD(id, " + array + ")";

                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    SqlUtils.repeatSetInt(stmt, players);
                    SqlUtils.repeatSetInt(stmt, players.length + 1, players);

                    try (ResultSet rs = stmt.executeQuery()) {
                        for (int i = 0; rs.next(); i++) {
                            msg.writeBool(true);
                            msg.writeInt(players[i]);
                            msg.writeString(rs.getString("name"), 15);
                            msg.writeShort(rs.getShort("level"));
                            msg.writeByte((byte) rs.getShort("position"));
                            msg.writeString(rs.getString("status_message"), 35);
                        }
                    }
                } finally {
                    if (con.length <= 0) {
                        connection.close();
                    }
                }
            } catch (SQLException ignored) {
                System.out.println(ignored.getMessage());
            }
        }

        return msg;
    }

    public static ServerMessage invitePlayer(byte result, Room room, String name) {
        ServerMessage msg = new ServerMessage(MessageId.INVITE_PLAYER);

        MessageUtils.appendResult(result, msg);

        if (result == 0 && room != null) {
            msg.writeString(name, 15);
            msg.writeShort((short) room.getId());
            msg.writeString(room.getPassword(), 5);
        }

        return msg;
    }

    public static ServerMessage chatMessage(MessageType messageType, String message) {
        return chatMessage(0, "", messageType, message);
    }

    public static ServerMessage chatMessage(int playerId, String name,
                                            MessageType messageType, String message) {
        ServerMessage msg = new ServerMessage(MessageId.CHAT_MESSAGE);

        MessageUtils.appendResult((byte) 0, msg);

        msg.writeInt(playerId);
        msg.writeString(name, 15);
        msg.writeByte((byte) messageType.toInt());
        msg.writeString(message, message.length());

        return msg;
    }

    public static ServerMessage setObserver(int playerId, boolean observer) {
        ServerMessage msg = new ServerMessage(MessageId.SET_OBSERVER);

        MessageUtils.appendResult((byte) 0, msg);

        msg.writeInt(playerId);
        msg.writeBool(observer, 2);

        return msg;
    }

    public static ServerMessage startCountDown(byte type) {
        ServerMessage msg = new ServerMessage(MessageId.START_COUNT_DOWN);

        MessageUtils.appendResult((byte) 0, msg);

        msg.writeByte(type);

        return msg;
    }

    public static ServerMessage hostInfo(Room room) {
        ServerMessage msg = new ServerMessage(MessageId.HOST_INFO);

        MessageUtils.appendResult((byte) 0, msg);

        Session session = ServerManager.getSessionById(room.getHost());

        msg.writeInt(room.getHost());
        msg.writeString(session.getRemoteAddress().getAddress().getHostAddress(), 16);
        msg.writeShort((short) session.getUdpPort());
        msg.writeBool(room.isTraining());
        msg.writeZeros(4);

        byte hostIndex = (byte)(room.getPlayerTeam(room.getHost()) == RoomTeam.RED ?
                room.getRedTeam() : room.getBlueTeam()).indexOf(room.getHost());

        msg.writeInt(room.getHost());
        msg.writeByte(hostIndex);

        room.getPlayers().keySet().stream()
                .filter(playerId -> playerId != room.getHost())
                .forEach(playerId -> {
                    byte playerIndex = (byte) (room.getPlayerTeam(playerId) == RoomTeam.RED ?
                            room.getRedTeam() : room.getBlueTeam()).indexOf(playerId);

                    msg.writeInt(playerId);
                    msg.writeByte(playerIndex);
                });

        return msg;
    }

    public static ServerMessage countDown(short count) {
        ServerMessage msg = new ServerMessage(MessageId.COUNT_DOWN);

        msg.writeShort(count);

        return msg;
    }

    public static ServerMessage cancelCountDown() {
        ServerMessage msg = new ServerMessage(MessageId.CANCEL_COUNT_DOWN);

        MessageUtils.appendResult((byte) 0, msg);

        return msg;
    }

    public static ServerMessage matchLoading(int playerId, int roomId, short status) {
        ServerMessage msg = new ServerMessage(MessageId.MATCH_LOADING);

        msg.writeInt(playerId);
        msg.writeShort((short) roomId);
        msg.writeShort(status);

        return msg;
    }

    public static ServerMessage playerReady(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_READY);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage cancelLoading() {
        ServerMessage msg = new ServerMessage(MessageId.CANCEL_LOADING);

        MessageUtils.appendResult((byte) 0, msg);

        return msg;
    }

    public static ServerMessage startMatch(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.START_MATCH);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage matchResult(MatchResult result, PlayerResult playerResult,
                                            Room room, Connection con) {
        ServerMessage msg = new ServerMessage(MessageId.MATCH_RESULT);

        MessageUtils.appendResult((byte) 0, msg);

        if (result != null && playerResult != null && room != null) {
            msg.writeInt(result.getMom());

            result.getRedTeam().appendResult(msg);
            result.getBlueTeam().appendResult(msg);

            result.getPlayers().stream().forEach(pr -> pr.appendResult(msg));
            msg.writeZeros(40 * (10 - result.getPlayers().size()));

            msg.writeShort(result.getCountdown());
            msg.writeBool(result.isGoldenTime());
            msg.writeBool(result.isExperience());
            msg.writeBool(result.isExperience()); // Point

            msg.writeInt(playerResult.getExperience());
            msg.writeInt(playerResult.getPoints());

            MessageUtils.appendMatchHistory(playerResult, room, result, msg, con);
        }

        return msg;
    }

    public static ServerMessage unknown1() {
        ServerMessage msg = new ServerMessage(MessageId.UNKNOWN1);

        MessageUtils.appendResult((byte) 0, msg);

        return msg;
    }

    public static ServerMessage unknown2() {
        ServerMessage msg = new ServerMessage(MessageId.UNKNOWN2);

        MessageUtils.appendResult((byte) 0, msg);

        return msg;
    }

    public static ServerMessage updateRoomPlayer(int playerId, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.UPDATE_ROOM_PLAYER);

        MessageUtils.appendResult((byte) 0, msg);

        msg.writeInt(playerId);
        msg.writeBool(true);

        MessageUtils.appendCharacterInfo(playerId, msg, con);
        MessageUtils.appendStats(playerId, msg, con);

        return msg;
    }

    public static ServerMessage playerBonusStats(int playerId, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_BONUS_STATS);

        MessageUtils.appendResult((byte) 0, msg);

        msg.writeInt(playerId);

        MessageUtils.appendStatsBonus(playerId, msg, con);

        return msg;
    }

    public static ServerMessage clubRoomList(Map<Integer, Room> rooms, short page, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_ROOM_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeShort(page);

            for (Room room : rooms.values()) {
                msg.writeByte((byte) room.getAccessType().toInt());
                msg.writeBool(room.isPlaying());
                msg.writeShort((short) room.getId());
                msg.writeString(room.getName(), 15);
                msg.writeByte((byte) room.getMaxSize().toInt());
                msg.writeByte(room.getCurrentSize());

                // Red team positions

                int i = 0;

                for (short position : room.getRedTeamPositions()) {
                    msg.writeByte((byte) position);
                    i++;
                }

                // Fill the remain spaces if red team is not full
                msg.writeZeros(5 - i);
            }
        }

        return msg;
    }

    public static ServerMessage clubCreateRoom(short roomId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_CREATE_ROOM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeShort(roomId);
        }

        return msg;
    }

    public static ServerMessage clubJoinRoom(Room room, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_JOIN_ROOM);

        MessageUtils.appendResult(result, msg);

        if (result == 0 && room != null) {
            msg.writeShort((short) room.getId());
        }

        return msg;
    }

    public static ServerMessage clubLeaveRoom(int playerId, RoomLeaveReason reason) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_LEAVE_ROOM);

        msg.writeShort(reason.toShort());
        msg.writeInt(playerId);

        return msg;
    }

    public static ServerMessage clubRoomCaptain(int playerId) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_ROOM_CAPTAIN);

        msg.writeZeros(2);
        msg.writeInt(playerId);

        return msg;
    }

    public static ServerMessage clubRoomInfo(Room room) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_ROOM_INFO);

        MessageUtils.appendResult((byte) 0, msg);

        if (room != null) {
            msg.writeBool(false);
            msg.writeByte((byte) room.getAccessType().toInt());
            msg.writeShort((short) room.getId());
            msg.writeString(room.getName(), 45);
            msg.writeString(room.getPassword(), 5);
            msg.writeInt(room.getMaster());
            msg.writeByte((byte) room.getRoomMode().toInt());
            msg.writeShort((short) 0); // wins
        }

        return msg;
    }

    public static ServerMessage clubRoomPlayerInfo(Session session, Room room, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_ROOM_PLAYER_INFO);

        if (session != null) {
            int playerId = session.getPlayerId();
            int ownerId = PlayerInfo.getOwner(playerId, con);
            int clubId = MemberInfo.getClubId(playerId, con);

            msg.writeShort((short) 1);
            msg.writeBool(false);
            msg.writeInt(playerId);
            msg.writeString(PlayerInfo.getName(playerId, con), 15);
            msg.writeString(ClubInfo.getName(clubId, con), 15);
            msg.writeBool(room.isObserver(playerId));
            msg.writeBool(false); // pc room
            msg.writeBool(false);
            msg.writeShort((short) UserInfo.getSettings(ownerId).getCountry());
            msg.writeString(session.getRemoteAddress().getAddress().getHostAddress(), 16);
            msg.writeShort((short) session.getUdpPort());

            MessageUtils.appendCharacterInfo(playerId, msg, con);

            msg.writeZeros(2);
            msg.writeShort(PlayerInfo.getAnimation(playerId, con));
            msg.writeShort(PlayerInfo.getFace(playerId, con));

            MessageUtils.appendDefaultClothes(playerId, msg, con);

            msg.writeShort(PlayerInfo.getPosition(playerId, con));
            msg.writeZeros(1);
            msg.writeBool(clubId > 0); // is club member
            msg.writeByte((byte) MemberInfo.getRole(playerId, con).toInt());
            msg.writeZeros(3);
            msg.writeByte((byte) 0);
            msg.writeByte((byte) 0);
            msg.writeByte((byte) 0);

            // Stats
            MessageUtils.appendStats(playerId, msg, con);
            MessageUtils.appendStatsTraining(playerId, msg, con);
            MessageUtils.appendStatsBonus(playerId, msg, con);

            MessageUtils.appendInventoryItemsInUse(playerId, msg, con);
            MessageUtils.appendClubUniform(clubId, msg, con);
            MessageUtils.appendInventorySkillsInUse(playerId, msg, con);
            MessageUtils.appendInventoryCelebrationsInUse(playerId, msg, con);
        }

        return msg;
    }

    public static ServerMessage clubRoomSettings(Room room, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_ROOM_SETTINGS);

        MessageUtils.appendResult(result, msg);

        if (room != null) {
            msg.writeShort((short) room.getAccessType().toInt());
            msg.writeString(room.getName(), 15);
            msg.writeString(room.getPassword(), 4);
        }

        return msg;
    }

    public static ServerMessage clubInvitePlayer(byte result, Room room, String name) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_INVITE_PLAYER);

        MessageUtils.appendResult(result, msg);

        if (result == 0 && room != null) {
            msg.writeString(name, 15);
            msg.writeShort((short) room.getId());
            msg.writeString(room.getPassword(), 5);
        }

        return msg;
    }

    public static ServerMessage purchaseItem(int playerId, byte result, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.PURCHASE_ITEM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendCharacterInfo(playerId, msg, con);
            MessageUtils.appendStatsBonus(playerId, msg, con);
            MessageUtils.appendInventoryItemsInUse(playerId, msg, con);
            msg.writeByte(PlayerInfo.getSkillSlots(playerId, con));
        }

        return msg;
    }

    public static ServerMessage resellItem(int playerId, int inventoryId, int refund,
                                           byte result, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.RESELL_ITEM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendCharacterInfo(playerId, msg, con);
            MessageUtils.appendStatsBonus(playerId, msg, con);
            MessageUtils.appendInventoryItemsInUse(playerId, msg, con);
            msg.writeByte(PlayerInfo.getSkillSlots(playerId, con));
            msg.writeZeros(3);
            msg.writeInt(refund);
            msg.writeInt(inventoryId);
        }

        return msg;
    }

    public static ServerMessage activateItem(int inventoryId, int playerId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ACTIVATE_ITEM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendStatsBonus(playerId, msg);
            MessageUtils.appendInventoryItemsInUse(playerId, msg);
            msg.writeInt(inventoryId);
        }

        return msg;
    }

    public static ServerMessage deactivateItem(int inventoryId, int playerId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.DEACTIVATE_ITEM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendStatsBonus(playerId, msg);
            MessageUtils.appendInventoryItemsInUse(playerId, msg);
            msg.writeInt(inventoryId);
        }

        return msg;
    }

    public static ServerMessage mergeItem(int playerId, int inventoryId,
                                          short usages, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.MERGE_ITEM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendStatsBonus(playerId, msg);
            msg.writeInt(inventoryId);
            msg.writeShort(usages);
        }

        return msg;
    }

    public static ServerMessage purchaseLearn(int playerId, Training learn,
                                              byte result, Connection con) {
        ServerMessage msg = new ServerMessage(MessageId.PURCHASE_LEARN);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendCharacterInfo(playerId, msg, con);
            MessageUtils.appendStatsTraining(playerId, msg, con);
            MessageUtils.appendInventoryTraining(learn, msg);
        }

        return msg;
    }

    public static ServerMessage purchaseSkill(int playerId, Skill skill,
                                              byte result, Connection con) {
        ServerMessage msg = new ServerMessage(MessageId.PURCHASE_SKILL);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendCharacterInfo(playerId, msg, con);
            MessageUtils.appendInventorySkill(skill, msg);
        }

        return msg;
    }

    public static ServerMessage activateSkill(int inventoryId, byte index, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ACTIVATE_SKILL);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeInt(inventoryId);
            msg.writeByte(index);
        }

        return msg;
    }

    public static ServerMessage deactivateSkill(int skillId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.DEACTIVATE_SKILL);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeInt(skillId);
        }

        return msg;
    }

    public static ServerMessage purchaseCele(int playerId, Celebration cele,
                                             byte result, Connection con) {
        ServerMessage msg = new ServerMessage(MessageId.PURCHASE_CELE);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendCharacterInfo(playerId, msg, con);
            MessageUtils.appendInventoryCelebration(cele, msg);
        }

        return msg;
    }

    public static ServerMessage activateCele(int inventoryId, byte index, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ACTIVATE_CELE);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeInt(inventoryId);
            msg.writeByte(index);
        }

        return msg;
    }

    public static ServerMessage deactivateCele(int inventoryId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.DEACTIVATE_CELE);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeInt(inventoryId);
        }

        return msg;
    }

    public static ServerMessage tcpPing() {
        return new ServerMessage(MessageId.TCP_PING);
    }

    public static ServerMessage updateSettings(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.UPDATE_SETTINGS);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage playerDetails(int playerId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_DETAILS);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.writeInt(playerId);
            msg.writeString(PlayerInfo.getName(playerId), 15);
            msg.writeShort(PlayerInfo.getPosition(playerId));
            msg.writeShort(PlayerInfo.getLevel(playerId));
            msg.writeString(ClubInfo.getName(MemberInfo.getClubId(playerId)), 15);

            PlayerHistory history = PlayerInfo.getHistory(playerId);
            PlayerHistory monthHistory = PlayerInfo.getMonthHistory(playerId);

            int historyLoses = history.getMatches() - (history.getWins() + history.getDraws());
            short historyMonthLoses = (short) (monthHistory.getMatches() -
                    (monthHistory.getWins() + monthHistory.getDraws()));

            // Matches history
            msg.writeInt(history.getWins());
            msg.writeInt(history.getDraws());
            msg.writeInt(historyLoses);
            msg.writeShort((short) monthHistory.getWins());
            msg.writeShort((short) monthHistory.getDraws());
            msg.writeShort(historyMonthLoses);

            // TODO Fix History and Last Month History writing to PlayerDetails message

            // History
            msg.writeShort((short) history.getMatches());
            msg.writeShort((short) history.getWins());
            msg.writeShort((short) history.getDraws());
            msg.writeShort((short) history.getMom());
            msg.writeShort((short) history.getValidGoals());
            msg.writeShort((short) history.getValidAssists());
            msg.writeShort((short) history.getValidInterception());
            msg.writeShort((short) history.getValidShooting());
            msg.writeShort((short) history.getValidStealing());
            msg.writeShort((short) history.getValidTackling());
            msg.writeZeros(2);
            msg.writeShort((short) history.getShooting());
            msg.writeShort((short) history.getStealing());
            msg.writeShort((short) history.getTackling());
            msg.writeZeros(2);
            msg.writeShort((short) history.getTotalPoints());

            // History Last Month
            msg.writeShort((short) monthHistory.getMatches());
            msg.writeShort((short) monthHistory.getWins());
            msg.writeShort((short) monthHistory.getDraws());
            msg.writeShort((short) monthHistory.getMom());
            msg.writeShort((short) monthHistory.getValidGoals());
            msg.writeShort((short) monthHistory.getValidAssists());
            msg.writeShort((short) monthHistory.getValidInterception());
            msg.writeShort((short) monthHistory.getValidShooting());
            msg.writeShort((short) monthHistory.getValidStealing());
            msg.writeShort((short) monthHistory.getValidTackling());
            msg.writeZeros(2);
            msg.writeShort((short) monthHistory.getShooting());
            msg.writeShort((short) monthHistory.getStealing());
            msg.writeShort((short) monthHistory.getTackling());
            msg.writeZeros(2);
            msg.writeShort((short) monthHistory.getTotalPoints());

            // TODO Add club info
        }

        return msg;
    }

    public static ServerMessage addStatsPoints(int playerId, byte result, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.ADD_STATS_POINTS);

        MessageUtils.appendResult(result, msg);

        msg.writeInt(playerId);

        msg.writeShort(PlayerInfo.getStatsPoints(playerId));
        MessageUtils.appendStats(playerId, msg, con);

        return msg;
    }

    public static ServerMessage playerProgress(int playerId,
                                               short finishedQuest, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_PROGRESS);

        msg.writeShort((short) 1);

        QuestState questState = PlayerInfo.getQuestState(playerId, con);

        short currentQuest = questState.getCurrentQuest();
        short remainMatches = questState.getRemainMatches();

        // By using the value -1 of remain matched with a finished quest,
        // the result screen will display 'Quest X completed'.
        if (finishedQuest > 0) {
            currentQuest = finishedQuest;
            remainMatches = -1;
        } else if (currentQuest > 3) {
            currentQuest = 3;
            remainMatches = -1;
        }

        msg.writeShort(currentQuest);
        msg.writeShort(remainMatches);
        msg.writeInt(PlayerInfo.getPoints(playerId, con));
        msg.writeInt(PlayerInfo.getLevel(playerId, con));

        return msg;
    }

    public static ServerMessage playerStats(int playerId, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_STATS);

        MessageUtils.appendResult((byte) 0, msg);

        msg.writeShort(PlayerInfo.getStatsPoints(playerId, con));
        MessageUtils.appendStats(playerId, msg, con);

        return msg;
    }

    public static ServerMessage udpPing() {
        return new ServerMessage(MessageId.UDP_PING);
    }
}
